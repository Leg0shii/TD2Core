package de.legoshi.td2core.map;

import de.legoshi.td2core.TD2Core;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class MapManager {
    
    @Getter
    private static final ConcurrentHashMap<String, ParkourMap> pkMapHashMap = new ConcurrentHashMap<>();
    
    public static void put(ParkourMap parkourMap) {
        if (parkourMap == null || parkourMap.getMapName() == null) {
            throw new IllegalArgumentException("ParkourMap or its name cannot be null");
        }
        pkMapHashMap.put(parkourMap.getMapName(), parkourMap);
    }
    
    public static ParkourMap get(String mapName) {
        return pkMapHashMap.get(mapName);
    }
    
    public static void remove(String mapName) {
        pkMapHashMap.remove(mapName);
    }
    
    public static void loadMaps() {
        pkMapHashMap.keySet().forEach(MapManager::loadMapStats);
    }
    
    public static CompletableFuture<Boolean> hasPassed(Player player, ParkourMap parkourMap) {
        return CompletableFuture.supplyAsync(() -> {
            String sqlString = "SELECT * FROM player_log WHERE mapname = ? AND passed = 1 AND userid = ?;";
            try {
                
                PreparedStatement preparedStatement = TD2Core.sql().prepare(sqlString);
                preparedStatement.setString(1, parkourMap.mapName);
                preparedStatement.setString(2, player.getUniqueId().toString());
                
                ResultSet resultSet = preparedStatement.executeQuery();
                return resultSet.next();
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        });
    }
    
    public static List<ParkourMap> getAll(String section) {
        return pkMapHashMap
            .values()
            .stream()
            .filter(parkourMap -> parkourMap.getCategory().equals(section))
            .sorted(Comparator.comparing(ParkourMap::getOrder))
            .collect(Collectors.toList());
    }
    
    public static void loadMapStats(String mapName) {
        ParkourMap parkourMap = MapManager.get(mapName);
        String sqlString = "SELECT p.userid, p.passed, p.playtime, p.fails, m.total_cp " +
            "FROM player_log as p " +
            "JOIN maps as m ON p.mapname = m.mapname " +
            "WHERE p.mapname = ?;";
        
        Bukkit.getScheduler().runTaskLaterAsynchronously(TD2Core.getInstance(), () -> {
            try {
                PreparedStatement preparedStatement = TD2Core.sql().prepare(sqlString);
                preparedStatement.setString(1, mapName);
    
                // count individual players
                Set<String> playedPlayer = new HashSet<>();
                Set<String> passedPlayer = new HashSet<>();
                
                ResultSet resultSet = preparedStatement.executeQuery();
                long totalPlayTime = 0;
                int totalFails = 0;
                int lowestPlayTime = Integer.MAX_VALUE;
                int cpCount = 0;
                String fastestPlayer = "None";
                while (resultSet.next()) {
                    playedPlayer.add(resultSet.getString("userid"));
                    cpCount = resultSet.getInt("total_cp");
                    if (resultSet.getInt("passed") == 1) passedPlayer.add(resultSet.getString("userid"));
                    if (resultSet.getInt("passed") == 0) continue;
                    
                    totalPlayTime += resultSet.getLong("playtime");
                    totalFails += resultSet.getInt("fails");
                    if (resultSet.getBoolean("passed") && resultSet.getInt("playtime") < lowestPlayTime) {
                        lowestPlayTime = resultSet.getInt("playtime");
                        fastestPlayer = (Bukkit.getOfflinePlayer(UUID.fromString(resultSet.getString("userid")))).getName();
                    }
                    
                    if (lowestPlayTime == Integer.MAX_VALUE) {
                        lowestPlayTime = 0;
                    }
                }
                parkourMap.setTotalPlays(playedPlayer.size());
                parkourMap.setTotalFails(totalFails);
                parkourMap.setTotalTime(totalPlayTime);
                parkourMap.setFastestTime((lowestPlayTime));
                parkourMap.setFastestPlayer(fastestPlayer);
                parkourMap.setCpCount(cpCount);
                parkourMap.setTotalCompletion(passedPlayer.size());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }, 60L);
    }
    
    public static void deletePlay(String playerID, String mapName) {
        Bukkit.getScheduler().runTaskAsynchronously(TD2Core.getInstance(), () -> {
            String sqlString = "DELETE FROM player_log WHERE mapname = ? AND userid = ? AND passed = 0;";
            try {
                PreparedStatement preparedStatement = TD2Core.sql().prepare(sqlString);
                preparedStatement.setString(1, mapName);
                preparedStatement.setString(2, playerID);
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
    
}
