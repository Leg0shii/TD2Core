package de.legoshi.td2core.kit;

import de.legoshi.td2core.util.CustomHeads;
import de.legoshi.td2core.util.ItemUtils;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public abstract class Kit {
    
    protected int version;
    @Setter protected ItemInventoryMap inventoryMap;
    protected ItemStack helpTool = CustomHeads.helpHead;
    
    public Kit(Integer version) {
        this.version = version;
        inventoryMap = new ItemInventoryMap();
        helpTool = ItemUtils.setItemText(helpTool, "§dClick me for help");
        helpTool = ItemUtils.addNbtId(helpTool, "help");
    }
    
    public Kit(ItemInventoryMap inventoryMap, Integer version) {
        this.version = version;
        this.inventoryMap = inventoryMap;
    }
    
    public ItemInventoryMap getInventoryMap() {
        return inventoryMap;
    }
    
    public ItemStack getItem(int slot) {
        return inventoryMap.getInventoryMap().get(slot);
    }
    
    public Set<Integer> getSlots() {
        return inventoryMap.getInventoryMap().keySet();
    }
    
}
