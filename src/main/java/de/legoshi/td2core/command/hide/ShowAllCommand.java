package de.legoshi.td2core.command.hide;

import de.legoshi.td2core.player.invis.InvisManager;
import de.legoshi.td2core.util.Message;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class ShowAllCommand implements CommandExecutor {
    
    private final InvisManager invisManager;
    
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(Message.ERROR_NOT_A_PLAYER.getWarningMessage());
            return false;
        }
        
        invisManager.unHideAllPlayers(((Player) commandSender).getPlayer());
        return false;
    }
    
}
