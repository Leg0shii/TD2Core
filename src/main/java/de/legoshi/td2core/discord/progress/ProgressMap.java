package de.legoshi.td2core.discord.progress;

import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.OptionalInt;

@Getter
public abstract class ProgressMap {
    
    private final String mapName;
    
    private List<String> progressLines = new ArrayList<>();
    private final HashMap<Integer, Integer> cpToProgressPosition = new HashMap<>();
    private final HashMap<Integer, String> cpToRole = new HashMap<>();
    
    protected ProgressMap(String mapName) {
        this.mapName = mapName;
        addProgressLine();
        addProgressMapping();
        addRoleMapping();
    }
    
    public abstract void addProgressLine();
    
    public abstract void addProgressMapping();
    
    public abstract void addRoleMapping();
    
    public List<String> getProgressLines() {
        return progressLines;
    }
    
    protected void add(String value) {
        progressLines.add(value);
    }
    
    protected void addProgressMapping(int cp, int position) {
        cpToProgressPosition.put(cp, position);
    }
    
    protected void addRoleMapping(int cp, String role) {
        cpToRole.put(cp, role);
    }
    
    public String getPlayerRole(int cpCount) {
        if (cpToRole.containsKey(cpCount)) {
            return cpToRole.get(cpCount);
        }
        int closestLowerKey = Integer.MIN_VALUE;
        for (int key : cpToRole.keySet()) {
            if (key < cpCount && key > closestLowerKey) {
                closestLowerKey = key;
            }
        }
    
        return closestLowerKey != Integer.MIN_VALUE ? cpToRole.get(closestLowerKey) : null;
    }
    
    public int getProgressPosition(int cpCount) {
        if (cpToProgressPosition.containsKey(cpCount)) {
            return cpToProgressPosition.get(cpCount);
        }
        
        int closestLowerKey = Integer.MIN_VALUE;
        for (int key : cpToProgressPosition.keySet()) {
            if (key < cpCount && key > closestLowerKey) {
                closestLowerKey = key;
            }
        }
        
        return closestLowerKey != Integer.MIN_VALUE ? cpToProgressPosition.get(closestLowerKey) : -1;
    }
    
    public String getVictorRole() {
        OptionalInt optionalMaxKey = cpToRole.keySet().stream().mapToInt(Integer::intValue).max();
        
        if (optionalMaxKey.isPresent()) {
            return cpToRole.get(optionalMaxKey.getAsInt());
        } else {
            return null;  // or any default value
        }
    }
    
    
}
