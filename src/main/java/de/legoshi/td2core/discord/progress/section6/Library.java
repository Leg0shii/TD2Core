package de.legoshi.td2core.discord.progress.section6;

import de.legoshi.td2core.discord.progress.ProgressMap;

public class Library extends ProgressMap {
    
    public Library() {
        super("Library");
    }
    
    @Override
    public void addProgressLine() {
        add("__Start__");
        add("**FS1**: ");
        add("**FS2**: ");
        add("------------------------------------------");
        add("Section 6 | Library FS3+");
        add("------------------------------------------");
        add("**FS3**: ");
        add("**FS4**: ");
        add("**FS5**: ");
        add("------------------------------------------");
        add("Section 6 | Library FS6+");
        add("------------------------------------------");
        add("**FS6**: ");
        add("**FS7**: ");
        add("**FS8**: ");
        add("------------------------------------------");
        add("Section 6 | Library FS9+");
        add("------------------------------------------");
        add("**FS9**: ");
        add("**FS10**: ");
        add("------------------------------------------");
        add("Section 6 | Library First Sky");
        add("------------------------------------------");
        add("**First Sky**: ");
        add("**Room 1**: ");
        add("**Room 2**: ");
        add("------------------------------------------");
        add("Section 6 | Library Room 3+");
        add("------------------------------------------");
        add("**Room 3**: ");
        add("**Room 4**: ");
        add("------------------------------------------");
        add("Section 6 | Library SKY");
        add("------------------------------------------");
        add("**Sky**: ");
    }
    
    @Override
    public void addProgressMapping() {
        addProgressMapping(1, 1);
        addProgressMapping(2, 2);
        addProgressMapping(3, 6);
        addProgressMapping(4, 7);
        addProgressMapping(5, 8);
        addProgressMapping(6, 12);
        addProgressMapping(7, 13);
        addProgressMapping(8, 14);
        addProgressMapping(9, 18);
        addProgressMapping(10, 19);
        addProgressMapping(11, 23);
        addProgressMapping(12, 24);
        addProgressMapping(13, 25);
        addProgressMapping(14, 29);
        addProgressMapping(15, 30);
        addProgressMapping(16, 34);
    }
    
    @Override
    public void addRoleMapping() {
        addRoleMapping(3, "Section 6 | Library FS3+");
        addRoleMapping(6, "Section 6 | Library FS6+");
        addRoleMapping(9, "Section 6 | Library FS9+");
        addRoleMapping(11, "Section 6 | Library First Sky");
        addRoleMapping(14, "Section 6 | Library Room 3+");
        addRoleMapping(16, "Section 6 | Library SKY");
        addRoleMapping(17, "Section 6 | Library VICTOR");
    }
}
