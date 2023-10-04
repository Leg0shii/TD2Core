package de.legoshi.td2core.discord.progress.section9;

import de.legoshi.td2core.discord.progress.ProgressChannel;
import de.legoshi.td2core.discord.progress.ProgressMap;

public class TD1XXL extends ProgressMap {
    
    public TD1XXL(ProgressChannel channel) {
        super("XXL TD1");
        add(channel.getRoleMentionByName("Section 9 | TD1 XXL CP6+") + ": ");
        add(channel.getRoleMentionByName("Section 9 | TD1 XXL CP11+") + ": ");
    }
    
    @Override
    public void addProgressLine() {
        add("__CP1-CP5__");
    }
    
    @Override
    public void addProgressMapping() {
        addProgressMapping(6, 1);
        addProgressMapping(11, 2);
    }
    
    @Override
    public void addRoleMapping() {
        addRoleMapping(6, "Section 9 | TD1 XXL CP6+");
        addRoleMapping(11, "Section 9 | TD1 XXL CP11+");
        addRoleMapping(16, "Section 9 | TD1 XXL VICTORY");
    }
    
}
