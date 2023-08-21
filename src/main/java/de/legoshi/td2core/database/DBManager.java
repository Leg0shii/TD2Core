package de.legoshi.td2core.database;

import de.legoshi.td2core.TD2Core;
import de.legoshi.td2core.config.DBConfig;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DBManager {
    
    private final Plugin plugin;
    public AsyncMySQL mySQL;
    
    public DBManager(Plugin plugin) {
        this.plugin = plugin;
        this.mySQL = connectToDB();
        
        initializeTables();
        startScheduler();
    }
    
    public void initializeTables() {
        if(mySQL != null) {
            Bukkit.getConsoleSender().sendMessage("[TD2Core] DB connected");
        } else {
            Bukkit.getConsoleSender().sendMessage("[TD2Core] Cannot connect to DB");
        }
    }
    
    private void startScheduler() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(TD2Core.getInstance(), () -> {
            String query = "SELECT 1 FROM player_log LIMIT 1";
            PreparedStatement preparedStatement = mySQL.prepare(query);
            try {
                preparedStatement.executeQuery();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }, 0, 20*60*10);
    }
    
    public AsyncMySQL connectToDB() {
        FileConfiguration config = TD2Core.getInstance().config.get(DBConfig.fileName).getConfig();
        
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
