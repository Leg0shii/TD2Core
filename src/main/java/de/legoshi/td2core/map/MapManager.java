package de.legoshi.td2core.map;

import de.legoshi.td2core.TD2Core;
import de.legoshi.td2core.config.ConfigManager;
import de.legoshi.td2core.config.MapConfig;
import de.legoshi.td2core.util.Utils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Getter
public class MapManager {
    
    private final ConcurrentHashMap<String, ParkourMap> pkMapHashMap;
    private final FileConfiguration config;
    
    public MapManager(ConfigManager configManager) {
        pkMapHashMap = new ConcurrentHashMap<>();
        this.config = configManager.getConfig(MapConfig.class);
    }
    
    public void put(ParkourMap parkourMap) {
        if (parkourMap == null || parkourMap.getMapName() == null) {
            throw new IllegalArgumentException("ParkourMap or its name cannot be null");
        }
        pkMapHashMap.put(parkourMap.getMapName(), parkourMap);
    }
    
    public ParkourMap get(String mapName) {
        return pkMapHashMap.get(mapName);
    }
    
    public void remove(String mapName) {
        pkMapHashMap.remove(mapName);
    }
    
    public void loadMaps() {
        loadMapsFromConfig();
        pkMapHashMap.keySet().forEach(this::loadMapStats);
    }
    
    private void loadMapsFromConfig() {
        config.getKeys(false).forEach(key -> {
            String mapName;
            for (int i = 1; true; i++) {
                String indexKey = key + "." + i;
                mapName = config.getString(indexKey + ".name");
                if (mapName == null) break;
                
                ParkourMap parkourMap = new ParkourMap(mapName);
                parkourMap.setCategory(key);
                parkourMap.setOrder(i);
                parkourMap.setBuildTime(config.getString(indexKey + ".build"));
                parkourMap.setWeight(config.getInt(indexKey + ".weight"));
                parkourMap.setDisplayName(config.getString(indexKey + ".displayname"));
                parkourMap.setHead(config.getString(indexKey + ".head"));
                parkourMap.setEstimatedDifficulty(config.getInt(indexKey + ".estimated_difficulty"));
                parkourMap.setStartLocation(Utils.getLocationFromString(config.getString(indexKey + ".start_location")));
                parkourMap.setEndLocation(Utils.getLocationFromString(config.getString(indexKey + ".end_location")));
                if (key.equals("section7")) {
                    if (i != 1) {
                        List<PotionEffect> list = new ArrayList<>();
                        list.add(new PotionEffect(PotionEffectType.SPEED, 10000000, i-2));
                        parkourMap.setPotionEffects(list);
                    }
                }
                
                if (key.equals("section6")) {
                    parkourMap.setRedstone(true);
                }
                
                put(parkourMap);
            }
        });
    }
    
    public CompletableFuture<Boolean> hasPassed(Player player, ParkourMap parkourMap) {
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
    
    public List<ParkourMap> getAll(String section) {
        return pkMapHashMap
            .values()
            .stream()
            .filter(parkourMap -> parkourMap.getCategory().equals(section))
            .sorted(Comparator.comparing(ParkourMap::getOrder))
            .collect(Collectors.toList());
    }
    
    public void loadMapStats(String mapName) {
        ParkourMap parkourMap = get(mapName);
        String sqlString = "SELECT p.userid, p.passed, p.playtime, p.fails, m.total_cp, m.redstone " +
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
    
    public void resetPlay(String playerID, ParkourMap parkourMap) {
        Bukkit.getScheduler().runTaskAsynchronously(TD2Core.getInstance(), () -> {
            String sqlString = "UPDATE player_log SET passed = 0, playtime = 0, fails = 0, finished_date = ?, last_location = ?, " +
                "last_cp_location = ?, next_cp_location = ?, time_till_next = ? " +
                "WHERE mapname = ? AND userid = ? AND passed = 0";
            
            try {
                PreparedStatement preparedStatement = TD2Core.sql().prepare(sqlString);
                preparedStatement.setDate(1, null);
                preparedStatement.setString(2, Utils.getStringFromLocation(parkourMap.getStartLocation()));
                preparedStatement.setString(3, Utils.getStringFromLocation(parkourMap.getStartLocation()));
                preparedStatement.setString(4, null);
                preparedStatement.setInt(5, -1);
                preparedStatement.setString(6, parkourMap.getMapName());
                preparedStatement.setString(7, playerID);
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
    
}
