package de.legoshi.td2core.player.tag;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PlayerTag {
    
    private String prefix;
    private String team;
    
    public String getTag() {
        return prefix;
    }
    
}
