package de.legoshi.td2core.discord.progress.section6;

import de.legoshi.td2core.discord.progress.ProgressMap;

public class Space extends ProgressMap {
    public Space() {
        super("Space");
    }
    
    @Override
    public void addProgressLine() {
        add("__Start__");
        add("**Planet 1**: ");
        add("**Rocket**: ");
        add("------------------------------------------");
        add("Section 6 | Space Planet 2+");
        add("------------------------------------------");
        add("**Planet 2**: ");
        add("**Planet 3**: ");
        add("------------------------------------------");
        add("Section 6 | Space SKY");
        add("------------------------------------------");
        add("**Sky**: ");
    }
    
    @Override
    public void addProgressMapping() {
        addProgressMapping(1, 1);
        addProgressMapping(2, 2);
        addProgressMapping(3, 6);
        addProgressMapping(4, 7);
        addProgressMapping(5, 11);
    }
    
    @Override
    public void addRoleMapping() {
        addRoleMapping(3, "Section 6 | Space Planet 2+");
        addRoleMapping(5, "Section 6 | Space SKY");
        addRoleMapping(6, "Section 6 | Space VICTOR");
    }
}
