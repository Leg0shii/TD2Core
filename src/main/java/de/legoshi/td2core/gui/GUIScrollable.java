package de.legoshi.td2core.gui;

import de.themoep.inventorygui.GuiElementGroup;
import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.CompletableFuture;

public abstract class GUIScrollable extends GUIPane {

    protected StaticGuiElement pageUp;
    protected StaticGuiElement pageDown;
    protected int page = 0;
    protected int pageVolume = 40;

    public void openGui(Player player, InventoryGui parent) {
        super.openGui(player, parent);
        registerPageElements();
    }

    protected abstract CompletableFuture<Boolean> getPage();
    
    private void registerPageElements() {
        this.page = 0;
        this.pageUp = new StaticGuiElement('u', new ItemStack(Material.ARROW, page + 1), (click -> {
            if (page > 0) {
                page--;
                getPage().thenAccept(isPageFilled -> {
                    if (isPageFilled) {
                        pageDown.setNumber(page + 2);
                        pageUp.setNumber(page + 1);
                        click.getGui().setPageNumber(click.getGui().getPageNumber(holder));
                        click.getGui().playClickSound();
                    }
                });
            }
            return true;
        }), "Previous page");
        
        this.pageDown = new StaticGuiElement('d', new ItemStack(Material.ARROW, page + 2), (click -> {
            page++;
            getPage().thenAccept(isPageFilled -> {
                if (isPageFilled) {
                    pageDown.setNumber(page + 2);
                    pageUp.setNumber(page + 1);
                    click.getGui().setPageNumber(click.getGui().getPageNumber(holder) + 2);
                    click.getGui().playClickSound();
                    click.getGui().setPageNumber(page);
                } else {
                    page--;
                }
            });
            return true;
        }), "Next page");
    }

    protected void noDataItem(char slot, String title) {
        GuiElementGroup group = new GuiElementGroup(slot);
        group.addElement(new StaticGuiElement(slot, new ItemStack(Material.PAPER, 1), ChatColor.RED + title));
        this.current.addElement(group);
        this.current.draw();
    }
}
