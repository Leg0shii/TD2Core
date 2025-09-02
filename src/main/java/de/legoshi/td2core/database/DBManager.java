package de.legoshi.td2core.database;

import de.legoshi.td2core.config.ConfigManager;
import de.legoshi.td2core.config.DBConfig;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

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
            createBlockData();
            createCollectedCp();
            createMaps();
            createDiscordData();
            createPlayerLog();
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

    private void createBlockData() {
        String sql = "CREATE TABLE IF NOT EXISTS block_data (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "block_location TEXT, " +
                "teleport_location TEXT, " +
                "next_checkpoint TEXT, " +
                "time_till_next INT, " +
                "is_checkpoint TINYINT(1) DEFAULT 1, " +
                "cp_index INT DEFAULT -1, " +
                "cp_effect VARCHAR(128), " +
                "is_nosprint TINYINT(1) DEFAULT 0" +
                ");";
        mySQL.update(sql);
    }

    private void createCollectedCp() {
        String sql = "CREATE TABLE IF NOT EXISTS collected_cp (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "userid VARCHAR(255), " +
                "mapname VARCHAR(255), " +
                "block_location TEXT" +
                ");";
        mySQL.update(sql);
    }

    private void createDiscordData() {
        String sql = "CREATE TABLE IF NOT EXISTS discord_data (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "mcuuid VARCHAR(255), " +
                "discorduuid VARCHAR(255)" +
                ");";
        mySQL.update(sql);
    }

    private void createMaps() {
        String sql = "CREATE TABLE IF NOT EXISTS maps (" +
                "mapname VARCHAR(255) PRIMARY KEY, " +
                "weight INT, " +
                "total_cp INT, " +
                "redstone TINYINT(1) DEFAULT 0, " +
                "speedrun TINYINT(1) DEFAULT 0, " +
                "display_name VARCHAR(255), " +
                "build_date datetime, " +
                "head_value VARCHAR(1024), " +
                "difficulty INT, " +
                "start_location VARCHAR(1024), " +
                "end_location VARCHAR(1024), " +
                "section INT DEFAULT -1" +
                ");";
        mySQL.update(sql);
    }

    private void createPlayerLog() {
        String sql = "CREATE TABLE IF NOT EXISTS player_log (" +
                "playid INT AUTO_INCREMENT PRIMARY KEY, " +
                "userid TEXT, " +
                "mapname VARCHAR(255), " +
                "passed TINYINT(1) DEFAULT 0, " +
                "playtime BIGINT DEFAULT 0, " +
                "fails INT DEFAULT 0, " +
                "started_date DATE, " +
                "finished_date DATE, " +
                "last_location TEXT, " +
                "last_cp_location TEXT, " +
                "next_cp_location TEXT, " +
                "time_till_next INT, " +
                "cp_count INT DEFAULT 0, " +
                "is_nosprint TINYINT(1) DEFAULT 0, " +
                "cp_effect VARCHAR(128)" +
                ");";
        mySQL.update(sql);
    }

}
