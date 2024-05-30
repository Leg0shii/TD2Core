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
import org.bukkit.potion.PotionEffect;

public class CheckPointGUI extends GUIPane {
    
    private final String[] guiSetup = {
        "ggggggggg",
        "gabecdfhg",
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
        StaticGuiElement timeElement = getTimeElement();
        GuiStateElement noSprintElement = getNoSprintStateElement();
        StaticGuiElement effectElement = getEffectElement();
        
        if (blockManager.isCheckpoint(selectedCP)) activeCheckpoints.setState("checkpointEnabled");
        else activeCheckpoints.setState("checkpointDisabled");

        if (!blockManager.isNoSprint(selectedCP)) noSprintElement.setState("sprintEnabled");
        else noSprintElement.setState("sprintDisabled");
        
        if (blockManager.getClickedCPIndex(selectedCP) == -1) cpIndexElement.setState("cpIndexDisabled");
        else cpIndexElement.setState("cpIndexEnabled");
        
        this.current.addElements(preciseCoordsElement, nextCPElement, activeCheckpoints, cpIndexElement, timeElement,
                noSprintElement, effectElement);
    }

    private StaticGuiElement getEffectElement() {
        String potions = "";
        for (PotionEffect p : blockManager.getPotionEffects(selectedCP)) {
            potions = potions + " §6- §8" + p.getType().getName() + " " + (p.getAmplifier() + 1) + "\n";
        }

        return new StaticGuiElement(
                'h',
                new ItemStack(Material.POTION),
                click -> {
                    blockManager.addPotionData(this.holder, selectedCP);
                    this.holder.closeInventory();
                    this.holder.sendMessage(Message.POTION_USAGE.getInfoMessage());
                    return true;
                },
                "§7Set potion effects",
                "§7Currently active:",
                potions,
                "§8Click to set potion effects."
        );
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
                ChatColor.GREEN + "Checkpoint index active: §6" + blockManager.getClickedCPIndex(selectedCP),
                "§8By clicking here you can set the index-th checkpoint to be activated." +
                        "\nThis is relevant for cases where players might skip checkpoints," +
                        "\nto maintain the correct checkpoint count."
            ),
            new GuiStateElement.State(
                change -> {
                    blockManager.addIsCPIndex((Player) change.getWhoClicked(), selectedCP);
                    this.holder.closeInventory();
                    this.holder.sendMessage(Message.ACTIVATE_INDEX_CP.getSuccessMessage());
                },
                "cpIndexDisabled",
                CustomHeads.indexDisabled,
                ChatColor.RED + "Checkpoint index not active",
                    "§8By clicking here you can reset the index-th checkpoint."
            )
        );
    }

    private GuiStateElement getNoSprintStateElement() {
        return new GuiStateElement('f',
                new GuiStateElement.State(
                        change -> {
                            blockManager.addIsNoSprint(selectedCP, false);
                            this.holder.closeInventory();
                            this.holder.sendMessage(Message.ACTIVATED_NO_SPRINT.getSuccessMessage());
                        },
                        "sprintEnabled",
                        new ItemStack(Material.DIAMOND_BOOTS),
                        ChatColor.RED + "Sprint active.",
                        "§8By clicking here you can disable sprinting for the checkpoint."
                ),
                new GuiStateElement.State(
                        change -> {
                            blockManager.addIsNoSprint(selectedCP, true);
                            this.holder.closeInventory();
                            this.holder.sendMessage(Message.DEACTIVATED_NO_SPRINT.getSuccessMessage());
                        },
                        "sprintDisabled",
                        new ItemStack(Material.LEATHER_BOOTS),
                        ChatColor.GREEN + "No Sprint active.",
                        "§8By clicking here you can enable sprinting for the checkpoint."
                )
        );
    }

    private StaticGuiElement getTimeElement() {
        return new StaticGuiElement(
                'e',
                new ItemStack(Material.WATCH, 1, (short) 0),
                click -> {
                    blockManager.addTimeTillNext(this.holder, selectedCP);
                    this.holder.closeInventory();
                    this.holder.sendMessage(Message.TIME_USAGE.getInfoMessage());
                    return true;
                },
                "§7Set time to reach next cp",
                "§7Current Time (in ticks): §6" + blockManager.getTimeTillNext(selectedCP),
                "§8Click to set time for next cp (in ticks)"
        );
    }
    
}
