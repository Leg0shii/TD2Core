package de.legoshi.td2core.cache;

import de.legoshi.td2core.TD2Core;
import de.legoshi.td2core.config.PlayerConfig;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class GlobalLBCache {
    
    private int page;
    private int pageVolume;
    
    private HashMap<UUID, GlobalLBStats> cache = null;
    private final Object cacheLock = new Object();
    
    public GlobalLBCache() {
        super();
        startScheduler();
    }
    
    /**
     * Invalidates the cache, causing the data to be reloaded the next time it's accessed.
     */
    public void invalidateCache() {
        synchronized (cacheLock) {
            cache = null;
        }
    }
    
    /**
     * Reloads the leaderboard data and updates the cache without invalidating it.
     */
    public void reloadCache() {
        synchronized (cacheLock) {
            HashMap<UUID, GlobalLBStats> loadedCache = loadLeaderboardData();
            if (loadedCache != null) {
                cache = loadedCache;
            }
        }
    }
    
    /**
     * Fetches the leaderboard data. If it's cached, return the cached version. Otherwise, load it.
     */
    public CompletableFuture<HashMap<UUID, GlobalLBStats>> getLeaderboardData() {
        synchronized (cacheLock) {
            HashMap<UUID, GlobalLBStats> loadedCache = cache;
            if (loadedCache == null) {
                // Load the data and return the CompletableFuture
                return loadLeaderboardDataAsync();
            }
            return CompletableFuture.completedFuture(new HashMap<>(loadedCache));
        }
    }
    
    private CompletableFuture<HashMap<UUID, GlobalLBStats>> loadLeaderboardDataAsync() {
        return CompletableFuture.supplyAsync(() -> {
            return loadLeaderboardData();
        }).thenApply(newData -> {
            synchronized (cacheLock) {
                if (newData != null) {
                    cache = newData;
                }
                return newData; // This ensures the CompletableFuture contains the loaded data
            }
        });
    }
    
    /**
     * Loads the leaderboard data from the database.
     */
    private HashMap<UUID, GlobalLBStats> loadLeaderboardData() {
        HashMap<UUID, GlobalLBStats> hashMap = new HashMap<>();
        
        String sqlQuery =
            "SELECT p.userid," +
                "       SUM(IF(p.passed = 1, m.weight, m.weight * (SELECT COUNT(*)" +
                "                                                  FROM collected_cp cp" +
                "                                                  WHERE cp.userid = p.userid" +
                "                                                    AND cp.mapname = p.mapname) / m.total_cp)) as total_weight " +
                "FROM (" +
                "    SELECT DISTINCT userid, mapname, passed" +
                "    FROM player_log" +
                ") p " +
                "JOIN maps m ON p.mapname = m.mapname " +
                "GROUP BY p.userid;";
    
        try {
            PreparedStatement preparedStatement = TD2Core.sql().prepare(sqlQuery);
            ResultSet resultSet = preparedStatement.executeQuery();
        
            while (resultSet.next()) {
                UUID uuid = UUID.fromString(resultSet.getString("userid"));
                double percentage = resultSet.getDouble("total_weight");
                OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
                int jumps;
                int playTime;
                if (player.isOnline()) {
                    jumps = player.getPlayer().getStatistic(Statistic.JUMP);
                    playTime = player.getPlayer().getStatistic(Statistic.PLAY_ONE_TICK) / 20;
                } else {
                    PlayerConfig playerConfig = (PlayerConfig) TD2Core.getInstance().config.get(PlayerConfig.fileName);
                    jumps = playerConfig.getJumps(uuid);
                    playTime = playerConfig.getTime(uuid);
                }
                hashMap.put(uuid, new GlobalLBStats(percentage, jumps, playTime));
            }
        
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return hashMap;
    }
    
    private void startScheduler() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(TD2Core.getInstance(), this::reloadCache, 0, 20L*60*10);
    }
    
}