package de.legoshi.td2core.kit;

import de.legoshi.td2core.util.CustomHeads;
import de.legoshi.td2core.util.ItemUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class LobbyKit extends Kit {
    
    protected ItemStack mapSelector = new ItemStack(Material.NETHER_STAR, 1);
    protected ItemStack leaderboard = CustomHeads.leaderboardHead;
    
    public LobbyKit() {
       super();
    
        mapSelector = ItemUtils.setItemText(mapSelector, "§eSelect Map");
        leaderboard = ItemUtils.setItemText(leaderboard, "§eLeaderboard");
    
        mapSelector = ItemUtils.addNbtId(mapSelector, "select");
        leaderboard = ItemUtils.addNbtId(leaderboard, "leaderboard");
    
        inventory.setItem(0, mapSelector);
        inventory.setItem(4, leaderboard);
        inventory.setItem(8, helpTool);
    }
    
}
