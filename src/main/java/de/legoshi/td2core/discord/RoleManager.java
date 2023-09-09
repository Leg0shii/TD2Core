package de.legoshi.td2core.discord;

import de.legoshi.td2core.TD2Core;
import de.legoshi.td2core.discord.progress.ProgressManager;
import de.legoshi.td2core.discord.progress.ProgressMap;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class RoleManager {
    
    private final ProgressManager progressManager;
    @Setter private Guild guild;
    
    public void firstUserVerify(String mcUserID) {
        List<ProgressMap> maps = progressManager.getAllProgressMaps();
        for (ProgressMap map : maps) {
            assignPlayerRole(map, mcUserID);
        }
    }
    
    public void cpRoleCheck(String mapName, String mcUserID) {
        List<ProgressMap> maps = progressManager.getAllProgressMaps();
        ProgressMap progressMap = maps
            .stream()
            .filter(m -> m.getMapName().equals(mapName))
            .findFirst()
            .orElse(null);
        
        if (progressMap == null) {
            System.err.println("Couldnt find map: " + mapName + " for user: " + mcUserID + " in progress manager");
            return;
        }
        
        assignPlayerRole(progressMap, mcUserID);
    }
    
    public void addVerifyRole(User user) {
        Role verifiedRole = guild.getRolesByName("verified", true).get(0);
        guild.addRoleToMember(user, verifiedRole).queue();
    }
    
    private void assignPlayerRole(ProgressMap progressMap, String mcUserID) {
        try {
            String sqlQuery =
                "SELECT p.userid, p.mapname, d.discorduuid, p.cp_count, p.passed " +
                    "FROM player_log AS p " +
                    "JOIN discord_data AS d ON p.userid = d.mcuuid " +
                    "WHERE p.userid = ? AND p.mapname = ?;";
            
            PreparedStatement preparedStatement = TD2Core.sql().prepare(sqlQuery);
            preparedStatement.setString(1, mcUserID);
            preparedStatement.setString(2, progressMap.getMapName());
            ResultSet resultSet = preparedStatement.executeQuery();
            
            if (resultSet.next()) {
                String discordUserID = resultSet.getString("discorduuid");
                int cpCount = resultSet.getInt("cp_count");
                String roleID = progressMap.getPlayerRole(cpCount);
                if (roleID == null) {
                    return;
                }
                
                Role highestRole = guild.getRolesByName(roleID, true).get(0);
                if (resultSet.getBoolean("passed")) {
                    highestRole = guild.getRolesByName(progressMap.getVictorRole(), true).get(0);
                }
                
                if (highestRole == null) {
                    return;
                }
    
                Role finalHighestRole = highestRole;
                guild.retrieveMemberById(discordUserID).queue(member -> {
                    List<Role> userRoles = member.getRoles();
                    List<Role> allMapRoles = progressMap.getCpToRole()
                        .values()
                        .stream()
                        .map(name -> guild.getRolesByName(name, true).get(0))
                        .collect(Collectors.toList());
    
                    if (!userRoles.contains(finalHighestRole)) {
                        guild.addRoleToMember(member, finalHighestRole).queue();
                        for (Role role : allMapRoles) {
                            if (userRoles.contains(role) && role != finalHighestRole) {
                                guild.removeRoleFromMember(member, role).queue();
                            }
                        }
                    }
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
}
