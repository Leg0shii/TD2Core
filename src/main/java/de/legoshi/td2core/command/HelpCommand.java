package de.legoshi.td2core.command;

import de.legoshi.td2core.util.Message;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class HelpCommand implements CommandExecutor {
    
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        commandSender.sendMessage(Message.HELP_MESSAGE.getInfoMessage());
        commandSender.sendMessage(Message.HELP_MESSAGE_1.getMessage());
        commandSender.sendMessage(Message.HELP_MESSAGE_2.getMessage());
        commandSender.sendMessage(Message.HELP_MESSAGE_3.getMessage());
        commandSender.sendMessage(Message.HELP_MESSAGE_4.getMessage());
        commandSender.sendMessage(Message.HELP_MESSAGE_6.getMessage());
        commandSender.sendMessage(Message.HELP_MESSAGE_8.getMessage());
        commandSender.sendMessage(Message.HELP_MESSAGE_9.getMessage());
        commandSender.sendMessage(Message.HELP_MESSAGE_10.getMessage());
        commandSender.sendMessage(Message.HELP_MESSAGE_11.getMessage());
        commandSender.sendMessage(Message.HELP_MESSAGE_12.getMessage());
        commandSender.sendMessage(Message.HELP_MESSAGE_14.getMessage());
        commandSender.sendMessage(Message.HELP_MESSAGE_15.getMessage());
        
        if (commandSender.hasPermission("td2core.deleteplayer")) {
            commandSender.sendMessage(Message.HELP_MESSAGE_5.getMessage());
            commandSender.sendMessage(Message.HELP_MESSAGE_13.getMessage());
        }
        
        if (commandSender.isOp()) {
            commandSender.sendMessage(Message.HELP_MESSAGE_7.getMessage());
        }
    
        return true;
    }
    
}
