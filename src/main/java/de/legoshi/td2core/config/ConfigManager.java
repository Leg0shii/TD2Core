package de.legoshi.td2core.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class ConfigManager {
    
    public final List<ConfigAccessor> configs;
    
    public ConfigManager(JavaPlugin plugin) {
        this.configs = new ArrayList<>();
        addConfig(new DBConfig(plugin));
        addConfig(new ServerConfig(plugin));
        addConfig(new MapConfig(plugin));
        addConfig(new PlayerConfig(plugin));
        addConfig(new DiscordConfig(plugin));
        addConfig(new AnnouncementConfig(plugin));
        configs.forEach(ConfigAccessor::reloadConfig);
    }
    
    public FileConfiguration getConfig(Class<? extends ConfigAccessor> clazz) {
        ConfigAccessor accessor = getConfigAccessor(clazz);
        if (accessor == null) {
            throw new IllegalArgumentException("Config not found: " + clazz);
        }
        return accessor.getConfig();
    }
    
    public <T extends ConfigAccessor> T getConfigAccessor(Class<T> clazz) {
        for (ConfigAccessor configAccessor : configs) {
            if (configAccessor.getClass().equals(clazz)) {
                return clazz.cast(configAccessor);
            }
        }
        return null;
    }
    
    public void loadConfig() {
        configs.forEach(ConfigAccessor::reloadConfig);
    }
    
    private void addConfig(ConfigAccessor config) {
        this.configs.add(config);
    }
    
}
