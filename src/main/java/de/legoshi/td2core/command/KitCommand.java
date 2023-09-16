package de.legoshi.td2core.command;

import de.legoshi.td2core.kit.KitManager;
import de.legoshi.td2core.player.ParkourPlayer;
import de.legoshi.td2core.player.PlayerManager;
import de.legoshi.td2core.util.Message;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class KitCommand implements CommandExecutor {
    
    private final KitManager kitManager;
    private final PlayerManager playerManager;
    
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(Message.ERROR_NOT_A_PLAYER.getMessage());
            return true;
        }
        
        Player player = (Player) commandSender;
        ParkourPlayer parkourPlayer = playerManager.get(player);
        
        if (strings.length == 0) {
            parkourPlayer.setKit();
            player.sendMessage(Message.KIT_RESET_HELP.getSuccessMessage());
        } else if (strings.length == 1 && strings[0].equalsIgnoreCase("reset")) {
            kitManager.resetPlayerKits(parkourPlayer);
            parkourPlayer.setKit();
            player.sendMessage(Message.KIT_RESET.getSuccessMessage());
        } else {
            player.sendMessage(Message.HELP_MESSAGE_3.getMessage());
        }
        
        return true;
    }
    
}
