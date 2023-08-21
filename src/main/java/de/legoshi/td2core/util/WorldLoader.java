package de.legoshi.td2core.util;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;

public class WorldLoader {
    
    public static void loadWorlds() {
        World td1_ = Bukkit.getWorld("td1_");
        World section6 = Bukkit.getWorld("section6");
        if (td1_ == null) {
            Bukkit.createWorld(new WorldCreator("td1_"));
        }
        if (section6 == null) {
            Bukkit.createWorld(new WorldCreator("section6"));
        }
    }
    
}
