package de.legoshi.td2core.command;

import de.legoshi.td2core.config.ConfigManager;
import de.legoshi.td2core.gui.GlobalLBGUI;
import de.legoshi.td2core.util.Message;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class LeaderboardCommand implements CommandExecutor {
    
    private final ConfigManager configManager;
    
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(Message.ERROR_NOT_A_PLAYER.getWarningMessage());
            return false;
        }
    
        Player player = (Player) commandSender;
        new GlobalLBGUI(configManager).openGui(player, null);
        
        return false;
    }
    
}
