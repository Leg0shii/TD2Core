package de.legoshi.td2core.discord.progress.section3;

import de.legoshi.td2core.discord.progress.ProgressChannel;
import de.legoshi.td2core.discord.progress.ProgressMap;

public class Cave extends ProgressMap {
    
    public Cave(ProgressChannel channel) {
        super("Cave");
    
        add(channel.getRoleMentionByName("Section 3 | Cave Chapter 2") + " (CP8-CP20): ");
        add(channel.getRoleMentionByName("Section 3 | Cave Chapter 3") + " (CP21-CP45): ");
        add(channel.getRoleMentionByName("Section 3 | Cave Chapter 4") + " (CP46-CP50): ");
        add(channel.getRoleMentionByName("Section 3 | Cave Chapter 5") + " (CP51-CP74): ");
        add(channel.getRoleMentionByName("Section 3 | Cave Chapter 6") + " (CP75-CP85): ");
        add(channel.getRoleMentionByName("Section 3 | Cave Chapter 7") + " (CP86-CP102): ");
    }
    
    @Override
    public void addProgressLine() {
        add("__Chapter 1 (CP1-CP7)__");
    }
    
    @Override
    public void addProgressMapping() {
        addProgressMapping(8, 1);
        addProgressMapping(21, 2);
        addProgressMapping(46, 3);
        addProgressMapping(51, 4);
        addProgressMapping(75, 5);
        addProgressMapping(86, 6);
    }
    
    @Override
    public void addRoleMapping() {
        addRoleMapping(8, "Section 3 | Cave Chapter 2");
        addRoleMapping(21, "Section 3 | Cave Chapter 3");
        addRoleMapping(46, "Section 3 | Cave Chapter 4");
        addRoleMapping(51, "Section 3 | Cave Chapter 5");
        addRoleMapping(75, "Section 3 | Cave Chapter 6");
        addRoleMapping(86, "Section 3 | Cave Chapter 7");
        addRoleMapping(102, "Section 3 | Cave VICTOR");
    }
}
