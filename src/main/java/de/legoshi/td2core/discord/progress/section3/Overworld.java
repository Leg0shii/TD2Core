package de.legoshi.td2core.discord.progress.section3;

import de.legoshi.td2core.discord.progress.ProgressChannel;
import de.legoshi.td2core.discord.progress.ProgressMap;

public class Overworld extends ProgressMap {
    
    public Overworld(ProgressChannel progressChannel) {
        super("Overworld");
        
        add(progressChannel.getRoleMentionByName("Section 3 | Overworld Chapter 2") + " (CP12-CP30): ");
        add(progressChannel.getRoleMentionByName("Section 3 | Overworld Chapter 3") + " (CP31-CP42): ");
        add(progressChannel.getRoleMentionByName("Section 3 | Overworld Chapter 4") + " (CP43-CP56): ");
        add(progressChannel.getRoleMentionByName("Section 3 | Overworld Chapter 5") + " (CP57-CP71): ");
    }
    
    @Override
    public void addProgressLine() {
        add("__Chapter 1 (CP1-CP11)__");
    }
    
    @Override
    public void addProgressMapping() {
        addProgressMapping(12, 1);
        addProgressMapping(31, 2);
        addProgressMapping(43, 3);
        addProgressMapping(57, 4);
    }
    
    @Override
    public void addRoleMapping() {
        addRoleMapping(12, "Section 3 | Overworld Chapter 2");
        addRoleMapping(31, "Section 3 | Overworld Chapter 3");
        addRoleMapping(43, "Section 3 | Overworld Chapter 4");
        addRoleMapping(57, "Section 3 | Overworld Chapter 5");
        addRoleMapping(71, "Section 3 | Overworld VICTOR");
    }
}
