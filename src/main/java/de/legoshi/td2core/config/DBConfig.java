package de.legoshi.td2core.config;

import org.bukkit.plugin.java.JavaPlugin;

public class DBConfig extends ConfigAccessor {
    
    public static final String fileName = "td2_db.yml";
    
    public DBConfig(JavaPlugin plugin) {
        super(plugin, fileName);
    }
    
}
