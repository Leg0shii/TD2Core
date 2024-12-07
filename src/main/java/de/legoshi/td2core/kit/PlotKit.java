package de.legoshi.td2core.kit;

import de.legoshi.td2core.util.ItemUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class PlotKit extends StaffKit {

    public PlotKit(Integer version) {
        super(version);

        this.helmet = new ItemStack(Material.LEATHER_HELMET, 1);
        this.helmet = ItemUtils.setItemText(this.helmet, "§Leather Helmet");
    }

    public PlotKit(ItemInventoryMap inventoryMap, Integer version) {
        super(inventoryMap, version);
    }

}
