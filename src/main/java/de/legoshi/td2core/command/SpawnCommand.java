package de.legoshi.td2core.command;

import de.legoshi.td2core.TD2Core;
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
public class SpawnCommand implements CommandExecutor {
    
    private final PlayerManager playerManager;
    
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(Message.ERROR_NOT_A_PLAYER.getWarningMessage());
            return false;
        }
        
        Player player = (Player) commandSender;
        ParkourPlayer parkourPlayer = playerManager.get(player);
    
        if (parkourPlayer.isTutorial()) {
            player.teleport(TD2Core.getTutSpawn());
            return true;
        }

        PlayerState state =  parkourPlayer.getPlayerState();
        if (state == PlayerState.PARKOUR || state == PlayerState.PRACTICE) {
            parkourPlayer.mapLeave(false);
        } else {
            player.teleport(TD2Core.getSpawn());
        }
        
        if (parkourPlayer.getPlayerState() != PlayerState.STAFF) {
            player.setAllowFlight(false);
        }

        player.sendMessage(Message.SUCCESS_SPAWN.getSuccessMessage());
        return false;
    }
    
}
