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
import de.legoshi.td2core.player.hide.HideManager;
import de.legoshi.td2core.util.Utils;
import de.legoshi.td2core.util.WorldLoader;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class TD2Core extends JavaPlugin {
    
    public static boolean isShuttingDown = false;
    private static TD2Core instance;
    
    public MapManager mapManager;
    public PlayerManager playerManager;
    
    public GlobalLBCache globalLBCache;
    public MapLBCache mapLBCache;
    
    public ConfigManager configManager;
    public Location spawnLocation;
    
    private BlockManager blockManager;
    private DBManager dbManager;
    private DiscordManager discordManager;
    private HideManager hideManager;
    
    @Override
    public void onLoad() {
        instance = this;
        
        configManager = new ConfigManager(this);
        dbManager = new DBManager(this, configManager);
    
        mapManager = new MapManager(configManager);
        playerManager = new PlayerManager(mapManager);
        discordManager = new DiscordManager(configManager, playerManager);
    
        blockManager = new BlockManager(playerManager);
        hideManager = new HideManager();
    
        globalLBCache = new GlobalLBCache(configManager);
        mapLBCache = new MapLBCache(mapManager);
    }
    
    @Override
    public void onEnable() {
        WorldLoader.loadWorlds();
        
        mapManager.loadMaps();
        
        discordManager.startLeaderboardScheduler();
        globalLBCache.startGlobalCacheScheduler();
        mapLBCache.startMapCacheScheduler();
        blockManager.loadBlockData();
        
        loadSpawnLocation();
        registerEvents();
        registerCommands();
    }

    @Override
    public void onDisable() {
        isShuttingDown = true;
        Bukkit.getOnlinePlayers().forEach(all -> playerManager.get(all).serverLeave(true));
    }
    
    private void registerCommands() {
        Bukkit.getPluginCommand("help").setExecutor(new HelpCommand());
        Bukkit.getPluginCommand("prac").setExecutor(new PracticeCommand(playerManager));
        Bukkit.getPluginCommand("unprac").setExecutor(new UnPracCommand(playerManager));
        Bukkit.getPluginCommand("kit").setExecutor(new KitCommand(playerManager));
        Bukkit.getPluginCommand("leave").setExecutor(new LeaveCommand(playerManager));
        Bukkit.getPluginCommand("delete").setExecutor(new DeletePlayerCommand(mapManager));
        Bukkit.getPluginCommand("spawn").setExecutor(new SpawnCommand(playerManager));
        Bukkit.getPluginCommand("nv").setExecutor(new NightVisionCommand());
        Bukkit.getPluginCommand("reset").setExecutor(new ResetCommand(mapManager, playerManager));
        Bukkit.getPluginCommand("spc").setExecutor(new SPCCommand(blockManager));
        Bukkit.getPluginCommand("staff").setExecutor(new StaffCommand(playerManager));
        Bukkit.getPluginCommand("hide").setExecutor(new HideCommand(hideManager));
        Bukkit.getPluginCommand("show").setExecutor(new ShowCommand(hideManager));
        Bukkit.getPluginCommand("hideall").setExecutor(new HideAllCommand(hideManager));
        Bukkit.getPluginCommand("showall").setExecutor(new ShowAllCommand(hideManager));
    }
    
    private void registerEvents() {
        PluginManager pluginManager = Bukkit.getServer().getPluginManager();
        pluginManager.registerEvents(new GeneralListener(configManager, playerManager), this);
        pluginManager.registerEvents(new ParkourListener(blockManager, mapManager, playerManager), this);
        pluginManager.registerEvents(blockManager, this);
        pluginManager.registerEvents(hideManager, this);
    }
    
    public static TD2Core getInstance() {
        return instance;
    }
    
    public static AsyncMySQL sql() {
        return instance.dbManager.mySQL;
    }
    
    public DiscordManager getDiscordManager() {
        return discordManager;
    }
    
    private void loadSpawnLocation() {
        spawnLocation = Utils.getLocationFromString(configManager.getConfig(ServerConfig.class).getString("spawn"));
    }
    
}
