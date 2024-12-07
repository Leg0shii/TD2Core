package de.legoshi.td2core.gui;

import de.legoshi.td2core.TD2Core;
import de.legoshi.td2core.block.BlockManager;
import de.legoshi.td2core.util.Message;
import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

public class PlotCheckPointGUI extends GUIPane {

    private final String[] guiSetup = {
            "ggggggggg",
            "gabgggggg",
            "ggggggggq"
    };
    private BlockManager blockManager;
    private Location selectedCP;

    public void openGui(Player player, BlockManager blockManager, Location selectedCP) {
        super.openGui(player, null);
        this.current = new InventoryGui(TD2Core.getInstance(), player, "Plate Editor", guiSetup);
        this.blockManager = blockManager;
        this.selectedCP = selectedCP;
        registerGuiElements();
        fullCloseOnEsc();
        this.current.show(this.holder);
    }

    @Override
    protected void registerGuiElements() {
        StaticGuiElement preciseCoordsElement = getPreciseCoordsElement();
        StaticGuiElement effectElement = getEffectElement();
        this.current.addElements(preciseCoordsElement, effectElement);
    }

    private StaticGuiElement getEffectElement() {
        String potions = "";
        for (PotionEffect p : blockManager.getPotionEffects(selectedCP)) {
            potions = potions + " §6- §8" + p.getType().getName() + " " + (p.getAmplifier() + 1) + "\n";
        }

        return new StaticGuiElement(
                'b',
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

}
