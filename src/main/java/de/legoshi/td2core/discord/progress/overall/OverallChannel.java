package de.legoshi.td2core.discord.progress.overall;

import de.legoshi.td2core.TD2Core;
import de.legoshi.td2core.cache.GlobalLBStats;
import de.legoshi.td2core.discord.RoleManager;
import de.legoshi.td2core.discord.progress.ProgressChannel;
import de.legoshi.td2core.discord.progress.ProgressMap;
import de.legoshi.td2core.util.Utils;
import net.dv8tion.jda.api.entities.Guild;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class OverallChannel extends ProgressChannel {
    
    public OverallChannel(String channelID, Guild guild) {
        super(channelID, guild);
    }
    
    @Override
    protected String getProgressString(ProgressMap progressMap) {
        try {
            return TD2Core.getInstance().globalLBCache.getLeaderboardData().thenApplyAsync(data -> {
                HashMap<UUID, GlobalLBStats> leaderboard = RoleManager.sortMap(data, 5);
    
                StringBuilder progressString = new StringBuilder();
                List<String> progressLines = new ArrayList<>(progressMap.getProgressLines());
                
                for (UUID uuid : leaderboard.keySet()) {
                    String playerName = Utils.getPlayerNameByUUID(uuid.toString()).replace("_", "\\_");
                    
                    int percentage = (int) leaderboard.get(uuid).getPercentage();
                    int lineIndex = progressMap.getProgressPosition(percentage);
                    if (lineIndex == -1) continue;
    
                    String lineElement = progressLines.get(lineIndex);
    
                    lineElement += playerName + ", ";
                    progressLines.set(lineIndex, lineElement);
                }
    
                for (String line : progressLines) {
                    String processedLine = removeTrailingComma(line);
                    progressString.append(getRoleMentionByName(processedLine)).append("\n");
                }
                
                return progressString.toString();
            }).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "ERROR";
    }
    
    @Override
    protected String getCompletionString(ProgressMap progressMap) {
        StringBuilder completionString = new StringBuilder();
        completionString
            .append(getTopHeader())
            .append("\n")
            .append(getRoleMentionByName(progressMap.getVictorRole()))
            .append("\n")
            .append(getBottomHeader())
            .append("\n");
        try {
            String sqlQuery = "SELECT" +
                "    pl.userid," +
                "    min_date," +
                "    SUM(pl.playtime) AS totalplaytime," +
                "    SUM(pl.fails) AS totalfails " +
                "FROM player_log AS pl " +
                "JOIN (" +
                "    SELECT" +
                "        userid," +
                "        MIN(finished_date) AS min_date" +
                "    FROM player_log" +
                "    WHERE" +
                "        passed = 1" +
                "        AND mapname = 'Final Jump'" +
                "    GROUP BY userid" +
                ") AS SubQuery ON pl.userid = SubQuery.userid " +
                "WHERE" +
                "    pl.finished_date <= SubQuery.min_date" +
                "    AND pl.mapname = 'Final Jump' " +
                "GROUP BY pl.userid;";
            
            PreparedStatement preparedStatement = TD2Core.sql().prepare(sqlQuery);
            ResultSet resultSet = preparedStatement.executeQuery();
            
            int index = 1;
            while (resultSet.next()) {
                String playerName = retrievePlayerName(resultSet.getString("userid"));
                String completionDate = "(" + resultSet.getString("min_date") + ") ";
                String playTime = "in " + Utils.secondsToTime(resultSet.getInt("totalplaytime")/1000) + " ";
                String failCount = "with " + resultSet.getString("totalfails") + " fails.";
    
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
    
}
