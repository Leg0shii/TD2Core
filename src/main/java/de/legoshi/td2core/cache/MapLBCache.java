package de.legoshi.td2core.cache;

import de.legoshi.td2core.TD2Core;
import de.legoshi.td2core.map.MapManager;
import de.legoshi.td2core.map.ParkourMap;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Getter
public class MapLBCache {
    
    private static final int PAGE_VOLUME = 40;
    private final HashMap<ParkourMap, HashMap<UUID, MapLBStats>> cache = new HashMap<>();
    private final Object cacheLock = new Object();
    
    public void startMapCacheScheduler() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(TD2Core.getInstance(), this::reloadCache, 0, 20L*60*10);
    }
    
    public void invalidateCache(UUID uuid, ParkourMap parkourMap) {
        synchronized (cacheLock) {
            cache.get(parkourMap).put(uuid, null);
        }
    }
    
    public void invalidateCache(ParkourMap parkourMap) {
        synchronized (cacheLock) {
            cache.put(parkourMap, null);
        }
    }
    
    public void reloadCache(UUID uuid, ParkourMap parkourMap) {
        synchronized (cacheLock) {
            MapLBStats stats = loadMapLeaderboardDataForUser(uuid, parkourMap);
            if (stats != null) {
                cache.get(parkourMap).put(uuid, stats);
            }
        }
    }
    
    public void reloadCache() {
        MapManager.getPkMapHashMap().values().forEach(parkourMap -> {
            synchronized (cacheLock) {
                HashMap<UUID, MapLBStats> stats = loadLeaderboardData(parkourMap);
                cache.put(parkourMap, stats);
            }
        });
    }
    
    public CompletableFuture<MapLBStats> getLeaderboardData(UUID uuid, ParkourMap parkourMap) {
        synchronized (cacheLock) {
            MapLBStats stats = cache.get(parkourMap).get(uuid);
            if (stats == null) {
                // Load the data and return the CompletableFuture
                return loadLeaderboardDataAsync(uuid, parkourMap);
            }
            return CompletableFuture.completedFuture(stats);
        }
    }
    
    public CompletableFuture<HashMap<UUID, MapLBStats>> getLeaderboardData(ParkourMap parkourMap) {
        synchronized (cacheLock) {
            HashMap<UUID, MapLBStats> stats = cache.get(parkourMap);
            if (stats == null) {
                // Load the data and return the CompletableFuture
                return loadLeaderboardDataAsync(parkourMap);
            }
            return CompletableFuture.completedFuture(stats);
        }
    }
    
    private CompletableFuture<MapLBStats> loadLeaderboardDataAsync(UUID uuid, ParkourMap parkourMap) {
        return CompletableFuture.supplyAsync(() -> {
            return loadMapLeaderboardDataForUser(uuid, parkourMap);
        }).thenApply(newData -> {
            synchronized (cacheLock) {
                if (newData != null) {
                    cache.get(parkourMap).put(uuid, newData);
                }
                return newData; // This ensures the CompletableFuture contains the loaded data
            }
        });
    }
    
    private CompletableFuture<HashMap<UUID, MapLBStats>> loadLeaderboardDataAsync(ParkourMap parkourMap) {
        return CompletableFuture.supplyAsync(() -> {
            return loadLeaderboardData(parkourMap);
        }).thenApply(newData -> {
            synchronized (cacheLock) {
                if (newData != null) {
                    cache.put(parkourMap, newData);
                }
                return newData; // This ensures the CompletableFuture contains the loaded data
            }
        });
    }
    
    public void addCPCount(UUID userId, ParkourMap parkourMap) {
        synchronized (cacheLock) {
            if (cache.get(parkourMap) != null && cache.get(parkourMap).get(userId) != null) {
                cache.get(parkourMap).get(userId).setCurrentCPCount(cache.get(parkourMap).get(userId).getCurrentCPCount() + 1);
            }
        }
    }
    
    protected MapLBStats loadMapLeaderboardDataForUser(UUID userId, ParkourMap parkourMap) {
        String mapName = parkourMap.getMapName();
        MapLBStats mapLBStats = null;
        
        String sqlQuery =
            "SELECT" +
                "    ps.userid," +
                "    COALESCE(ps.min_playtime, 0) as playtime," +
                "    COALESCE(ps.min_fails, 0) as fails," +
                "    COALESCE(cps.cpCount, 0) as cpCount," +
                "    ps.has_passed " +
                "FROM (" +
                "    SELECT" +
                "        userid," +
                "        IFNULL(MIN(IF(passed, playtime, NULL)), MIN(playtime)) as min_playtime," +
                "        IFNULL(MIN(IF(passed, fails, NULL)), MIN(fails)) as min_fails," +
                "        MAX(passed) as has_passed" +
                "    FROM player_log" +
                "    WHERE mapname = ? AND userid = ?" +
                "    GROUP BY userid" +
                ") AS ps " +
                "LEFT JOIN (" +
                "    SELECT" +
                "        userid," +
                "        COUNT(*) as cpCount" +
                "    FROM collected_cp" +
                "    WHERE mapname = ? AND userid = ?" +
                "    GROUP BY userid" +
                ") AS cps ON ps.userid = cps.userid;";
        
        try {
            PreparedStatement preparedStatement = TD2Core.sql().prepare(sqlQuery);
            preparedStatement.setString(1, mapName);
            preparedStatement.setString(2, userId.toString());
            preparedStatement.setString(3, mapName);
            preparedStatement.setString(4, userId.toString());
            
            ResultSet resultSet = preparedStatement.executeQuery();
            
            if (resultSet.next()) {
                double playtime = resultSet.getDouble("playtime");
                int fails = resultSet.getInt("fails");
                int cpCount = resultSet.getInt("cpCount");
                boolean passed = resultSet.getBoolean("has_passed");
                
                mapLBStats = new MapLBStats(passed, cpCount, playtime, fails);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return mapLBStats;
    }
    
    private HashMap<UUID, MapLBStats> loadLeaderboardData(ParkourMap parkourMap) {
        HashMap<UUID, MapLBStats> hashMap = new HashMap<>();
        String mapName = parkourMap.getMapName();
    
        String sqlQuery =
            "SELECT" +
                "    ps.userid," +
                "    COALESCE(ps.min_playtime, 0) as playtime," +
                "    COALESCE(ps.min_fails, 0) as fails," +
                "    COALESCE(cps.cpCount, 0) as cpCount," +
                "    ps.has_passed " +
                "FROM (" +
                "    SELECT" +
                "        userid," +
                "        IFNULL(MIN(IF(passed, playtime, NULL)), MIN(playtime)) as min_playtime," +
                "        IFNULL(MIN(IF(passed, fails, NULL)), MIN(fails)) as min_fails," +
                "        MAX(passed) as has_passed" +
                "    FROM player_log" +
                "    WHERE mapname = ?" +
                "    GROUP BY userid" +
                ") AS ps " +
                "LEFT JOIN (" +
                "    SELECT" +
                "        userid," +
                "        COUNT(*) as cpCount" +
                "    FROM collected_cp" +
                "    WHERE mapname = ?" +
                "    GROUP BY userid" +
                ") AS cps ON ps.userid = cps.userid;";
    
        try {
            PreparedStatement preparedStatement = TD2Core.sql().prepare(sqlQuery);
            preparedStatement.setString(1, mapName);
            preparedStatement.setString(2, mapName);
        
            ResultSet resultSet = preparedStatement.executeQuery();
        
            while (resultSet.next()) {
                UUID uuid = UUID.fromString(resultSet.getString("userid"));
                double playtime = resultSet.getDouble("playtime");
                int fails = resultSet.getInt("fails");
                int cpCount = resultSet.getInt("cpCount");
                boolean passed = resultSet.getBoolean("has_passed");
            
                hashMap.put(uuid, new MapLBStats(passed, cpCount, playtime, fails));
            }
        
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        return hashMap;
    }
    
}
