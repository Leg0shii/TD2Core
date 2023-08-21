package de.legoshi.td2core.kit;

import de.legoshi.td2core.util.CustomHeads;
import de.legoshi.td2core.util.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public abstract class Kit {
    
    protected Inventory inventory;
    protected ItemStack helpTool = CustomHeads.helpHead;
    
    public Kit() {
        inventory = Bukkit.createInventory(null, 9, "");
        helpTool = ItemUtils.setItemText(helpTool, "§dClick me for help");
        helpTool = ItemUtils.addNbtId(helpTool, "help");
    }
    
    public Inventory getInventory() {
        return inventory;
    }
    
}
