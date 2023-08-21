package de.legoshi.td2core.gui;

import de.legoshi.td2core.TD2Core;
import de.legoshi.td2core.cache.GlobalLBCache;
import de.themoep.inventorygui.GuiElement;
import de.themoep.inventorygui.GuiElementGroup;
import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class GUILeaderboard<T> extends GUIScrollable {
    
    private final String[] guiSetup = {
        "ggggggggu",
        "ggggggggf",
        "ggggggggq",
        "ggggggggf",
        "ggggggggd",
    };
    
    public abstract HashMap<UUID, T> sortMap(HashMap<UUID, T> map, int page, int pageVolume);
    
    protected abstract GuiElement loadGUIElement(UUID uuid, T stats, int count);
    
    public void openGui(Player player, InventoryGui parent, String boardName) {
        super.openGui(player, parent);
        this.current = new InventoryGui(TD2Core.getInstance(), player, boardName, guiSetup);
        fullCloseOnEsc();
        registerGuiElements();
        this.current.show(this.holder);
    }
    
    @Override
    protected void registerGuiElements() {
        getPage();
        StaticGuiElement filler = new StaticGuiElement('f', new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7), click -> true, " ");
        this.current.addElements(this.pageUp, this.pageDown, this.returnToParent, filler);
    }
    
    protected void prepareItem(CompletableFuture<Boolean> result, HashMap<UUID, T> map) {
        if (!loadEmptyPage(map)) {
            result.complete(false);
        }
    
        HashMap<UUID, T> sortedMap = sortMap(map, page, pageVolume);
        boolean isPageFilled = !sortedMap.isEmpty();
    
        if (isPageFilled) {
            GuiElementGroup group = new GuiElementGroup('g');
            this.current.addElement(group);
        
            AtomicInteger count = new AtomicInteger(1);
            sortedMap.forEach((uuid, stats) -> {
                GuiElement staticGuiElement = loadGUIElement(uuid, stats, count.get());
                group.addElement(staticGuiElement);
                count.getAndIncrement();
            });
        }
    }
    
    protected boolean loadEmptyPage(HashMap<UUID, T> map) {
        if (map == null || map.isEmpty()) {
            if (page == 0) {
                noDataItem('g', "No data...");
            }
            return false;
        }
        return true;
    }
    
    protected String getRankPrefix(int rank) {
        String prefix;
        if (rank == 1) {
            prefix = "§b§l" + rank + ". ";
        } else if (rank == 2) {
            prefix = "§6§l" + rank + ". ";
        } else if (rank == 3) {
            prefix = "§7§l" + rank + ". ";
        } else {
            prefix = rank + ". ";
        }
        return prefix;
    }
    
}
