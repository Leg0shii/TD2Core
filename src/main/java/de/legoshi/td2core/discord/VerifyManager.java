package de.legoshi.td2core.discord;

import de.legoshi.td2core.TD2Core;
import de.legoshi.td2core.util.Message;
import lombok.Setter;
import net.dv8tion.jda.api.entities.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class VerifyManager {
    
    @Setter private RoleManager roleManager;
    
    // mc-uuid/discordid
    private final HashMap<String, String> verifiedUsers;
    
    // key/mc-uuid
    private final HashMap<String, String> toBeVerified;
    
    public VerifyManager() {
        verifiedUsers = new HashMap<>();
        toBeVerified = new HashMap<>();
    }
    
    public void loadVerifiedUsers() {
        Bukkit.getScheduler().runTaskAsynchronously(TD2Core.getInstance(), () -> {
            try {
                String sqlQuery = "SELECT * FROM discord_data";
                PreparedStatement preparedStatement = TD2Core.sql().prepare(sqlQuery);
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    String mcUUID = resultSet.getString("mcuuid");
                    String discordUUID = resultSet.getString("discorduuid");
                    verifiedUsers.put(mcUUID, discordUUID);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
    
    public String checkVerify(User user, String randomID) {
        String mcUUID = toBeVerified.getOrDefault(randomID, null);
        if (verifiedUsers.containsKey(mcUUID)) {
            return Message.ALREADY_VERIFIED.getMessage();
        }
        
        if (mcUUID == null) {
            return Message.VERIFY_ERROR.getMessage();
        }
        
        // send to DB
        Bukkit.getScheduler().runTaskAsynchronously(TD2Core.getInstance(), () -> {
            try {
                String sqlQuery = "INSERT INTO discord_data (discorduuid, mcuuid) VALUES (?, ?)";
                PreparedStatement preparedStatement = TD2Core.sql().prepare(sqlQuery);
                preparedStatement.setString(1, user.getId());
                preparedStatement.setString(2, mcUUID);
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        
        removeVerify(randomID);
        
        Bukkit.getScheduler().runTaskLaterAsynchronously(TD2Core.getInstance(), () -> {
            roleManager.firstUserVerify(mcUUID);
            roleManager.addVerifyRole(user);
            roleManager.checkPercentageRole(mcUUID);
            verifiedUsers.put(mcUUID, user.getId());
        }, 20L);
        return Message.VERIFY_SUCCESS.getMessage();
    }
    
    public String addVerify(String playerUUID, String randomID) {
        if (toBeVerified.containsValue(playerUUID)) {
            return Message.ALREADY_CREATED.getWarningMessage();
        }
        
        toBeVerified.put(randomID, playerUUID);
        Bukkit.getScheduler().runTaskLaterAsynchronously(TD2Core.getInstance(), () -> removeVerify(randomID), 20L *60 * 5);
        return Message.VERIFY_START.getMessage(randomID);
    }
    
    private void removeVerify(String key) {
        toBeVerified.remove(key);
    }
    
    public boolean isVerified(Player player) {
        return verifiedUsers.containsKey(player.getUniqueId().toString());
    }
}
