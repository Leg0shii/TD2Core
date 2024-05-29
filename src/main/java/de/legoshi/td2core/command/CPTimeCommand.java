package de.legoshi.td2core.command;

import de.legoshi.td2core.block.BlockManager;
import de.legoshi.td2core.util.Message;
import lombok.AllArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@AllArgsConstructor
public class CPTimeCommand implements CommandExecutor {

    private final BlockManager blockManager;

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!commandSender.hasPermission("td2core.edit_cp")) {
            commandSender.sendMessage(Message.ERROR_NO_PERMISSION.getWarningMessage());
            return false;
        }

        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(Message.ERROR_NOT_A_PLAYER.getWarningMessage());
            return false;
        }

        Player player = (Player) commandSender;
        if (!blockManager.hasTime(player)) {
            commandSender.sendMessage(Message.ERROR_NO_SPC.getWarningMessage());
            return false;
        }

        int time;
        try {
            if (strings.length != 1) {
                commandSender.sendMessage(Message.BLOCK_DATA_ERROR_SYNTAX.getWarningMessage());
                return false;
            }

            time = Integer.parseInt(strings[0]);
        } catch (NumberFormatException e) {
            commandSender.sendMessage(Message.BLOCK_DATA_ERROR_SYNTAX.getWarningMessage());
            return false;
        }

        blockManager.saveTimeTillNext(player, time);
        commandSender.sendMessage(Message.BLOCK_DATA_SUCCESS.getSuccessMessage());
        return true;
    }

}
