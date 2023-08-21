package de.legoshi.td2core.command;

import de.legoshi.td2core.TD2Core;
import de.legoshi.td2core.player.ParkourPlayer;
import de.legoshi.td2core.player.PlayerManager;
import de.legoshi.td2core.player.PlayerState;
import de.legoshi.td2core.util.Message;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpawnCommand implements CommandExecutor {
    
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(Message.ERROR_NOT_A_PLAYER.getWarningMessage());
            return false;
        }
        
        Player player = (Player) commandSender;
        ParkourPlayer parkourPlayer = PlayerManager.get(player);
        
        if (parkourPlayer.getPlayerState() == PlayerState.PARKOUR || parkourPlayer.getPlayerState() == PlayerState.PRACTICE) {
            parkourPlayer.mapLeave(false);
        } else {
            player.teleport(TD2Core.getInstance().spawnLocation);
        }
        
        if (parkourPlayer.getPlayerState() != PlayerState.STAFF_MODE) {
            player.setAllowFlight(false);
        }
        player.sendMessage(Message.SUCCESS_SPAWN.getSuccessMessage());
        return false;
    }
    
}
