package de.legoshi.td2core.config;

import de.legoshi.td2core.map.ParkourMap;
import de.legoshi.td2core.map.MapManager;
import de.legoshi.td2core.util.Utils;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class MapConfig extends ConfigAccessor {
    
    public static final String fileName = "td2_map.yml";
    
    public MapConfig(JavaPlugin plugin) {
        super(plugin, fileName);
        loadMapsFromConfig();
    }
    
    public void loadMapsFromConfig() {
        MapManager.getPkMapHashMap().clear();
        getConfig().getKeys(false).forEach(key -> {
            String mapName;
            for (int i = 1; true; i++) {
                String indexKey = key + "." + i;
                mapName = getConfig().getString(indexKey + ".name");
                if (mapName == null) break;
                
                ParkourMap parkourMap = new ParkourMap(mapName);
                parkourMap.setCategory(key);
                parkourMap.setOrder(i);
                parkourMap.setBuildTime(getConfig().getString(indexKey + ".build"));
                parkourMap.setWeight(getConfig().getInt(indexKey + ".weight"));
                parkourMap.setDisplayName(getConfig().getString(indexKey + ".displayname"));
                parkourMap.setHead(getConfig().getString(indexKey + ".head"));
                parkourMap.setEstimatedDifficulty(getConfig().getInt(indexKey + ".estimated_difficulty"));
                parkourMap.setStartLocation(Utils.getLocationFromString(getConfig().getString(indexKey + ".start_location")));
                parkourMap.setEndLocation(Utils.getLocationFromString(getConfig().getString(indexKey + ".end_location")));
                if (key.equals("section7")) {
                    if (i != 1) {
                        List<PotionEffect> list = new ArrayList<>();
                        list.add(new PotionEffect(PotionEffectType.SPEED, 10000000, i-2));
                        parkourMap.setPotionEffects(list);
                    }
                }
                MapManager.put(parkourMap);
            }
        });
    }
    
}
