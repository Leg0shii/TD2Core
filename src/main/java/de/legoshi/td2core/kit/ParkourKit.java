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
    
    public ParkourKit(int version) {
        super();
    
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
    
        inventory.setItem(0, checkPointTool);
        inventory.setItem(1, practiceTool);
        inventory.setItem(7, helpTool);
        inventory.setItem(8, leaveTool);
        
        applyPracticeTool(version, 6);
    }
    
    protected void applyPracticeTool(int version, int count) {
        inventory.setItem(count, shield);
        if (!(version >= 335 && version <= 340)) inventory.setItem(count, sword);
    }
    
}
