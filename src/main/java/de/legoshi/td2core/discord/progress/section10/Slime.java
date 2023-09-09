package de.legoshi.td2core.discord.progress.section10;

import de.legoshi.td2core.discord.progress.ProgressChannel;
import de.legoshi.td2core.discord.progress.ProgressMap;

public class Slime extends ProgressMap {
    
    public Slime(ProgressChannel channel) {
        super("Slime");
    
        add(channel.getRoleMentionByName("Section 10 | Second Drop") + ": ");
        add(channel.getRoleMentionByName("Section 10 | Third Drop") + ": ");
    }
    
    @Override
    public void addProgressLine() {
        add("__First Drop__");
    }
    
    @Override
    public void addProgressMapping() {
        addProgressMapping(1, 1);
        addProgressMapping(2, 2);
    }
    
    @Override
    public void addRoleMapping() {
        addRoleMapping(1, "Section 10 | Second Drop");
        addRoleMapping(2, "Section 10 | Third Drop");
        addRoleMapping(3, "Section 10 | Slime VICTOR");
    }
}
