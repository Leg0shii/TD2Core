package de.legoshi.td2core.block;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.Location;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BlockData {
    
    private Location teleportLoc;
    private Location nextCheckpoint;
    private int timeTillNextCheckpoint = -1;
    private boolean isCheckpoint = true;
    
    public BlockData(Location teleportLoc) {
        this.teleportLoc = teleportLoc;
    }
    
}
