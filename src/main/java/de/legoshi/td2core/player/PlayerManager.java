package de.legoshi.td2core.player;

import com.viaversion.viaversion.util.Pair;
import de.legoshi.td2core.TD2Core;
import de.legoshi.td2core.map.ParkourMap;
import de.legoshi.td2core.map.MapManager;
import de.legoshi.td2core.map.session.ParkourSession;
import de.legoshi.td2core.map.session.SessionManager;
import de.legoshi.td2core.util.Utils;
import org.bukkit.entity.Player;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class PlayerManager {
    
    private static final HashMap<Player, ParkourPlayer> playerHashMap = new HashMap<>();
    
    public static void put(ParkourPlayer parkourPlayer) {
        playerHashMap.put(parkourPlayer.getPlayer(), parkourPlayer);
    }
    
    public static ParkourPlayer get(Player player) {
        return playerHashMap.get(player);
    }
    
    public static void remove(ParkourPlayer parkourPlayer) {
        playerHashMap.remove(parkourPlayer.getPlayer());
    }
    
    public static CompletableFuture<Void> loadPercentage(Player player) {
        return PlayerManager.getCompPercentage(player.getUniqueId().toString()).thenApply(percentage -> {
            get(player).setPercentage(percentage.key());
            get(player).setRank(percentage.value());
            return null;
        });
    }
    
    public static void saveIndividualStats(Player player, ParkourMap parkourMap) {
        if (parkourMap == null) return;
        String mapName = parkourMap.getMapName();
        ParkourSession session = SessionManager.get(player, parkourMap);
        ParkourPlayer parkourPlayer = get(player);
        
        if (parkourPlayer.getPlayerState() == PlayerState.STAFF_MODE) return;
        
        // check if player has entry
        String entryCheck = "SELECT * FROM player_log WHERE mapname = ? AND userid = ? AND passed = 0";
        try {
            PreparedStatement preparedStatement = TD2Core.sql().prepare(entryCheck);
            preparedStatement.setString(1, mapName);
            preparedStatement.setString(2, player.getUniqueId().toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String sqlString = "UPDATE player_log SET passed = ?, playtime = ?, fails = ?, finished_date = ?, last_location = ?, last_cp_location = ?, next_cp_location = ?, time_till_next = ? WHERE mapname = ? AND userid = ? AND passed = 0";
                long playTime = parkourPlayer.getPlayerState() == PlayerState.PRACTICE ? session.getPausedTime() : session.getPlayTime();
                PreparedStatement preparedStatement2 = TD2Core.sql().prepare(sqlString);
                preparedStatement2.setBoolean(1, session.justFinished());
                preparedStatement2.setLong(2, playTime);
                preparedStatement2.setInt(3, session.getFails());
                preparedStatement2.setDate(4, session.getFinished());
                preparedStatement2.setString(5, Utils.getStringFromLocation(session.getLastMapLocation()));
                preparedStatement2.setString(6, Utils.getStringFromLocation(session.getLastCheckpointLocation()));
                preparedStatement2.setString(7, Utils.getStringFromLocation(session.getNextCP()));
                preparedStatement2.setInt(8, -1);
                preparedStatement2.setString(9, mapName);
                preparedStatement2.setString(10, player.getUniqueId().toString());
                preparedStatement2.execute();
            } else {
                String sqlString = "INSERT INTO player_log (mapname, userid, passed, playtime, fails, started_date, finished_date, last_location, last_cp_location, next_cp_location, time_till_next) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement preparedStatement2 = TD2Core.sql().prepare(sqlString);
                preparedStatement2.setString(1, mapName);
                preparedStatement2.setString(2, player.getUniqueId().toString());
                preparedStatement2.setBoolean(3, session.justFinished());
                preparedStatement2.setLong(4, session.getPlayTime());
                preparedStatement2.setInt(5, session.getFails());
                preparedStatement2.setDate(6, session.getStarted());
                preparedStatement2.setDate(7, session.getFinished());
                preparedStatement2.setString(8, Utils.getStringFromLocation(session.getLastMapLocation()));
                preparedStatement2.setString(9, Utils.getStringFromLocation(session.getLastCheckpointLocation()));
                preparedStatement2.setString(10, Utils.getStringFromLocation(session.getNextCP()));
                preparedStatement2.setInt(11, -1);
                preparedStatement2.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (!TD2Core.isShuttingDown) {
                MapManager.loadMapStats(mapName);
            }
        }
    }
    
    public static CompletableFuture<Void> loadIndividualStats(Player player, ParkourMap parkourMap) {
        if (parkourMap == null) new CompletableFuture<>();
        
        String mapName = parkourMap.getMapName();
        String sqlString = "SELECT * FROM player_log WHERE mapname = ? AND userid = ?";
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                SessionManager.put(player, parkourMap);
                ParkourSession session = SessionManager.get(player, parkourMap);
                
                PreparedStatement preparedStatement = TD2Core.sql().prepare(sqlString);
                preparedStatement.setString(1, mapName);
                preparedStatement.setString(2, player.getUniqueId().toString());
                
                ResultSet resultSet = preparedStatement.executeQuery();
                
                if (!resultSet.next()) {
                    String query = "INSERT INTO player_log (mapname, userid, started_date) VALUES (?, ?, ?)";
                    PreparedStatement preparedStatement2 = TD2Core.sql().prepare(query);
                    preparedStatement2.setString(1, mapName);
                    preparedStatement2.setString(2, player.getUniqueId().toString());
                    preparedStatement2.setDate(3, new Date(System.currentTimeMillis()));
                    preparedStatement2.execute();
                }
                
                resultSet.beforeFirst();
                while (resultSet.next()) {
                    if (!resultSet.getBoolean("passed")) {
                        Date started = resultSet.getDate("started_date");
                        Date finished = resultSet.getDate("finished_date");
                        long playTime = resultSet.getLong("playtime");
                        int fails = resultSet.getInt("fails");
                        session.setLastMapLocation(Utils.getLocationFromString(resultSet.getString("last_location")));
                        session.setLastCheckpointLocation(Utils.getLocationFromString(resultSet.getString("last_cp_location")));
                        if (started == null) started = new Date(System.currentTimeMillis());
                        session.setStarted(started);
                        session.setFinished(finished);
                        session.setPlayTime(playTime);
                        session.setFails(fails);
                        session.setNextCP(Utils.getLocationFromString(resultSet.getString("next_cp_location")));
                        // load time till next
                    } else {
                        session.setPassed(true);
                    }
                    session.setTotalFails(session.getTotalFails() + resultSet.getInt("fails"));
                    session.setTotalPlayTime(session.getTotalPlayTime() + resultSet.getInt("playtime"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        });
    }
    
    public static CompletableFuture<Pair<Double, Integer>> getCompPercentage(String uuid) {
        return CompletableFuture.supplyAsync(() -> {
            String sqlQuery =
                "SELECT" +
                    "    SUM(IF(p.passed = 1, m.weight, m.weight * (SELECT COUNT(*)" +
                    "                                               FROM collected_cp cp" +
                    "                                               WHERE cp.userid = p.userid" +
                    "                                                 AND cp.mapname = p.mapname) / m.total_cp)) as total_weight " +
                    "FROM player_log p " +
                    "JOIN maps m ON p.mapname = m.mapname " +
                    "WHERE p.userid = ?";
            double percentage = 0;
            
            try {
                PreparedStatement preparedStatement = TD2Core.sql().prepare(sqlQuery);
                preparedStatement.setString(1, uuid);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    percentage = resultSet.getDouble("total_weight");
                }
    
                // Now, find the rank based on the percentage
                String rankSql =
                    "SELECT COUNT(*) + 1 AS td2rank " +
                        "FROM (" +
                        "    SELECT p.userid, " +
                        "           SUM(IF(p.passed = 1, m.weight, m.weight * (SELECT COUNT(*)" +
                        "                                                  FROM collected_cp cp" +
                        "                                                  WHERE cp.userid = p.userid" +
                        "                                                    AND cp.mapname = p.mapname) / m.total_cp)) as total_weight " +
                        "    FROM player_log p " +
                        "    JOIN maps m ON p.mapname = m.mapname " +
                        "    GROUP BY p.userid" +
                        ") t " +
                        "WHERE t.total_weight > ? OR " +
                        "     (t.total_weight = ? AND t.userid < ?)";
    
                preparedStatement = TD2Core.sql().prepare(rankSql);
                preparedStatement.setDouble(1, percentage);
                preparedStatement.setDouble(2, percentage);
                preparedStatement.setString(3, uuid);
                resultSet = preparedStatement.executeQuery();
                int rank = 0;
                if (resultSet.next()) {
                    rank = resultSet.getInt("td2rank");
                }
    
                return new Pair<>(((int) (percentage*1000))/1000.0, rank);
            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException("Error fetching player percentage.", e);
            }
        });
    }
    
}