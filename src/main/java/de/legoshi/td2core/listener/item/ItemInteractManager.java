package de.legoshi.td2core.listener.item;

import de.legoshi.td2core.TD2Core;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class ItemInteractManager {
    
    private static final Set<Player> set = new HashSet<>();
    
    public static void add(Player player) {
        if (hasClicked(player)) return;
        
        set.add(player);
        Bukkit.getScheduler().runTaskLater(TD2Core.getInstance(), () -> set.remove(player), 1);
    }
    
    public static boolean hasClicked(Player player) {
        return set.contains(player);
    }
    
}
