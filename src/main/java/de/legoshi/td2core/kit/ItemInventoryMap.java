package de.legoshi.td2core.kit;

import lombok.Getter;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

@Getter
public class ItemInventoryMap {
    
    private final HashMap<Integer, ItemStack> inventoryMap;
    
    public ItemInventoryMap() {
        inventoryMap = new HashMap<>();
    }
    
    public void setItem(int slot, ItemStack item) {
        inventoryMap.put(slot, item);
    }
    
}
