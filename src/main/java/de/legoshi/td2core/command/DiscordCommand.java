package de.legoshi.td2core.command;

import de.legoshi.td2core.util.Message;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class DiscordCommand implements CommandExecutor {
    
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        commandSender.sendMessage(Message.DISCORD_MESSAGE.getInfoMessage());
        return false;
    }
    
}
