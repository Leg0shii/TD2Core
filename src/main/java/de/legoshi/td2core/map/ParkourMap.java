package de.legoshi.td2core.map;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ParkourMap {
    
    // config fields
    public int category;
    public String mapName;
    public String displayName;
    public String buildTime;
    public String head;
    private Location startLocation;
    private Location endLocation;
    
    private int cpCount;
    private int order;
    private int weight;
    private int estimatedDifficulty;
    private boolean redstone;
    
    // dynamic fields
    private List<PotionEffect> potionEffects = new ArrayList<>();
    private String fastestPlayer;
    private long fastestTime;
    
    private int totalPlays;
    private int totalCompletion;
    private long totalTime;
    private double totalFails;
    
    public ParkourMap(String mapName) {
        this.mapName = mapName;
    }
    
    public double getClearRate() {
        return (double) totalCompletion / totalPlays;
    }
    
    public long getFastestTime() {
        return (Integer.MAX_VALUE == fastestTime) ? 0L : fastestTime;
    }
    
}
