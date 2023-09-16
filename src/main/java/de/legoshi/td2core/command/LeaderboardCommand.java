package de.legoshi.td2core.command;

import de.legoshi.td2core.TD2Core;
import de.legoshi.td2core.cache.MapLBCache;
import de.legoshi.td2core.config.ConfigManager;
import de.legoshi.td2core.gui.GlobalLBGUI;
import de.legoshi.td2core.gui.MapLBGUI;
import de.legoshi.td2core.map.ParkourMap;
import de.legoshi.td2core.player.ParkourPlayer;
import de.legoshi.td2core.player.PlayerManager;
import de.legoshi.td2core.player.PlayerState;
import de.legoshi.td2core.util.Message;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class LeaderboardCommand implements CommandExecutor {
    
    private final PlayerManager playerManager;
    private final ConfigManager configManager;
    
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(Message.ERROR_NOT_A_PLAYER.getWarningMessage());
            return false;
        }
    
        Player player = (Player) commandSender;
        ParkourPlayer parkourPlayer = playerManager.get(player);
        
        if (strings.length == 0) {
            if (parkourPlayer.getPlayerState() == PlayerState.LOBBY) {
                player.sendMessage(Message.ERROR_IN_LOBBY.getWarningMessage());
                player.sendMessage(Message.LB_HELP.getWarningMessage());
                return false;
            } else {
                ParkourMap parkourMap = parkourPlayer.getCurrentParkourMap();
                new MapLBGUI(configManager).openGui(player, null, parkourMap);
            }
        } else if (strings.length == 1) {
            if (strings[0].equalsIgnoreCase("global")) {
                new GlobalLBGUI(configManager).openGui(player, null);
            } else {
                player.sendMessage(Message.LB_HELP.getWarningMessage());
            }
        } else {
            player.sendMessage(Message.HELP_MESSAGE_12.getMessage());
        }
        return false;
    }
    
}
