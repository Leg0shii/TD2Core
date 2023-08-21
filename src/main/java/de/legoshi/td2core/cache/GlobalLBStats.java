package de.legoshi.td2core.cache;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GlobalLBStats {
    
    private double percentage;
    private int jumps;
    private int playTime;
    
}
