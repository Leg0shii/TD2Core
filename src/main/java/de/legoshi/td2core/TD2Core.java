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
import de.legoshi.td2core.discord.RoleManager;
import de.legoshi.td2core.discord.VerifyManager;
import de.legoshi.td2core.kit.Kit;
import de.legoshi.td2core.kit.KitManager;
import de.legoshi.td2core.listener.GeneralListener;
import de.legoshi.td2core.listener.ParkourListener;
import de.legoshi.td2core.map.MapManager;
import de.legoshi.td2core.map.session.SessionManager;
import de.legoshi.td2core.player.PlayerManager;
import de.legoshi.td2core.cache.GlobalLBCache;
import de.legoshi.td2core.player.hide.HideManager;
import de.legoshi.td2core.util.AnnouncementManager;
import de.legoshi.td2core.util.Utils;
import de.legoshi.td2core.util.WorldLoader;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class TD2Core extends JavaPlugin {
    
    public static boolean isShuttingDown = false;
    public PlayerManager playerManager;
    public GlobalLBCache globalLBCache;
    public MapLBCache mapLBCache;
    
    private static TD2Core instance;
    private Location spawnLocation;
    private Location tutSpawnLocation;
    private Location tutEndLocation;
    
    private MapManager mapManager;
    private SessionManager sessionManager;
    private VerifyManager verifyManager;
    private RoleManager roleManager;
    private KitManager kitManager;
    private AnnouncementManager announcementManager;
    
    private ConfigManager configManager;
    private BlockManager blockManager;
    private DBManager dbManager;
    private DiscordManager discordManager;
    private HideManager hideManager;
    
    @Override
    public void onLoad() {
        instance = this;
        
        configManager = new ConfigManager(this);
        
        verifyManager = new VerifyManager();
        sessionManager = new SessionManager();
        dbManager = new DBManager(this, configManager);
        announcementManager = new AnnouncementManager(verifyManager, configManager);
        mapManager = new MapManager(configManager);
        kitManager = new KitManager(configManager);
        playerManager = new PlayerManager(mapManager, sessionManager, kitManager);
        discordManager = new DiscordManager(configManager, playerManager, sessionManager, verifyManager);
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
        discordManager.startProgressScheduler();
        announcementManager.startAnnouncementScheduler();
        verifyManager.loadVerifiedUsers();
        
        globalLBCache.startGlobalCacheScheduler();
        mapLBCache.startMapCacheScheduler();
        blockManager.loadBlockData();
    
        loadLocation();
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
        Bukkit.getPluginCommand("kit").setExecutor(new KitCommand(kitManager, playerManager));
        Bukkit.getPluginCommand("leave").setExecutor(new LeaveCommand(playerManager));
        Bukkit.getPluginCommand("delete").setExecutor(new DeletePlayerCommand(mapManager));
        Bukkit.getPluginCommand("spawn").setExecutor(new SpawnCommand(playerManager));
        Bukkit.getPluginCommand("nv").setExecutor(new NightVisionCommand());
        Bukkit.getPluginCommand("reset").setExecutor(new ResetCommand(mapManager, playerManager, sessionManager));
        Bukkit.getPluginCommand("spc").setExecutor(new SPCCommand(blockManager));
        Bukkit.getPluginCommand("staff").setExecutor(new StaffCommand(playerManager));
        Bukkit.getPluginCommand("hide").setExecutor(new HideCommand(hideManager));
        Bukkit.getPluginCommand("show").setExecutor(new ShowCommand(hideManager));
        Bukkit.getPluginCommand("hideall").setExecutor(new HideAllCommand(hideManager));
        Bukkit.getPluginCommand("showall").setExecutor(new ShowAllCommand(hideManager));
        Bukkit.getPluginCommand("verify").setExecutor(new VerifyCommand(verifyManager));
        Bukkit.getPluginCommand("tp").setExecutor(new TPCommand());
        Bukkit.getPluginCommand("cp").setExecutor(new CPCountCommand(blockManager));
        Bukkit.getPluginCommand("discord").setExecutor(new DiscordCommand());
        Bukkit.getPluginCommand("leaderboard").setExecutor(new LeaderboardCommand(playerManager, configManager));
    }
    
    private void registerEvents() {
        PluginManager pluginManager = Bukkit.getServer().getPluginManager();
        pluginManager.registerEvents(new GeneralListener(configManager, playerManager), this);
        pluginManager.registerEvents(new ParkourListener(blockManager, mapManager, playerManager, sessionManager, configManager), this);
        pluginManager.registerEvents(blockManager, this);
        pluginManager.registerEvents(hideManager, this);
    }
    
    public static TD2Core getInstance() {
        return instance;
    }
    
    public static Location getSpawn() {
        return instance.spawnLocation;
    }
    
    public static Location getTutSpawn() {
        return instance.tutSpawnLocation;
    }
    
    public static Location getEndTut() {
        return instance.tutEndLocation;
    }
    
    public static AsyncMySQL sql() {
        return instance.dbManager.mySQL;
    }
    
    public DiscordManager getDiscordManager() {
        return discordManager;
    }
    
    private void loadLocation() {
        spawnLocation = Utils.getLocationFromString(configManager.getConfig(ServerConfig.class).getString("spawn"));
        tutSpawnLocation = Utils.getLocationFromString(configManager.getConfig(ServerConfig.class).getString("tutspawn"));
        tutEndLocation = Utils.getLocationFromString(configManager.getConfig(ServerConfig.class).getString("tutend"));
    }
    
    public static boolean isServerGUI(String name) {
        return name.equals("Section Selection") || name.contains("Leaderboard") || name.contains("Map Selection")
            || name.equals("§7§lLeaderboard") || name.equals("Checkpoint Editor");
    }
    
}
