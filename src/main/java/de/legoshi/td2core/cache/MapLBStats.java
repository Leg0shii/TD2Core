package de.legoshi.td2core.cache;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MapLBStats {
    
    private boolean passed;
    private int currentCPCount;
    private double currentPlayTime;
    private int currentFails;
    
}
