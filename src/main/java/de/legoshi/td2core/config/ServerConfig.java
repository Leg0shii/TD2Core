package de.legoshi.td2core.config;

import de.legoshi.td2core.TD2Core;
import de.legoshi.td2core.util.Utils;
import org.bukkit.plugin.java.JavaPlugin;

public class ServerConfig extends ConfigAccessor {
    
    public static final String fileName = "td2_server.yml";
    
    public ServerConfig(JavaPlugin plugin) {
        super(plugin, fileName);
        loadConfig();
    }
    
    public void loadConfig() {
        TD2Core.getInstance().spawnLocation = Utils.getLocationFromString(getConfig().getString("spawn"));
        TD2Core.getInstance().discordToken = getConfig().getString("discord_token");
    }
    
}
