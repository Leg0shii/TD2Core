package de.legoshi.td2core.gui;

import de.legoshi.td2core.TD2Core;
import de.legoshi.td2core.config.ConfigManager;
import de.legoshi.td2core.config.PlayerConfig;
import de.legoshi.td2core.util.Message;
import de.themoep.inventorygui.GuiStateElement;
import de.themoep.inventorygui.InventoryGui;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class SettingsGUI extends GUIPane {

    private GuiStateElement practiceSetting;

    private final String[] guiSetup = {
            "ggggggggg",
            "gaggggggg",
            "ggggggggq"
    };
    private final PlayerConfig config;

    public SettingsGUI(ConfigManager configManager) {
        this.config = configManager.getConfigAccessor(PlayerConfig.class);
    }

    public void openGui(Player player, InventoryGui parent) {
        super.openGui(player, parent);
        this.current = new InventoryGui(TD2Core.getInstance(), player, "Settings", guiSetup);

        registerGuiElements();
        if (config.isPracticeAlwaysActive(player.getUniqueId())) practiceSetting.setState("flyEnabled");
        else practiceSetting.setState("flyDisabled");

        fullCloseOnEsc();
        this.current.show(this.holder);
    }

    @Override
    protected void registerGuiElements() {
        this.practiceSetting = getGuiStateElement();
        this.current.addElements(practiceSetting);
    }

    private GuiStateElement getGuiStateElement() {
        return new GuiStateElement('a',
                new GuiStateElement.State(
                        change -> {
                            UUID uuid = holder.getUniqueId();
                            this.config.setPracticeAlwaysActive(uuid, true);
                            this.holder.closeInventory();
                            this.holder.sendMessage(Message.PRACTICE_FLY_STATE_ON.getSuccessMessage());
                        },
                        "flyEnabled",
                        new ItemStack(Material.FEATHER),
                        ChatColor.GREEN + "Practice Active",
                        "§8By clicking here you will deactivate fly to be always active in practice."
                ),
                new GuiStateElement.State(
                        change -> {
                            UUID uuid = holder.getUniqueId();
                            this.config.setPracticeAlwaysActive(uuid, false);
                            this.holder.closeInventory();
                            this.holder.sendMessage(Message.PRACTICE_FLY_STATE_OFF.getSuccessMessage());
                        },
                        "flyDisabled",
                        new ItemStack(Material.FEATHER),
                        ChatColor.RED + "Practice Inactive",
                        "§8By clicking here you will activate fly to be always active in practice."
                )
        );
    }

}
