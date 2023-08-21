package de.legoshi.td2core;

import de.legoshi.td2core.block.BlockManager;
import de.legoshi.td2core.cache.MapLBCache;
import de.legoshi.td2core.command.*;
import de.legoshi.td2core.command.hide.HideAllCommand;
import de.legoshi.td2core.command.hide.HideCommand;
import de.legoshi.td2core.command.hide.ShowAllCommand;
import de.legoshi.td2core.command.hide.ShowCommand;
import de.legoshi.td2core.config.*;
import de.legoshi.td2core.database.AsyncMySQL;
import de.legoshi.td2core.database.DBManager;
import de.legoshi.td2core.discord.DiscordManager;
import de.legoshi.td2core.listener.GeneralListener;
import de.legoshi.td2core.listener.ParkourListener;
import de.legoshi.td2core.map.MapManager;
import de.legoshi.td2core.player.PlayerManager;
import de.legoshi.td2core.cache.GlobalLBCache;
import de.legoshi.td2core.player.invis.InvisManager;
import de.legoshi.td2core.util.WorldLoader;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public final class TD2Core extends JavaPlugin {
    
    public static boolean isShuttingDown = false;
    public GlobalLBCache globalLBCache;
    public MapLBCache mapLBCache;
    
    private static TD2Core instance;
    
    public HashMap<String, ConfigAccessor> config = new HashMap<>();
    public Location spawnLocation;
    public String discordToken;
    
    private BlockManager blockManager;
    private DBManager dbManager;
    @Getter private DiscordManager discordManager;
    private InvisManager invisManager;
    
    @Override
    public void onEnable() {
        instance = this;
    
        WorldLoader.loadWorlds(); // load before config
        
        loadConfig();
        
        discordManager = new DiscordManager();
        dbManager = new DBManager(this);
        blockManager = new BlockManager();
        invisManager = new InvisManager();
    
        registerEvents();
        registerCommands();
        
        MapManager.loadMaps();
        globalLBCache = new GlobalLBCache();
        mapLBCache = new MapLBCache();
    }

    @Override
    public void onDisable() {
        isShuttingDown = true;
        Bukkit.getOnlinePlayers().forEach(all -> PlayerManager.get(all).serverLeave(true));
        config.keySet().forEach(key -> config.get(key).saveConfig());
    }
    
    private void registerCommands() {
        Bukkit.getPluginCommand("help").setExecutor(new HelpCommand());
        Bukkit.getPluginCommand("prac").setExecutor(new PracticeCommand());
        Bukkit.getPluginCommand("unprac").setExecutor(new UnPracCommand());
        Bukkit.getPluginCommand("kit").setExecutor(new KitCommand());
        Bukkit.getPluginCommand("leave").setExecutor(new LeaveCommand());
        Bukkit.getPluginCommand("delete").setExecutor(new DeletePlayerCommand());
        Bukkit.getPluginCommand("td2reload").setExecutor(new ReloadCommand());
        Bukkit.getPluginCommand("spawn").setExecutor(new SpawnCommand());
        Bukkit.getPluginCommand("nv").setExecutor(new NightVisionCommand());
        Bukkit.getPluginCommand("reset").setExecutor(new ResetCommand());
        Bukkit.getPluginCommand("spc").setExecutor(new SPCCommand(blockManager));
        Bukkit.getPluginCommand("staff").setExecutor(new StaffCommand());
        Bukkit.getPluginCommand("hide").setExecutor(new HideCommand(invisManager));
        Bukkit.getPluginCommand("show").setExecutor(new ShowCommand(invisManager));
        Bukkit.getPluginCommand("hideall").setExecutor(new HideAllCommand(invisManager));
        Bukkit.getPluginCommand("showall").setExecutor(new ShowAllCommand(invisManager));
    }
    
    private void registerEvents() {
        PluginManager pluginManager = Bukkit.getServer().getPluginManager();
        pluginManager.registerEvents(new GeneralListener(), this);
        pluginManager.registerEvents(new ParkourListener(blockManager), this);
        pluginManager.registerEvents(blockManager, this);
        pluginManager.registerEvents(invisManager, this);
    }
    
    public void loadConfig() {
        config.put(DBConfig.fileName, new DBConfig(this));
        config.put(ServerConfig.fileName, new ServerConfig(this));
        config.put(MapConfig.fileName, new MapConfig(this));
        config.put(PlayerConfig.fileName, new PlayerConfig(this));
        config.put(DiscordConfig.fileName, new DiscordConfig(this));
        config.keySet().forEach(key -> config.get(key).reloadConfig());
    }
    
    public static TD2Core getInstance() {
        return instance;
    }
    
    public static AsyncMySQL sql() {
        return instance.dbManager.mySQL;
    }
    
}
