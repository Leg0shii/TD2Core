package de.legoshi.td2core.discord.progress.overall;

import de.legoshi.td2core.discord.progress.ProgressMap;

public class OverallProgress extends ProgressMap {
    
    public OverallProgress() {
        super("TD2 Overall Progression");
    }
    
    public OverallProgress(OverallChannel channel) {
        super("TD2 Overall Progression");
        
        add(channel.getRoleMentionByName("TD2 | 5%") + ": ");
        add(channel.getRoleMentionByName("TD2 | 10%") + ": ");
        add(channel.getRoleMentionByName("TD2 | 20%") + ": ");
        add(channel.getRoleMentionByName("TD2 | 30%") + ": ");
        add(channel.getRoleMentionByName("TD2 | 40%") + ": ");
        add(channel.getRoleMentionByName("TD2 | 50%") + ": ");
        add(channel.getRoleMentionByName("TD2 | 60%") + ": ");
        add(channel.getRoleMentionByName("TD2 | 70%") + ": ");
        add(channel.getRoleMentionByName("TD2 | 80%") + ": ");
        add(channel.getRoleMentionByName("TD2 | 90%") + ": ");
        add(channel.getRoleMentionByName("TD2 | 95%") + ": ");
    }
    
    @Override
    public void addProgressLine() {
    
    }
    
    @Override
    public void addProgressMapping() {
        addProgressMapping(5, 0);
        addProgressMapping(10, 1);
        addProgressMapping(20, 2);
        addProgressMapping(30, 3);
        addProgressMapping(40, 4);
        addProgressMapping(50, 5);
        addProgressMapping(60, 6);
        addProgressMapping(70, 7);
        addProgressMapping(80, 8);
        addProgressMapping(90, 9);
        addProgressMapping(95, 10);
        addProgressMapping(100, 11);
    }
    
    @Override
    public void addRoleMapping() {
        addRoleMapping(5, "TD2 | 5%");
        addRoleMapping(10, "TD2 | 10%");
        addRoleMapping(20, "TD2 | 20%");
        addRoleMapping(30, "TD2 | 30%");
        addRoleMapping(40, "TD2 | 40%");
        addRoleMapping(50, "TD2 | 50%");
        addRoleMapping(60, "TD2 | 60%");
        addRoleMapping(70, "TD2 | 70%");
        addRoleMapping(80, "TD2 | 80%");
        addRoleMapping(90, "TD2 | 90%");
        addRoleMapping(95, "TD2 | 95%");
        addRoleMapping(100, "TD2 | Completionist");
    }
    
}
