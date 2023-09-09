package de.legoshi.td2core.discord.progress.section6;

import de.legoshi.td2core.discord.progress.ProgressMap;

public class City extends ProgressMap {
    
    public City() {
        super("City");
    }
    
    @Override
    public void addProgressLine() {
        add("__Building 1__");
        add("**B2**: ");
        add("**B4 bottom**: ");
        add("**B3**: ");
        add("------------------------------------------");
        add("Section 6 | City B4+");
        add("------------------------------------------");
        add("**B4**: ");
        add("------------------------------------------");
        add("Section 6 | City SKY");
        add("------------------------------------------");
        add("**SKY**: ");
    }
    
    @Override
    public void addProgressMapping() {
        addProgressMapping(1, 1);
        addProgressMapping(2, 2);
        addProgressMapping(3, 3);
        addProgressMapping(4, 7);
        addProgressMapping(5, 11);
    }
    
    @Override
    public void addRoleMapping() {
        addRoleMapping(4, "Section 6 | City B4+");
        addRoleMapping(5, "Section 6 | City SKY");
        addRoleMapping(6, "Section 6 | City VICTOR");
    }
}
