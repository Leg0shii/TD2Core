package de.legoshi.td2core.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class ConfigManager {
    
    private final List<ConfigAccessor> config;
    
    public ConfigManager(JavaPlugin plugin) {
        this.config = new ArrayList<>();
        addConfig(new DBConfig(plugin));
        addConfig(new ServerConfig(plugin));
        addConfig(new MapConfig(plugin));
        addConfig(new PlayerConfig(plugin));
        addConfig(new DiscordConfig(plugin));
        config.forEach(ConfigAccessor::reloadConfig);
    }
    
    public FileConfiguration getConfig(Class<? extends ConfigAccessor> clazz) {
        ConfigAccessor accessor = getConfigAccessor(clazz);
        if (accessor == null) {
            throw new IllegalArgumentException("Config not found: " + clazz);
        }
        return accessor.getConfig();
    }
    
    public <T extends ConfigAccessor> T getConfigAccessor(Class<T> clazz) {
        for (ConfigAccessor configAccessor : config) {
            if (configAccessor.getClass().equals(clazz)) {
                return clazz.cast(configAccessor);
            }
        }
        return null;
    }
    
    public void loadConfig() {
        config.forEach(ConfigAccessor::reloadConfig);
    }
    
    private void addConfig(ConfigAccessor config) {
        this.config.add(config);
    }
    
}
