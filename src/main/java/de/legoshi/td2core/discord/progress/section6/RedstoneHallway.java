package de.legoshi.td2core.discord.progress.section6;

import de.legoshi.td2core.discord.progress.ProgressMap;

public class RedstoneHallway extends ProgressMap {
    
    public RedstoneHallway() {
        super("Redstone Hallway");
    }
    
    @Override
    public void addProgressLine() {
        add("*CP1-CP4*");
        add("**CP5-CP8**: ");
        add("------------------------------------------");
        add("Section 6 | Redstone Hallway Half");
        add("------------------------------------------");
        add("**CP9-CP12**: ");
        add("**CP13-CP16**: ");
    }
    
    @Override
    public void addProgressMapping() {
        addProgressMapping(5, 1);
        addProgressMapping(9, 5);
        addProgressMapping(13, 6);
    }
    
    @Override
    public void addRoleMapping() {
        addRoleMapping(9, "Section 6 | Redstone Hallway Half");
        addRoleMapping(16, "Section 6 | Redstone Hallway VICTOR");
    }
}
