package de.legoshi.td2core.discord.progress.section6;

import de.legoshi.td2core.discord.progress.ProgressMap;

public class RedstoneSky extends ProgressMap {
    
    public RedstoneSky() {
        super("Redstone Sky");
    }
    
    @Override
    public void addProgressLine() {
        add("*CP1-CP4*");
        add("**CP5-CP8**: ");
        add("------------------------------------------");
        add("Section 6 | Redstone Sky Half");
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
        addRoleMapping(9, "Section 6 | Redstone Sky Half");
        addRoleMapping(16, "Section 6 | Redstone Sky VICTOR");
    }
    
}
