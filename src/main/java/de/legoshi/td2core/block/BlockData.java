package de.legoshi.td2core.block;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BlockData {
    
    private Location teleportLoc;
    private Location nextCheckpoint;
    private int cpIndex = -1;
    private int timeTillNextCheckpoint = -1;
    private boolean isCheckpoint = true;
    private boolean isNoSprint = false;
    private List<PotionEffect> potionEffects = new ArrayList<>();
    
    public BlockData(Location teleportLoc) {
        this.teleportLoc = teleportLoc;
    }
    
}
