package de.legoshi.td2core.discord.progress.section3;

import de.legoshi.td2core.discord.progress.ProgressChannel;
import de.legoshi.td2core.discord.progress.ProgressMap;

public class Overworld extends ProgressMap {
    
    public Overworld(ProgressChannel progressChannel) {
        super("Overworld");
        
        add(progressChannel.getRoleMentionByName("Section 3 | Overworld Chapter 2") + " (CP11-CP31): ");
        add(progressChannel.getRoleMentionByName("Section 3 | Overworld Chapter 3") + " (CP32-CP41): ");
        add(progressChannel.getRoleMentionByName("Section 3 | Overworld Chapter 4") + " (CP42-CP54): ");
        add(progressChannel.getRoleMentionByName("Section 3 | Overworld Chapter 5") + " (CP55-CP70): ");
    }
    
    @Override
    public void addProgressLine() {
        add("__Chapter 1 (CP1-CP10)__");
    }
    
    @Override
    public void addProgressMapping() {
        addProgressMapping(11, 1);
        addProgressMapping(32, 2);
        addProgressMapping(42, 3);
        addProgressMapping(55, 4);
    }
    
    @Override
    public void addRoleMapping() {
        addRoleMapping(11, "Section 3 | Overworld Chapter 2");
        addRoleMapping(32, "Section 3 | Overworld Chapter 3");
        addRoleMapping(42, "Section 3 | Overworld Chapter 4");
        addRoleMapping(55, "Section 3 | Overworld Chapter 5");
        addRoleMapping(68, "Section 3 | Overworld VICTOR");
    }
}
