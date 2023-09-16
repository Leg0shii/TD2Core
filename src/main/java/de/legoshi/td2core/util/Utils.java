package de.legoshi.td2core.util;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

public class Utils {
    
    public static String getStringFromLocation(Location l) {
        if (l == null)
            return "";
        return l.getWorld().getName() + "," + l.getX() + "," + l.getY() + "," + l.getZ() + "," + l.getYaw() + "," + l.getPitch();
    }
    
    public static Location getLocationFromString(String s) {
        if (s == null || s.trim() == "")
            return null;
        String[] parts = s.split(",");
        if (parts.length == 6) {
            World w = Bukkit.getServer().getWorld(parts[0]);
            double x = Double.parseDouble(parts[1]);
            double y = Double.parseDouble(parts[2]);
            double z = Double.parseDouble(parts[3]);
            float yaw = Float.parseFloat(parts[4]);
            float pitch = Float.parseFloat(parts[5]);
            return new Location(w, x, y, z, yaw, pitch);
        } else if (parts.length == 4) {
            World w = Bukkit.getServer().getWorld(parts[0]);
            double x = Double.parseDouble(parts[1]);
            double y = Double.parseDouble(parts[2]);
            double z = Double.parseDouble(parts[3]);
            return new Location(w, x, y, z);
        }
        return null;
    }

    public static String getStringFromItemStack(ItemStack itemStack) {
        String material = itemStack.getType().toString();
        String name = "";
        if (itemStack.hasItemMeta()) {
            name = itemStack.getItemMeta().getDisplayName();
        }
        String nbt = ItemUtils.getNBTId(itemStack);
        String data = "-";
        if (itemStack.getType().equals(Material.INK_SACK)) {
            if (name.contains("Checkpoint")) {
                data = "1";
            } else if (name.contains("Lobby")) {
                data = "8";
            }
        }
        return material + ";" + name + ";" + nbt + ";" + data;
    }
    
    public static ItemStack getItemStackFromString(String itemString) {
        String[] components = itemString.split(";");
        if(components.length != 4) {
            return new ItemStack(Material.AIR);
        }
        
        Material material = Material.valueOf(components[0]);
        String name = components[1];
        String nbt = components[2];
        String data = components[3];
        
        ItemStack itemStack = new ItemStack(material);
        if (itemStack.getType().equals(Material.INK_SACK)) {
            itemStack = new ItemStack(material, 1, (short) Integer.parseInt(data));
        } else if (itemStack.getType().equals(Material.SKULL_ITEM)) {
            if (name.contains("help")) {
                itemStack = CustomHeads.helpHead;
            } else if (name.contains("Leaderboard")) {
                itemStack = CustomHeads.leaderboardHead;
            }
        }
        
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(name);
        itemStack.setItemMeta(meta);
        
        itemStack = ItemUtils.addNbtId(itemStack, nbt);
        return itemStack;
    }
    
    public static void sendActionBar(Player p, String message) {
        if (p != null) {
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
        } else {
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
            }
        }
    }
    
    public static String secondsToTime(int totalSeconds) {
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;
        
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
    
    public static String difficultyString(double difficulty) {
        String diffString = "★★★★★★★★★★";
        diffString = ChatColor.GOLD + diffString.substring(0, (int) difficulty) + ChatColor.RESET + diffString.substring((int) difficulty, 10);
        return diffString;
    }
    
    public static boolean isOnGround(Player p) {
        double[] allowedHeights = {0.0, 0.0625, 0.09375, 0.125, 0.1875, 0.25, 0.375, 0.5, 0.5625, 0.625, 0.75, 0.8125, 0.875, 0.9375};
        double playerHeight = p.getLocation().getY();
    
        int wholePart = (int) playerHeight;
        double fractionalPart = playerHeight - wholePart;
    
        for (double height : allowedHeights) {
            if (fractionalPart == height) {
                return p.isOnGround();
            }
        }
        
        return false;
    }
    
    public static String repeatSpace(double count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(" ");
        }
        return sb.toString();
    }
    
    public static String getPlayerName(String playerID) {
        OfflinePlayer offPlayer = Bukkit.getOfflinePlayer(playerID);
        String playerName = playerID;
        if (offPlayer != null) {
            playerName = Bukkit.getOfflinePlayer(playerID).getName();
        }
        return playerName;
    }
    
    public static String getPlayerNameByUUID(String uuid) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
        String playerName = "null";
        if (player != null) {
            playerName = player.getName();
        }
        return playerName;
    }
    
}