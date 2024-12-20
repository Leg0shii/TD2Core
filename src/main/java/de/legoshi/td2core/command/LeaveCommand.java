package de.legoshi.td2core.command;

import de.legoshi.td2core.map.ParkourMap;
import de.legoshi.td2core.player.ParkourPlayer;
import de.legoshi.td2core.player.PlayerManager;
import de.legoshi.td2core.player.PlayerState;
import de.legoshi.td2core.util.Message;
import de.legoshi.td2core.util.Utils;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class LeaveCommand implements CommandExecutor {
    
    private final PlayerManager playerManager;
    
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(Message.ERROR_NOT_A_PLAYER.getMessage());
            return true;
        }
    
        Player player = (Player) commandSender;
        ParkourPlayer parkourPlayer = playerManager.get(player);
        
        if (parkourPlayer.getPlayerState() == PlayerState.PARKOUR && !Utils.isOnGround(player)) {
            player.sendMessage(Message.PLAYER_NOT_ON_GROUND.getWarningMessage());
            return true;
        }
        
        ParkourMap parkourMap = parkourPlayer.getCurrentParkourMap();
        
        if (parkourPlayer.getPlayerState() == PlayerState.LOBBY || parkourPlayer.getPlayerState() == PlayerState.STAFF
            || parkourPlayer.getPlayerState() == PlayerState.PLOT) {
            player.sendMessage(Message.PLAYER_NOT_IN_MAP.getWarningMessage());
            return false;
        }
        
        parkourPlayer.mapLeave(false);
        player.sendMessage(Message.PLAYER_MAP_LEAVE.getInfoMessage(parkourMap.mapName));
        return false;
    }
    
}
