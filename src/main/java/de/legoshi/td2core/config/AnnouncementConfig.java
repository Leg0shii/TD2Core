package de.legoshi.td2core.config;

import org.bukkit.plugin.java.JavaPlugin;

public class AnnouncementConfig extends ConfigAccessor{
    
    public static final String fileName = "announcement.yml";
    
    public AnnouncementConfig(JavaPlugin plugin) {
        super(plugin, fileName);
    }
    
}
