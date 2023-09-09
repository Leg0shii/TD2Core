package de.legoshi.td2core.command;

import de.legoshi.td2core.discord.VerifyManager;
import de.legoshi.td2core.util.Message;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

@RequiredArgsConstructor
public class VerifyCommand implements CommandExecutor {
    
    private final VerifyManager verifyManager;
    
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(Message.ERROR_NOT_A_PLAYER.getMessage());
            return true;
        }
        
        if (verifyManager.isVerified(((Player) commandSender).getPlayer())) {
            commandSender.sendMessage(Message.ERROR_ALREADY_VERIFIED.getSuccessMessage());
            return true;
        }
        
        Player player = (Player) commandSender;
        String randomID = UUID.randomUUID().toString().replace("-", "").substring(0, 6);
        String result = verifyManager.addVerify(player.getUniqueId().toString(), randomID);
        player.sendMessage(result);
        
        return false;
    }
    
}
