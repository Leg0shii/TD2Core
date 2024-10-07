package de.legoshi.td2core.player.hide;

import de.legoshi.td2core.TD2Core;
import de.legoshi.td2core.util.Message;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;

public class HideManager implements Listener {
    
    private final Set<Player> hideAllPlayers = new HashSet<>();
    private final HashMap<Player, List<Player>> hidePlayers = new HashMap<>();
    
    public void hidePlayer(Player player, String name) {
        Player playerToHide = Bukkit.getPlayer(name);
        if (playerToHide != null) {
            player.hidePlayer(playerToHide);
            hidePlayers.computeIfAbsent(player, k -> new ArrayList<>()).add(playerToHide);
            player.sendMessage(Message.SUCCESS_HIDE_PLAYER.getSuccessMessage(name));
        } else {
            player.sendMessage(Message.ERROR_HIDE_PLAYER.getWarningMessage(name));
        }
    }
    
    public void unHidePlayer(Player player, String name) {
        Player playerToUnHide = Bukkit.getPlayer(name);
        if (playerToUnHide != null && hidePlayers.containsKey(player)) {
            player.showPlayer(playerToUnHide);
            hidePlayers.get(player).remove(playerToUnHide);
            player.sendMessage(Message.SUCCESS_SHOW_PLAYER.getSuccessMessage(name));
        } else {
            player.sendMessage(Message.ERROR_SHOW_PLAYER.getWarningMessage(name));
        }
    }
    
    public void hideAllPlayers(Player player) {
        Bukkit.getOnlinePlayers().forEach(all -> player.hidePlayer(all));
        hideAllPlayers.add(player);
        player.sendMessage(Message.SUCCESS_HIDE_ALL.getSuccessMessage());
    }
    
    public void unHideAllPlayers(Player player) {
        Bukkit.getOnlinePlayers().forEach(all -> player.showPlayer(all));
        hideAllPlayers.remove(player);
        hidePlayers.remove(player);
        player.sendMessage(Message.SUCCESS_SHOW_ALL.getSuccessMessage());
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player joinedPlayer = event.getPlayer();
        hideAllPlayers.forEach(p -> p.hidePlayer(joinedPlayer));
        hidePlayers.forEach((k, playerListToHide) ->
            playerListToHide.stream()
                .filter(p -> p.getName().equals(joinedPlayer.getName()))
                .forEach(p -> k.hidePlayer(joinedPlayer))
        );
    }
    
    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player leavePlayer = event.getPlayer();
        hideAllPlayers.remove(leavePlayer);
        hidePlayers.remove(leavePlayer);
        hidePlayers.forEach((k, playerListToHide) -> {
            playerListToHide.removeIf(p -> p.getName().equals(leavePlayer.getName()));
        });
    }
    
}
