package de.legoshi.td2core.discord.progress.section6;

import de.legoshi.td2core.discord.progress.ProgressMap;

public class Castle extends ProgressMap {
    
    public Castle() {
        super("Castle");
    }
    
    @Override
    public void addProgressLine() {
        add("__Outside__");
        add("**R1**: ");
        add("**R2**: ");
        add("------------------------------------------");
        add("Section 6 | Castle R3+");
        add("------------------------------------------");
        add("**R3**: ");
        add("**R4**: ");
        add("**R5**: ");
        add("------------------------------------------");
        add("Section 6 | Castle R6+");
        add("------------------------------------------");
        add("**R6**: ");
        add("------------------------------------------");
        add("Section 6 | Castle R7+");
        add("------------------------------------------");
        add("**R7**: ");
        add("**R8**: ");
        add("------------------------------------------");
        add("Section 6 | Castle R9+");
        add("------------------------------------------");
        add("**R9**: ");
        add("**R10**: ");
        add("------------------------------------------");
        add("Section 6 | Castle SKY");
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
        addProgressMapping(7, 16);
        addProgressMapping(8, 17);
        addProgressMapping(9, 21);
        addProgressMapping(10, 22);
        addProgressMapping(11, 16);
    }
    
    @Override
    public void addRoleMapping() {
        addRoleMapping(3, "Section 6 | Castle R3+");
        addRoleMapping(6, "Section 6 | Castle R6+");
        addRoleMapping(7, "Section 6 | Castle R7+");
        addRoleMapping(9, "Section 6 | Castle R9+");
        addRoleMapping(11, "Section 6 | Castle SKY");
        addRoleMapping(12, "Section 6 | Castle VICTOR");
    }
}
