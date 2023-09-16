package de.legoshi.td2core.kit;

import de.legoshi.td2core.util.ItemUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class PracticeKit extends ParkourKit {
    
    protected ItemStack unPracticeTool = new ItemStack(Material.MAGMA_CREAM, 1);
    protected ItemStack flyTool = new ItemStack(Material.FEATHER, 1);
    protected ItemStack goldHelmet = new ItemStack(Material.GOLD_HELMET, 1);
    protected ItemStack setCPTool = new ItemStack(Material.EMERALD, 1);
    
    public PracticeKit(Integer version) {
        super(version);
    
        unPracticeTool = ItemUtils.setItemText(unPracticeTool, "§eUnpractice");
        flyTool = ItemUtils.setItemText(flyTool, "§eFly");
        goldHelmet = ItemUtils.setItemText(goldHelmet, "§eGold Helmet");
        setCPTool = ItemUtils.setItemText(setCPTool, "§eSet Checkpoint");
    
        unPracticeTool = ItemUtils.addNbtId(unPracticeTool, "unprac");
        flyTool = ItemUtils.addNbtId(flyTool, "fly");
        setCPTool = ItemUtils.addNbtId(setCPTool, "set");
    
        inventoryMap.setItem(1, unPracticeTool);
        inventoryMap.setItem(2, setCPTool);
        inventoryMap.setItem(3, flyTool);
        inventoryMap.setItem(4, goldHelmet);
    }
    
    public PracticeKit(ItemInventoryMap inventoryMap, Integer version) {
        super(inventoryMap, version);
    }
    
}
