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
        addProgressMapping(1, 5);
        addProgressMapping(5, 9);
        addProgressMapping(6, 13);
    }
    
    @Override
    public void addRoleMapping() {
        addRoleMapping(9, "Section 6 | Redstone Sky Half");
        addRoleMapping(16, "Section 6 | Redstone Sky VICTOR");
    }
    
}
