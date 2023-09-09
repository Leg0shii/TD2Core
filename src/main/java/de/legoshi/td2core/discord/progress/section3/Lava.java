package de.legoshi.td2core.discord.progress.section3;

import de.legoshi.td2core.discord.progress.ProgressChannel;
import de.legoshi.td2core.discord.progress.ProgressMap;

public class Lava extends ProgressMap {
    
    public Lava(ProgressChannel channel) {
        super("Lava");
    
        add(channel.getRoleMentionByName("Section 3 | Lava Chapter 2") + " (CP16-CP30): ");
        add(channel.getRoleMentionByName("Section 3 | Lava Chapter 3") + " (CP31-CP45): ");
        add(channel.getRoleMentionByName("Section 3 | Lava Chapter 4") + " (CP46-CP60): ");
        add(channel.getRoleMentionByName("Section 3 | Lava Chapter 5") + " (CP61-CP75): ");
        add(channel.getRoleMentionByName("Section 3 | Lava Chapter 6") + " (CP76-CP90): ");
        add(channel.getRoleMentionByName("Section 3 | Lava Chapter 7") + " (CP91-CP105): ");
        add(channel.getRoleMentionByName("Section 3 | Lava Chapter 8") + " (CP106-CP120): ");
        add(channel.getRoleMentionByName("Section 3 | Lava Chapter 9") + " (CP121-CP135): ");
        add(channel.getRoleMentionByName("Section 3 | Lava Chapter 10") + " (CP136-CP150): ");
        add(channel.getRoleMentionByName("Section 3 | Lava Chapter 11") + " (CP151-CP165): ");
        add(channel.getRoleMentionByName("Section 3 | Lava Chapter 12") + " (CP166-CP182): ");
    }
    
    @Override
    public void addProgressLine() {
        add("__Lava Chapter 1 (CP1-CP15) __");
    }
    
    @Override
    public void addProgressMapping() {
        addProgressMapping(16, 1);
        addProgressMapping(31, 2);
        addProgressMapping(46, 3);
        addProgressMapping(61, 4);
        addProgressMapping(76, 5);
        addProgressMapping(91, 6);
        addProgressMapping(106, 7);
        addProgressMapping(121, 8);
        addProgressMapping(136, 9);
        addProgressMapping(151, 10);
        addProgressMapping(166, 11);
    }
    
    @Override
    public void addRoleMapping() {
        addRoleMapping(16, "Section 3 | Lava Chapter 2");
        addRoleMapping(31, "Section 3 | Lava Chapter 3");
        addRoleMapping(46, "Section 3 | Lava Chapter 4");
        addRoleMapping(61, "Section 3 | Lava Chapter 5");
        addRoleMapping(76, "Section 3 | Lava Chapter 6");
        addRoleMapping(91, "Section 3 | Lava Chapter 7");
        addRoleMapping(106, "Section 3 | Lava Chapter 8");
        addRoleMapping(121, "Section 3 | Lava Chapter 9");
        addRoleMapping(136, "Section 3 | Lava Chapter 10");
        addRoleMapping(151, "Section 3 | Lava Chapter 11");
        addRoleMapping(166, "Section 3 | Lava Chapter 12");
        addRoleMapping(183, "Section 3 | Lava VICTOR");
    }
}
