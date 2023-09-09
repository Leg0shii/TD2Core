package de.legoshi.td2core.listener.item;

import de.legoshi.td2core.TD2Core;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class ItemInteractManager {
    
    private static final Set<Player> itemSet = new HashSet<>();
    private static final Set<Player> cpSet = new HashSet<>();
    
    public static void addItem(Player player) {
        if (hasClickedItem(player)) return;
    
        itemSet.add(player);
        Bukkit.getScheduler().runTaskLater(TD2Core.getInstance(), () -> itemSet.remove(player), 1);
    }
    
    public static boolean hasClickedItem(Player player) {
        return itemSet.contains(player);
    }
    
    public static void addCP(Player player) {
        if (hasClickedCP(player)) return;
    
        cpSet.add(player);
        Bukkit.getScheduler().runTaskLater(TD2Core.getInstance(), () -> cpSet.remove(player), 20L);
    }
    
    public static boolean hasClickedCP(Player player) {
        return cpSet.contains(player);
    }
    
}
