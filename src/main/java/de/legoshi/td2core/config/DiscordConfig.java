package de.legoshi.td2core.config;

import org.bukkit.plugin.java.JavaPlugin;

public class DiscordConfig extends ConfigAccessor {
    
    public static final String fileName = "discord.yml";
    
    public DiscordConfig(JavaPlugin plugin) {
        super(plugin, fileName);
    }
    
}
