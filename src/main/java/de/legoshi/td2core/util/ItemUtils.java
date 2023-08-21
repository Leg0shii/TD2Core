package de.legoshi.td2core.util;

import net.minecraft.server.v1_12_R1.NBTTagCompound;
import net.minecraft.server.v1_12_R1.NBTTagString;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public interface ItemUtils {
    
    static void addGlow(ItemStack item) {
        ItemMeta im = item.getItemMeta();
        im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        im.addEnchant(Enchantment.DURABILITY, 1, true);
        item.setItemMeta(im);
    }
    
    static ItemStack addNbtId(ItemStack item, String value) {
        net.minecraft.server.v1_12_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        NBTTagCompound itemCompound = (nmsItem.hasTag()) ? nmsItem.getTag() : new NBTTagCompound();
        itemCompound.set("id", new NBTTagString(value));
        nmsItem.setTag(itemCompound);
        return CraftItemStack.asBukkitCopy(nmsItem);
    }
    
    static boolean hasNbtId(ItemStack item, String value) {
        net.minecraft.server.v1_12_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        NBTTagCompound itemCompound = (nmsItem.hasTag()) ? nmsItem.getTag() : new NBTTagCompound();
        return itemCompound.getString("id").equals(value);
    }
    
    static boolean isFullInventory(Player player) {
        return player.getInventory().firstEmpty() == -1;
    }
    
    static ItemStack setItemText(ItemStack item, String... text) {
        if (item != null && text != null && text.length > 0) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                String combined = Arrays.stream(text)
                    .map(s -> s == null ? " " : s)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.joining("\n"));
                String[] lines = combined.split("\n");
                if (text[0] != null) {
                    meta.setDisplayName(lines[0]);
                }
                if (lines.length > 1) {
                    meta.setLore(Arrays.asList(Arrays.copyOfRange(lines, 1, lines.length)));
                } else {
                    meta.setLore(null);
                }
                item.setItemMeta(meta);
            }
        }
        return item;
    }
    
    static void removeItemIf(Player player, Predicate<ItemStack> predicate) {
        Inventory inv = player.getInventory();
        for (ItemStack item : inv.getContents()) {
            if (item == null || item.getType().equals(Material.AIR)) continue;
            
            if (predicate.test(item)) {
                inv.remove(item);
            }
        }
        
    }
}