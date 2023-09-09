package de.legoshi.td2core.discord.progress.section9;

import de.legoshi.td2core.discord.progress.ProgressMap;

public class TD1Buffed extends ProgressMap {
    
    public TD1Buffed() {
        super("TD1 Buffed");
    }
    
    @Override
    public void addProgressLine() {
        add("*CP1-CP5*");
        add("**CP6-CP10**: ");
        add("**CP11-CP15**: ");
        add("------------------------------------------");
        add("Section 9 | TD1 Buffed CP 16+");
        add("------------------------------------------");
        add("**CP16-CP20**: ");
        add("**CP21-CP25**: ");
        add("------------------------------------------");
        add("Section 9 | TD1 Buffed CP 26+");
        add("------------------------------------------");
        add("**CP26-CP30**: ");
        add("**CP31-CP35**: ");
        add("------------------------------------------");
        add("Section 9 | TD1 Buffed CP 36+");
        add("------------------------------------------");
        add("**CP36-CP39**: ");
        add("------------------------------------------");
        add("Section 9 | TD1 Buffed SKY");
        add("------------------------------------------");
        add("**Sky**: ");
    }
    
    @Override
    public void addProgressMapping() {
        addProgressMapping(1, 1);
        addProgressMapping(6, 2);
        addProgressMapping(11, 6);
        addProgressMapping(16, 7);
        addProgressMapping(21, 11);
        addProgressMapping(26, 12);
        addProgressMapping(31, 16);
        addProgressMapping(36, 20);
    }
    
    @Override
    public void addRoleMapping() {
        addRoleMapping(16, "Section 9 | TD1 Buffed CP 16+");
        addRoleMapping(26, "Section 9 | TD1 Buffed CP 26+");
        addRoleMapping(36, "Section 9 | TD1 Buffed CP 36+");
        addRoleMapping(40, "Section 9 | TD1 Buffed SKY");
        addRoleMapping(41, "Section 9 | TD1 Buffed VICTOR");
    }
}
