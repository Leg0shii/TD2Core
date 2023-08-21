package de.legoshi.td2core.database;

import de.legoshi.td2core.TD2Core;
import de.legoshi.td2core.config.ConfigManager;
import de.legoshi.td2core.config.DBConfig;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DBManager {
    
    private final Plugin plugin;
    private final ConfigManager configManager;
    
    public AsyncMySQL mySQL;
    
    public DBManager(Plugin plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.mySQL = connectToDB();
        
        initializeTables();
    }
    
    public void initializeTables() {
        if(mySQL != null) {
            Bukkit.getConsoleSender().sendMessage("[TD2Core] DB connected");
        } else {
            Bukkit.getConsoleSender().sendMessage("[TD2Core] Cannot connect to DB");
        }
    }
    
    public AsyncMySQL connectToDB() {
        FileConfiguration config = configManager.getConfig(DBConfig.class);
        
        String host = config.getString("host");
        int port = config.getInt("port");
        String username = config.getString("username");
        String password = config.getString("password");
        String database = config.getString("database");
        
        try {
            mySQL = new AsyncMySQL(plugin, host, port, username, password, database);
            return mySQL;
        } catch (SQLException | ClassNotFoundException throwables) { throwables.printStackTrace(); }
        
        return null;
    }
    
}
