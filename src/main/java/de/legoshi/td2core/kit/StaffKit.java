package de.legoshi.td2core.kit;

import de.legoshi.td2core.util.CustomHeads;
import de.legoshi.td2core.util.ItemUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class StaffKit extends PracticeKit {
    
    private ItemStack leaderboard = CustomHeads.leaderboardHead;
    private ItemStack diamondHelmet = new ItemStack(Material.DIAMOND_HELMET, 1);
    
    public StaffKit(int version) {
        super(version);
    
        diamondHelmet = ItemUtils.setItemText(diamondHelmet, "§3Diamond Helmet");
        leaderboard = ItemUtils.setItemText(leaderboard, "§eLeaderboard");
    
        leaderboard = ItemUtils.addNbtId(leaderboard, "leaderboard");
    
        inventory.setItem(0, checkPointTool);
        inventory.setItem(1, setCPTool);
        inventory.setItem(2, flyTool);
        inventory.setItem(3, diamondHelmet);
        inventory.setItem(4, leaderboard);
        inventory.setItem(6, new ItemStack(Material.AIR));
        inventory.setItem(8, helpTool);
        
        applyPracticeTool(version, 7);
    }
    
}
