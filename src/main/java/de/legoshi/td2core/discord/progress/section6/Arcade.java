package de.legoshi.td2core.discord.progress.section6;

import de.legoshi.td2core.discord.progress.ProgressMap;

public class Arcade extends ProgressMap {
    
    public Arcade() {
        super("Arcade");
    }
    
    @Override
    public void addProgressLine() {
        add("__Start__");
        add("**M1**: ");
        add("------------------------------------------");
        add("Section 6 | Arcade M2+");
        add("------------------------------------------");
        add("**M2**: ");
        add("------------------------------------------");
        add("Section 6 | Arcade M3+");
        add("------------------------------------------");
        add("**M3**: ");
        add("**M4**: ");
        add("------------------------------------------");
        add("Section 6 | Arcade M5+");
        add("------------------------------------------");
        add("**M5**: ");
        add("------------------------------------------");
        add("Section 6 | Arcade SKY");
        add("------------------------------------------");
        add("**Sky**: ");
    }
    
    @Override
    public void addProgressMapping() {
        addProgressMapping(1, 1);
        addProgressMapping(2, 5);
        addProgressMapping(3, 9);
        addProgressMapping(4, 10);
        addProgressMapping(5, 14);
        addProgressMapping(6, 18);
    }
    
    @Override
    public void addRoleMapping() {
        addRoleMapping(2, "Section 6 | Arcade M2+");
        addRoleMapping(3, "Section 6 | Arcade M3+");
        addRoleMapping(5, "Section 6 | Arcade M5+");
        addRoleMapping(6, "Section 6 | Arcade SKY");
        addRoleMapping(7, "Section 6 | Arcade VICTOR");
    }
}
