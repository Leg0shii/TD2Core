package de.legoshi.td2core.gui;

import de.legoshi.td2core.TD2Core;
import de.legoshi.td2core.block.BlockManager;
import de.legoshi.td2core.util.CustomHeads;
import de.legoshi.td2core.util.Message;
import de.themoep.inventorygui.GuiStateElement;
import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CheckPointGUI extends GUIPane {
    
    private final String[] guiSetup = {
        "ggggggggg",
        "gagbgcgdg",
        "ggggggggq"
    };
    private BlockManager blockManager;
    private Location selectedCP;
    
    public void openGui(Player player, BlockManager blockManager, Location selectedCP) {
        super.openGui(player, null);
        this.current = new InventoryGui(TD2Core.getInstance(), player, "Checkpoint Editor", guiSetup);
        this.blockManager = blockManager;
        this.selectedCP = selectedCP;
        registerGuiElements();
        fullCloseOnEsc();
        this.current.show(this.holder);
    }
    
    @Override
    protected void registerGuiElements() {
        StaticGuiElement preciseCoordsElement = getPreciseCoordsElement();
        StaticGuiElement nextCPElement = getNextCPElement();
        GuiStateElement activeCheckpoints = getGuiStateElement();
        GuiStateElement cpIndexElement = getCPIndexStateElement();
        
        if (blockManager.isCheckpoint(selectedCP)) activeCheckpoints.setState("checkpointEnabled");
        else activeCheckpoints.setState("checkpointDisabled");
        
        if (blockManager.getClickedCPIndex(selectedCP) == -1) cpIndexElement.setState("cpIndexDisabled");
        else cpIndexElement.setState("cpIndexEnabled");
        
        this.current.addElements(preciseCoordsElement, nextCPElement, activeCheckpoints, cpIndexElement);
    }
    
    private StaticGuiElement getPreciseCoordsElement() {
        Location preciseCoords = blockManager.getTeleportPos(selectedCP);
        String usedCoords = getPrecisionString(preciseCoords);
        
        return new StaticGuiElement(
            'a',
            new ItemStack(Material.SIGN, 1, (short) 0),
            click -> {
                blockManager.addTempPreciseData(this.holder, selectedCP);
                this.holder.closeInventory();
                this.holder.sendMessage(Message.SPC_USAGE.getInfoMessage());
                return true;
            },
            "§7Set precise coords",
            usedCoords,
            "§8Click to set precise coords"
            );
    }
    
    private String getPrecisionString(Location location) {
        String usedCoords = "";
        if (location != null) {
            usedCoords = "§7Currently used coords\n" +
                "§7X: §6" + location.getX() + "\n" +
                "§7Y: §6" + location.getY() + "\n" +
                "§7Z: §6" + location.getZ() + "\n" +
                "§7Yaw: §6" + location.getYaw()  + "\n" +
                "§7Pitch: §6" + location.getPitch()  + "\n";
        }
        return usedCoords;
    }
    
    private StaticGuiElement getNextCPElement() {
        Location nextCPCoords = blockManager.getNextCPLocation(selectedCP);
        String usedCoords = getNextCPString(nextCPCoords);
        
        return new StaticGuiElement(
            'b',
            new ItemStack(Material.COMPASS, 1, (short) 0),
            click -> {
                blockManager.addTempNextData(this.holder, selectedCP);
                this.holder.closeInventory();
                this.holder.sendMessage(Message.PRECISE_COORDS_USAGE.getInfoMessage());
                return true;
            },
            "§7Set next pressure plate",
            usedCoords,
            "§8Click to set new next pressure plate"
        );
    }
    
    private String getNextCPString(Location location) {
        String usedCoords = "";
        if (location != null) {
            usedCoords = "§7Currently connected pressure plate\n" +
                "§7X: §6 " + location.getX() + "\n" +
                "§7Y: §6 " + location.getY() + "\n" +
                "§7Z: §6 " + location.getZ();
        }
        return usedCoords;
    }
    
    private GuiStateElement getGuiStateElement() {
        return new GuiStateElement('c',
            new GuiStateElement.State(
                change -> {
                    blockManager.addIsCheckpoint(selectedCP, true);
                    this.holder.closeInventory();
                    this.holder.sendMessage(Message.ACTIVATED_CP.getSuccessMessage());
                },
                "checkpointEnabled",
                CustomHeads.checkMark,
                ChatColor.GREEN + "Active Checkpoint",
                "§8By clicking here you will deactivate the checkpoint."
            ),
            new GuiStateElement.State(
                change -> {
                    blockManager.addIsCheckpoint(selectedCP, false);
                    this.holder.closeInventory();
                    this.holder.sendMessage(Message.DEACTIVATED_CP.getSuccessMessage());
                },
                "checkpointDisabled",
                CustomHeads.crossMark,
                ChatColor.RED + "Not Active Checkpoint",
                "§8By clicking here you will activate the checkpoint again."
            )
        );
    }
    
    private GuiStateElement getCPIndexStateElement() {
        return new GuiStateElement('d',
            new GuiStateElement.State(
                change -> {
                    blockManager.addIsCPIndex((Player) change.getWhoClicked(), selectedCP);
                    this.holder.closeInventory();
                    this.holder.sendMessage(Message.ACTIVATE_INDEX_CP.getInfoMessage());
                },
                "cpIndexEnabled",
                CustomHeads.indexEnabled,
                ChatColor.GREEN + "Checkpoint index active: §6" + blockManager.getClickedCPIndex(selectedCP)
            ),
            new GuiStateElement.State(
                change -> {
                    blockManager.addIsCPIndex((Player) change.getWhoClicked(), selectedCP);
                    this.holder.closeInventory();
                    this.holder.sendMessage(Message.ACTIVATE_INDEX_CP.getSuccessMessage());
                },
                "cpIndexDisabled",
                CustomHeads.indexDisabled,
                ChatColor.RED + "Checkpoint index not active: §6" + blockManager.getClickedCPIndex(selectedCP)
            )
        );
    }
    
}
