package de.legoshi.td2core.gui;

import de.legoshi.td2core.TD2Core;
import de.legoshi.td2core.cache.MapLBStats;
import de.legoshi.td2core.config.PlayerConfig;
import de.legoshi.td2core.map.ParkourMap;
import de.legoshi.td2core.util.CustomHeads;
import de.legoshi.td2core.util.Utils;
import de.themoep.inventorygui.GuiElement;
import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class MapLBGUI extends GUILeaderboard<MapLBStats> {
    
    private ParkourMap parkourMap;
    
    public void openGui(Player player, InventoryGui parent, ParkourMap parkourMap) {
        this.parkourMap = parkourMap;
        super.openGui(player, parent, "§c§l" + parkourMap.getMapName() + "§7§l - Leaderboard");
    }
    
    @Override
    protected CompletableFuture<Boolean> getPage() {
        CompletableFuture<Boolean> result = new CompletableFuture<>();
        TD2Core.getInstance().mapLBCache.getLeaderboardData(parkourMap).thenAcceptAsync(map -> {
            prepareItem(result, map);
            Bukkit.getScheduler().runTask(TD2Core.getInstance(), () -> this.current.draw());
        });
        return result;
    }
    
    @Override
    protected GuiElement loadGUIElement(UUID uuid, MapLBStats stats, int count) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
        String name = player.getName();
        
        String currentCPCount = String.valueOf(stats.getCurrentCPCount());
        String totalCPCount = String.valueOf(parkourMap.getCpCount());
        double currentPlayTime = stats.getCurrentPlayTime();
        String currentFails = String.valueOf(stats.getCurrentFails());
        
        boolean hasPassed = stats.isPassed();
        String cpCompletionString = hasPassed ?
            "§eCompleted CPs: §7" + totalCPCount + " §8of " + totalCPCount:
            "§eCompleted CPs: §7" + currentCPCount + " §8of " + totalCPCount;
            
    
        String prefix = getRankPrefix(count + page * pageVolume);
    
        PlayerConfig playerConfig = (PlayerConfig) TD2Core.getInstance().config.get(PlayerConfig.fileName);
        ItemStack playerHead = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
    
        if (playerConfig.getConfig().contains(uuid + ".skin")) {
            playerHead = CustomHeads.create(playerConfig.getSkin(uuid));
        }
    
        return new StaticGuiElement(
            'g',
            playerHead,
            click -> true,
            prefix + ChatColor.GREEN + name,
            "§6➤ " + cpCompletionString,
            "§6➤ §ePlaytime: §7" + Utils.secondsToTime((int) (currentPlayTime / 1000)),
            "§6➤ §eFails: §7" + currentFails
        );
    }
    
    public HashMap<UUID, MapLBStats> sortMap(HashMap<UUID, MapLBStats> map, int page, int pageVolume) {
        Comparator<Map.Entry<UUID, MapLBStats>> comparator = (e1, e2) -> {
            boolean passed1 = e1.getValue().isPassed();
            boolean passed2 = e2.getValue().isPassed();
            if (passed1 && !passed2) return -1;
            if (!passed1 && passed2) return 1;
            
            if (!(passed1 && passed2)) {
                if (e1.getValue().getCurrentCPCount() > e2.getValue().getCurrentCPCount()) return -1;
                if (e1.getValue().getCurrentCPCount() < e2.getValue().getCurrentCPCount()) return 1;
                if (e1.getValue().getCurrentCPCount() == e2.getValue().getCurrentCPCount()) {
                    if (e1.getValue().getCurrentPlayTime() >= e2.getValue().getCurrentPlayTime()) return -1;
                    if (e1.getValue().getCurrentPlayTime() < e2.getValue().getCurrentPlayTime()) return 1;
                }
            }
            
            if (passed1 && passed2) {
                if (e1.getValue().getCurrentPlayTime() < e2.getValue().getCurrentPlayTime()) return -1;
                if (e1.getValue().getCurrentPlayTime() >= e2.getValue().getCurrentPlayTime()) return 1;
            }
            
            double time1 = e1.getValue().getCurrentPlayTime();
            double time2 = e2.getValue().getCurrentPlayTime();
            return Double.compare(time1, time2);
        };
        
        return map.entrySet()
            .stream()
            .sorted(comparator)
            .skip((long) page * pageVolume)
            .limit(pageVolume)
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                (e1, e2) -> e1,
                LinkedHashMap::new
            ));
    }
}
