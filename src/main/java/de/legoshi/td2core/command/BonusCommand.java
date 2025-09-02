package de.legoshi.td2core.command;

import de.legoshi.td2core.config.ConfigManager;
import de.legoshi.td2core.gui.MapGUI;
import de.legoshi.td2core.map.MapManager;
import de.legoshi.td2core.map.session.SessionManager;
import de.legoshi.td2core.player.PlayerManager;
import de.legoshi.td2core.util.Message;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class BonusCommand implements CommandExecutor {

    private final MapManager mapManager;
    private final PlayerManager playerManager;
    private final SessionManager sessionManager;
    private final ConfigManager configManager;

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!commandSender.hasPermission("td2core.bonus")) {
            commandSender.sendMessage(Message.ERROR_NO_PERMISSION.getWarningMessage());
            return false;
        }

        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(Message.ERROR_NOT_A_PLAYER.getWarningMessage());
            return false;
        }

        Player player = (Player) commandSender;
        if (strings.length == 0) {
            new MapGUI(mapManager, playerManager, sessionManager, configManager).openGui(player, null, -1);
            return true;
        }

        return true;
    }

}
