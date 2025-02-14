package de.legoshi.td2core.config;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Statistic;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class PlayerConfig extends ConfigAccessor {
    
    public static final String fileName = "player_data.yml";
    
    public PlayerConfig(JavaPlugin plugin) {
        super(plugin, fileName);
    }
    
    public void savePlayer(Player player) {
        String uuid = player.getUniqueId().toString();
        getConfig().set(uuid + ".jumps", player.getStatistic(Statistic.JUMP));
        getConfig().set(uuid + ".playtime", player.getStatistic(Statistic.PLAY_ONE_TICK));
    
        if (!getConfig().contains(uuid + ".skin")) {
            CraftPlayer craftPlayer = (CraftPlayer) player;
            GameProfile profile = craftPlayer.getProfile();
        
            Property property = profile.getProperties().get("textures").iterator().next();
            String decoded = property.getValue();
            getConfig().set(uuid + ".skin", decoded);
        }

        if (!getConfig().contains(uuid + ".practice_not_always_active")) {
            getConfig().set(uuid + ".practice_not_always_active", true);
        }
        
        saveConfig();
    }
    
    public void saveSkin(UUID uuid, String skin) {
        getConfig().set(uuid.toString() + ".skin", skin);
        saveConfig();
    }
    
    public int getJumps(UUID uuid) {
        return getConfig().getInt(uuid.toString() + ".jumps");
    }
    
    public int getTime(UUID uuid) {
        return getConfig().getInt(uuid.toString() + ".playtime");
    }
    
    public String getSkin(UUID uuid) {
        return getConfig().getString(uuid.toString() + ".skin");
    }
    
    public void completeTutorial(UUID uuid) {
        getConfig().set(uuid.toString() + ".tutorial", true);
        saveConfig();
    }
    
    public boolean getTutorial(UUID uuid) {
        return !getConfig().getBoolean(uuid.toString() + ".tutorial");
    }

    public void setPracticeAlwaysActive(UUID uuid, boolean value) {
        getConfig().set(uuid.toString() + ".practice_not_always_active", !value);
    }

    public boolean isPracticeAlwaysActive(UUID uuid) {
        return !getConfig().getBoolean(uuid.toString() + ".practice_not_always_active");
    }
    
}
