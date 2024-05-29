package de.legoshi.td2core.discord.progress;

import de.legoshi.td2core.TD2Core;
import de.legoshi.td2core.util.Utils;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class ProgressChannel {
    
    @Getter private final String channelID;
    @Getter private final String topHeader = "**=================================**";
    @Getter private final String bottomHeader = "**=================================**";
    @Getter private final List<ProgressMap> progressMaps = new ArrayList<>();
    private final Guild guild;
    private String query;
    
    public ProgressChannel(String channelID, Guild guild) {
        this.channelID = channelID;
        this.guild = guild;
    }
    
    public List<String> getFullProgress() {
        List<String> progressStringList = new ArrayList<>();
        for (ProgressMap progressMap : progressMaps) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder
                .append(getTitleString(progressMap))
                .append(getProgressString(progressMap))
                .append(getCompletionString(progressMap));
            progressStringList.add(stringBuilder.toString());
        }
        return progressStringList;
    }
    
    protected String getTitleString(ProgressMap progressMap) {
        if (progressMap.getProgressLines().isEmpty()) return "";
        return topHeader + "\n**" + progressMap.getMapName() + "**\n" + bottomHeader + "\n";
    }
    
    protected String getProgressString(ProgressMap progressMap) {
        String mapName = progressMap.getMapName();
        StringBuilder progressString = new StringBuilder();
        if (progressMap.getProgressLines().isEmpty()) return "";
        
        try {
            String sqlQuery =
                "SELECT userid, MAX(cp_count) AS cpCount " +
                    "FROM player_log " +
                    "WHERE mapname = ? " +
                    "AND userid NOT IN (" +
                    "SELECT DISTINCT userid FROM player_log WHERE mapname = ? AND passed = 1" +
                    ") " +
                    "GROUP BY userid " +
                    "HAVING MAX(cp_count) >= 1 " +
                    "ORDER BY cpCount DESC;";
            
            PreparedStatement preparedStatement = TD2Core.sql().prepare(sqlQuery);
            preparedStatement.setString(1, mapName);
            preparedStatement.setString(2, mapName);
            
            ResultSet resultSet = preparedStatement.executeQuery();
            List<String> progressLines = new ArrayList<>(progressMap.getProgressLines());
            
            while (resultSet.next()) {
                // don't replace with retrievePlayerName - it adds additional white spaces
                String playerName = Utils.getPlayerNameByUUID(resultSet.getString("userid"));
                playerName= playerName.replace("_", "\\_");
                
                int playerCPCount = resultSet.getInt("cpCount");
                int lineIndex = progressMap.getProgressPosition(playerCPCount);
                if (lineIndex == -1) continue;
                
                String lineElement = progressLines.get(lineIndex);

                lineElement += playerName + ", ";
                progressLines.set(lineIndex, lineElement);
            }
            
            for (String line : progressLines) {
                String processedLine = removeTrailingComma(line);
                progressString.append(getRoleMentionByName(processedLine)).append("\n");
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return progressString.toString();
    }
    
    protected String getCompletionString(ProgressMap progressMap) {
        String mapName = progressMap.getMapName();
        StringBuilder completionString = new StringBuilder();
        
        completionString
            .append(topHeader)
            .append("\n")
            .append(getRoleMentionByName(progressMap.getVictorRole()))
            .append("\n")
            .append(bottomHeader)
            .append("\n");
        try {
            String sqlQuery = "SELECT userid, fails, playtime, finished_date FROM player_log WHERE passed=1 AND mapname=? ORDER BY finished_date ASC;";
            PreparedStatement preparedStatement = TD2Core.sql().prepare(sqlQuery);
            preparedStatement.setString(1, mapName);
    
            ResultSet resultSet = preparedStatement.executeQuery();
            int index = 1;
            while (resultSet.next()) {
                String playerName = retrievePlayerName(resultSet.getString("userid"));
                String completionDate = "(" + resultSet.getString("finished_date") + ") ";
                String playTime = "in " + Utils.secondsToTime(resultSet.getInt("playtime")/1000) + " ";
                String failCount = "with " + resultSet.getString("fails") + " fails.";
    
                completionString = indexLB(completionString, index);
                completionString.append("**").append(playerName).append("** ").append(completionDate).append(playTime).append(failCount).append("\n");
        
                index++;
            }
            completionString = rankString(completionString, index);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return completionString.toString();
    }
    
    public String getRoleMentionByName(String roleName) {
        List<Role> roles = guild.getRolesByName(roleName, true);
        if (!roles.isEmpty()) {
            return roles.get(0).getAsMention();
        }
        return roleName;
    }
    
    public String removeTrailingComma(String input) {
        input = input.trim();
        if (input.endsWith(",")) {
            return input.substring(0, input.length() - 1).trim();
        }
        return input;
    }
    
    protected String retrievePlayerName(String uuid) {
        String playerName = Utils.getPlayerNameByUUID(uuid);
        playerName = playerName + Utils.repeatSpace((16 - playerName.length())*1.5);
        return playerName.replace("_", "\\_");
    }
    
    protected StringBuilder indexLB(StringBuilder builder, int index) {
        if (index == 1) builder.append("> :first_place: ");
        else if (index == 2) builder.append("> :second_place: ");
        else if (index == 3) builder.append("> :third_place: ");
        else builder.append("> **").append(index).append(".** ");
        return builder;
    }
    
    protected StringBuilder rankString(StringBuilder builder, int index) {
        if (index == 1) {
            builder.append("> :first_place: **Be the first!**\n");
            builder.append("> :second_place: \n");
        } else {
            if (index == 2) builder.append("> :second_place: \n");
            else if (index == 3) builder.append("> :third_place: \n");
            else builder.append("> **").append(index).append(".** \n");
        }
        return builder;
    }
    
}
