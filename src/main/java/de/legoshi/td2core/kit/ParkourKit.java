package de.legoshi.td2core.kit;

import de.legoshi.td2core.util.ItemUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ParkourKit extends Kit {
    
    protected ItemStack checkPointTool = new ItemStack(Material.INK_SACK, 1, (short) 1);
    protected ItemStack leaveTool = new ItemStack(Material.INK_SACK, 1, (short) 8);
    protected ItemStack practiceTool = new ItemStack(Material.SLIME_BALL, 1);
    protected ItemStack shield = new ItemStack(Material.SHIELD);
    protected ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
    
    public ParkourKit(Integer version) {
        super(version);
        
        checkPointTool = ItemUtils.setItemText(checkPointTool, "§aBack to Checkpoint");
        practiceTool = ItemUtils.setItemText(practiceTool, "§ePractice");
        leaveTool = ItemUtils.setItemText(leaveTool, "§cBack to Lobby");
        shield = ItemUtils.setItemText(shield, "§eAlign Yourself");
        sword = ItemUtils.setItemText(sword, "§eAlign Yourself");
    
        checkPointTool = ItemUtils.addNbtId(checkPointTool, "cp");
        practiceTool = ItemUtils.addNbtId(practiceTool, "prac");
        leaveTool = ItemUtils.addNbtId(leaveTool, "leave");
        shield = ItemUtils.addNbtId(shield, "align");
        sword = ItemUtils.addNbtId(sword, "align");
    
        inventoryMap.setItem(0, checkPointTool);
        inventoryMap.setItem(1, practiceTool);
        inventoryMap.setItem(7, helpTool);
        inventoryMap.setItem(8, leaveTool);
        
        applyPracticeTool(6);
    }
    
    public ParkourKit(ItemInventoryMap inventoryMap, Integer version) {
        super(inventoryMap, version);
        for (int slot : inventoryMap.getInventoryMap().keySet()) {
            if (inventoryMap.getInventoryMap().get(slot).getType().equals(Material.DIAMOND_SWORD) && is12Version()) {
                inventoryMap.setItem(slot, shield);
            } else if (inventoryMap.getInventoryMap().get(slot).getType().equals(Material.SHIELD) && !is12Version()) {
                inventoryMap.setItem(slot, sword);
            }
        }
    }
    
    protected void applyPracticeTool(int count) {
        inventoryMap.setItem(count, shield);
        if (!is12Version()) inventoryMap.setItem(count, sword);
    }
    
    private boolean is12Version() {
        return version >= 335 && version <= 340;
    }
    
}
