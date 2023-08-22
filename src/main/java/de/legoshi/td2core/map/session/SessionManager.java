package de.legoshi.td2core.map.session;

import de.legoshi.td2core.map.ParkourMap;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {
    
    private final ConcurrentHashMap<Player, HashMap<ParkourMap, ParkourSession>> sessionMap;
    
    public SessionManager() {
        this.sessionMap = new ConcurrentHashMap<>();
    }
    
    public void init(Player player) {
        sessionMap.put(player, new HashMap<>());
    }
    
    public ParkourSession get(Player player, ParkourMap parkourMap) {
        return sessionMap.get(player).get(parkourMap);
    }
    
    public void put(Player player, ParkourMap parkourMap) {
        sessionMap.get(player).put(parkourMap, new ParkourSession());
    }
    
    public void put(Player player, ParkourMap parkourMap, ParkourSession session) {
        sessionMap.get(player).put(parkourMap, session);
    }
    
    public void removeAll(Player player) {
        sessionMap.remove(player);
    }
    
    public void remove(Player player, ParkourMap parkourMap) {
        sessionMap.get(player).remove(parkourMap);
    }
    
    public boolean contains(Player player, ParkourMap parkourMap) {
        return sessionMap.get(player).containsKey(parkourMap);
    }
    
}
