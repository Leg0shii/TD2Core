package de.legoshi.td2core.listener;

import com.viaversion.viaversion.api.Via;
import de.legoshi.td2core.TD2Core;
import de.legoshi.td2core.config.ConfigManager;
import de.legoshi.td2core.config.PlayerConfig;
import de.legoshi.td2core.kit.LobbyKit;
import de.legoshi.td2core.player.ParkourPlayer;
import de.legoshi.td2core.player.PlayerManager;
import de.legoshi.td2core.player.PlayerState;
import de.legoshi.td2core.player.tag.PlayerTag;
import de.legoshi.td2core.util.Message;
import de.legoshi.td2core.util.ScoreboardUtil;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class GeneralListener implements Listener {
    
    private final PlayerConfig playerConfig;
    private final PlayerManager playerManager;
    
    public GeneralListener(ConfigManager configManager, PlayerManager playerManager) {
        this.playerConfig = configManager.getConfigAccessor(PlayerConfig.class);
        this.playerManager = playerManager;
    }
    
    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        event.setCancelled(true);
        Player player = event.getPlayer();
        PlayerTag tag = playerManager.get(player).getPlayerTag();
        Bukkit.broadcastMessage(tag.getTag() + "§7" + player.getDisplayName() + ": " + event.getMessage());
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (event.getPlayer().hasPlayedBefore()) {
            initPlayer(event);
        } else {
            Bukkit.getScheduler().runTaskLater(TD2Core.getInstance(), () -> initPlayer(event), 3L);
        }
        
        int version = Via.getAPI().getPlayerVersion(event.getPlayer().getUniqueId());
        if (!(version >= 335 && version <= 340)) {
            event.getPlayer().sendMessage(Message.PLAYER_VERSION_NOT_SUPPORTED.getMessage());
        }
    }
    
    @EventHandler
    public void onItemMove(InventoryClickEvent event) {
        if (event.getClickedInventory() == null || event.getCurrentItem() == null) {
            return;
        }
        
        Inventory topInv = event.getView().getTopInventory();
        Inventory bottomInv = event.getView().getBottomInventory();
        Inventory clickedInv = event.getClickedInventory();
        
        // If moving between inventories, cancel it
        if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
            event.setCancelled(true);
            return;
        }
        
        // If they are clicking inside their personal inventory, allow
        if (clickedInv == bottomInv) {
            return;
        }
    
        // If they are clicking inside the opened inventory, cancel it (preventing putting things inside)
        if (clickedInv == topInv && !TD2Core.isServerGUI(topInv.getName())) {
            event.setCancelled(true);
            return;
        }
        
        // Restrict SKULL_ITEM or GOLD_HELMET from being placed in the helmet slot (slot 39)
        ItemStack itemStack = event.getCurrentItem();
        if ((itemStack.getType() == Material.SKULL_ITEM || itemStack.getType() == Material.GOLD_HELMET) && event.getSlot() == 39) {
            event.setCancelled(true);
        }
    }
    
    private void initPlayer(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.teleport(TD2Core.getSpawn());
        event.setJoinMessage("");
    
        playerConfig.savePlayer(player);
        playerManager.create(player).serverJoin(playerConfig); // creates new ParkourPlayer
    }
    
    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        player.getInventory().clear();

        event.setQuitMessage("");
        playerLeave(player);
    }
    
    private void playerLeave(Player player) {
        ParkourPlayer parkourPlayer = playerManager.get(player);
        parkourPlayer.serverLeave(false);
        playerConfig.savePlayer(player);
    }
    
    @EventHandler
    public void onStatisticsUpdate(PlayerStatisticIncrementEvent event) {
        if (event.getStatistic().equals(Statistic.JUMP)) {
            ScoreboardUtil.updateJumpAndTime(event.getPlayer());
        }
        
    }

    @EventHandler
    public void onDamage(FoodLevelChangeEvent event) {
        Player player = (Player) event.getEntity();
        if (player.getFoodLevel() + 1 == event.getFoodLevel()) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        event.setCancelled(true);
    }
    
    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }
    
    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        if (Bukkit.getServer().hasWhitelist()) {
            event.setKickMessage(Bukkit.getServer().getMotd());
        }
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        if(!event.getFrom().getWorld().getName().equalsIgnoreCase("plotworld") &&
                event.getTo().getWorld().getName().equalsIgnoreCase("plotworld")) {
            ParkourPlayer parkourPlayer = playerManager.get(event.getPlayer());
            parkourPlayer.switchPlayerState(PlayerState.PLOT);
        }
    }
    
}
