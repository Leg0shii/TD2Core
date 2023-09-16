package de.legoshi.td2core.kit;

import de.legoshi.td2core.util.CustomHeads;
import de.legoshi.td2core.util.ItemUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class StaffKit extends PracticeKit {
    
    private ItemStack leaderboard = CustomHeads.leaderboardHead;
    private ItemStack diamondHelmet = new ItemStack(Material.DIAMOND_HELMET, 1);
    
    public StaffKit(Integer version) {
        super(version);
    
        diamondHelmet = ItemUtils.setItemText(diamondHelmet, "§3Diamond Helmet");
        leaderboard = ItemUtils.setItemText(leaderboard, "§eLeaderboard");
    
        leaderboard = ItemUtils.addNbtId(leaderboard, "leaderboard");
    
        inventoryMap.setItem(0, checkPointTool);
        inventoryMap.setItem(1, setCPTool);
        inventoryMap.setItem(2, flyTool);
        inventoryMap.setItem(3, diamondHelmet);
        inventoryMap.setItem(4, leaderboard);
        inventoryMap.setItem(6, new ItemStack(Material.AIR));
        inventoryMap.setItem(8, helpTool);
        
        applyPracticeTool(7);
    }
    
    public StaffKit(ItemInventoryMap inventoryMap, Integer version) {
        super(inventoryMap, version);
    }
    
}
