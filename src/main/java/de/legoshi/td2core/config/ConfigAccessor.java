package de.legoshi.td2core.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;

public abstract class ConfigAccessor {

    private final String fileName;
    private final JavaPlugin plugin;
    private final File configFile;
    private FileConfiguration fileConfiguration;

    public ConfigAccessor(JavaPlugin plugin, String fileName) {
        if (plugin == null) throw new IllegalArgumentException("Plugin cannot be null.");
        this.plugin = plugin;
        this.fileName = fileName;
        File dataFolder = plugin.getDataFolder();
        if (dataFolder == null) throw new IllegalStateException();
        this.configFile = new File(plugin.getDataFolder(), fileName);
    }

    public ConfigAccessor(JavaPlugin plugin, File folder, String fileName) {
        if (plugin == null) throw new IllegalArgumentException("Plugin cannot be null.");
        this.plugin = plugin;
        this.fileName = fileName;
        if (folder == null) throw new IllegalStateException();
        this.configFile = new File(folder, fileName);
    }

    public void reloadConfig() {
        this.fileConfiguration = YamlConfiguration.loadConfiguration(this.configFile);
        InputStream defConfigStream = this.plugin.getResource(this.fileName);
        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream));
            this.fileConfiguration.setDefaults(defConfig);
        }
    }

    public FileConfiguration getConfig() {
        if (this.fileConfiguration == null) reloadConfig();
        return this.fileConfiguration;
    }

    public void saveConfig() {
        if (this.fileConfiguration != null && this.configFile != null) {
            try {
                getConfig().save(this.configFile);
            } catch (IOException var2) {
                this.plugin.getLogger().log(Level.SEVERE, "Could not save config to " + this.configFile, var2);
            }
        }
    }

    public void saveDefaultConfig() {
        if (!this.configFile.exists()) {
            this.plugin.saveResource(this.fileName, false);
        }
    }
}

