package de.legoshi.td2core.gui;

import de.legoshi.td2core.TD2Core;
import de.legoshi.td2core.config.PlayerConfig;
import de.legoshi.td2core.cache.GlobalLBStats;
import de.legoshi.td2core.util.CustomHeads;
import de.themoep.inventorygui.*;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class GlobalLBGUI extends GUILeaderboard<GlobalLBStats> {
    
    public void openGui(Player player, InventoryGui parent) {
        super.openGui(player, parent, "§7§lLeaderboard");
    }
    
    @Override
    protected CompletableFuture<Boolean> getPage() {
        CompletableFuture<Boolean> result = new CompletableFuture<>();
        TD2Core.getInstance().globalLBCache.getLeaderboardData().thenAcceptAsync(map -> {
            prepareItem(result, map);
            Bukkit.getScheduler().runTask(TD2Core.getInstance(), () -> this.current.draw());
        });
        
        return result;
    }
    
    @Override
    protected GuiElement loadGUIElement(UUID uuid, GlobalLBStats stats, int count) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
        PlayerConfig playerConfig = TD2Core.getInstance().configManager.getConfigAccessor(PlayerConfig.class);
        String name = player.getName();
        String playTime;
        String jumps;
        
        if (player.getPlayer() != null) {
            playTime = player.getPlayer().getStatistic(Statistic.PLAY_ONE_TICK) / (20 * 60 * 60) + " h";
            jumps = player.getPlayer().getStatistic(Statistic.JUMP) + "";
        } else {
            playTime = playerConfig.getTime(uuid) / (20 * 60 * 60) + " h";
            jumps = playerConfig.getJumps(uuid) + "";
        }
        
        String prefix = getRankPrefix(count + page * pageVolume);
        ItemStack playerHead = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        
        if (playerConfig.getConfig().contains(uuid + ".skin")) {
            playerHead = CustomHeads.create(playerConfig.getSkin(uuid));
        }
        
        return new StaticGuiElement(
            'g',
            playerHead,
            click -> true,
            prefix + ChatColor.GREEN + name,
            "§6➤ §eCompletion: §7" + ((int) (10*stats.getPercentage()))/10.0 + " %",
            "§6➤ §ePlaytime: §7" + playTime,
            "§6➤ §eJumps: §7" + jumps
        );
    }
    
    public HashMap<UUID, GlobalLBStats> sortMap(HashMap<UUID, GlobalLBStats> map, int page, int pageVolume) {
        return map.entrySet()
            .stream()
            .sorted(Map.Entry.<UUID, GlobalLBStats>comparingByValue(
                Comparator.comparingDouble(GlobalLBStats::getPercentage)
                    .thenComparingDouble(GlobalLBStats::getPlayTime).reversed()
            ))
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
