package de.legoshi.td2core.kit;

import de.legoshi.td2core.util.CustomHeads;
import de.legoshi.td2core.util.ItemUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class LobbyKit extends Kit {
    
    protected ItemStack mapSelector = new ItemStack(Material.NETHER_STAR, 1);
    protected ItemStack leaderboard = CustomHeads.leaderboardHead;
    
    public LobbyKit(Integer version) {
       super(version);
    
        mapSelector = ItemUtils.setItemText(mapSelector, "§eSelect Map");
        leaderboard = ItemUtils.setItemText(leaderboard, "§eLeaderboard");
    
        mapSelector = ItemUtils.addNbtId(mapSelector, "select");
        leaderboard = ItemUtils.addNbtId(leaderboard, "leaderboard");
    
        inventoryMap.setItem(0, mapSelector);
        inventoryMap.setItem(4, leaderboard);
        inventoryMap.setItem(8, helpTool);
    }
    
    public LobbyKit(ItemInventoryMap inventoryMap, Integer version) {
        super(inventoryMap, version);
    }
    
}
