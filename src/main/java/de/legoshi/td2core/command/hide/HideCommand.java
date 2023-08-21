package de.legoshi.td2core.command.hide;

import de.legoshi.td2core.player.hide.HideManager;
import de.legoshi.td2core.util.Message;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class HideCommand implements CommandExecutor {
    
    private final HideManager hideManager;
    
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(Message.ERROR_NOT_A_PLAYER.getWarningMessage());
            return false;
        }
        
        if (strings.length != 1) {
            commandSender.sendMessage(Message.HELP_MESSAGE_11.getWarningMessage());
            return false;
        }
        String playerName = strings[0];
        Player player = (Player) commandSender;
        hideManager.hidePlayer(player, playerName);
        return false;
    }
    
}
