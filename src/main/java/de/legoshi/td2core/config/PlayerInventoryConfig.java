package de.legoshi.td2core.config;

import org.bukkit.plugin.java.JavaPlugin;

public class PlayerInventoryConfig extends ConfigAccessor {

    private final static String fileName = "player_inventory.yml";

    public PlayerInventoryConfig(JavaPlugin plugin) {
        super(plugin, fileName);
    }
}
