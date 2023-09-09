package de.legoshi.td2core.discord.progress.section5;

import de.legoshi.td2core.discord.progress.ProgressChannel;
import de.legoshi.td2core.discord.progress.ProgressMap;

public class OneJump extends ProgressMap {
    
    public OneJump(ProgressChannel channel) {
        super("160 OneJumps");
    
        add(channel.getRoleMentionByName("Section 5 | OneJump II") + " (11CP-20CP): ");
        add(channel.getRoleMentionByName("Section 5 | OneJump III") + " (21CP-30CP): ");
        add(channel.getRoleMentionByName("Section 5 | OneJump IV") + " (31CP-40CP): ");
        add(channel.getRoleMentionByName("Section 5 | OneJump V") + " (41CP-50CP): ");
        add(channel.getRoleMentionByName("Section 5 | OneJump VI") + " (51CP-60CP): ");
        add(channel.getRoleMentionByName("Section 5 | OneJump VII") + " (61CP-70CP): ");
        add(channel.getRoleMentionByName("Section 5 | OneJump VIII") + " (71CP-80CP): ");
        add(channel.getRoleMentionByName("Section 5 | OneJump IX") + " (81CP-90CP): ");
        add(channel.getRoleMentionByName("Section 5 | OneJump X") + " (91CP-100CP): ");
        add(channel.getRoleMentionByName("Section 5 | OneJump XI") + " (101CP-110CP): ");
        add(channel.getRoleMentionByName("Section 5 | OneJump XII") + " (111CP-120CP): ");
        add(channel.getRoleMentionByName("Section 5 | OneJump XIII") + " (121CP-130CP): ");
        add(channel.getRoleMentionByName("Section 5 | OneJump XIV") + " (131CP-140CP): ");
        add(channel.getRoleMentionByName("Section 5 | OneJump XV") + " (141CP-150CP): ");
        add(channel.getRoleMentionByName("Section 5 | OneJump XVI") + " (151CP-160CP): ");
    }
    
    @Override
    public void addProgressLine() {
        add("__Section 5 | OneJump I (1CP-10CP)__");
    }
    
    @Override
    public void addProgressMapping() {
        addProgressMapping(11, 1);
        addProgressMapping(21, 2);
        addProgressMapping(31, 3);
        addProgressMapping(41, 4);
        addProgressMapping(51, 5);
        addProgressMapping(61, 6);
        addProgressMapping(71, 7);
        addProgressMapping(81, 8);
        addProgressMapping(91, 9);
        addProgressMapping(101, 10);
        addProgressMapping(111, 11);
        addProgressMapping(121, 12);
        addProgressMapping(131, 13);
        addProgressMapping(141, 14);
        addProgressMapping(151, 15);
    }
    
    @Override
    public void addRoleMapping() {
        addRoleMapping(11, "Section 5 | OneJump II");
        addRoleMapping(21, "Section 5 | OneJump III");
        addRoleMapping(31, "Section 5 | OneJump IV");
        addRoleMapping(41, "Section 5 | OneJump V");
        addRoleMapping(51, "Section 5 | OneJump VI");
        addRoleMapping(61, "Section 5 | OneJump VII");
        addRoleMapping(71, "Section 5 | OneJump VIII");
        addRoleMapping(81, "Section 5 | OneJump IX");
        addRoleMapping(91, "Section 5 | OneJump X");
        addRoleMapping(101, "Section 5 | OneJump XI");
        addRoleMapping(111, "Section 5 | OneJump XII");
        addRoleMapping(121, "Section 5 | OneJump XIII");
        addRoleMapping(131, "Section 5 | OneJump XIV");
        addRoleMapping(141, "Section 5 | OneJump XV");
        addRoleMapping(151, "Section 5 | OneJump XVI");
        addRoleMapping(160, "Section 5 | OneJump VICTOR");
    }
}
