package de.legoshi.td2core.command;

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
public class PracticeCommand implements CommandExecutor {
    
    private final PlayerManager playerManager;
    
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(Message.ERROR_NOT_A_PLAYER.getMessage());
            return true;
        }
    
        Player player = (Player) commandSender;
        ParkourPlayer parkourPlayer = playerManager.get(player);
        if (parkourPlayer.isTutorial()) {
            player.sendMessage(Message.ERROR_IN_TUTORIAL.getWarningMessage());
            return true;
        }

        PlayerState state =  parkourPlayer.getPlayerState();
        if (state == PlayerState.LOBBY || state == PlayerState.STAFF || state == PlayerState.PLOT) {
            player.sendMessage(Message.ERROR_IN_LOBBY.getWarningMessage());
        } else {
            if (parkourPlayer.getPlayerState() == PlayerState.PRACTICE) {
                parkourPlayer.switchPlayerState(PlayerState.PARKOUR);
            } else {
                parkourPlayer.switchPlayerState(PlayerState.PRACTICE);
            }
        }
        
        return false;
    }
    
}
