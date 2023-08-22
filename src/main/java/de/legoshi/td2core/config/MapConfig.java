package de.legoshi.td2core.config;

import org.bukkit.plugin.java.JavaPlugin;

public class MapConfig extends ConfigAccessor {
    
    public static final String fileName = "td2_map.yml";
    
    public MapConfig(JavaPlugin plugin) {
        super(plugin, fileName);
    }
    
}
