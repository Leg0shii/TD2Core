package de.legoshi.td2core.map.session;

import de.legoshi.td2core.map.ParkourMap;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {
    
    private static final ConcurrentHashMap<Player, HashMap<ParkourMap, ParkourSession>> map = new ConcurrentHashMap<>();
    
    public static void init(Player player) {
        map.put(player, new HashMap<>());
    }
    
    public static ParkourSession get(Player player, ParkourMap parkourMap) {
        return map.get(player).get(parkourMap);
    }
    
    public static void put(Player player, ParkourMap parkourMap) {
        map.get(player).put(parkourMap, new ParkourSession());
    }
    
    public static void put(Player player, ParkourMap parkourMap, ParkourSession session) {
        map.get(player).put(parkourMap, session);
    }
    
    public static void removeAll(Player player) {
        map.remove(player);
    }
    
    public static void remove(Player player, ParkourMap parkourMap) {
        map.get(player).remove(parkourMap);
    }
    
    public static boolean contains(Player player, ParkourMap parkourMap) {
        return map.get(player).containsKey(parkourMap);
    }
    
}
