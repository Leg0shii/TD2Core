package de.legoshi.td2core.command;

import de.legoshi.td2core.player.ParkourPlayer;
import de.legoshi.td2core.player.PlayerManager;
import de.legoshi.td2core.player.PlayerState;
import de.legoshi.td2core.util.Message;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class TPCommand implements CommandExecutor {

    private final PlayerManager playerManager;
    
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(Message.ERROR_NOT_A_PLAYER.getMessage());
            return true;
        }
        
        Player player = (Player) commandSender;
        ParkourPlayer parkourPlayer = playerManager.get(player);
        PlayerState state = parkourPlayer.getPlayerState();

        if (!commandSender.hasPermission("td2core.tp")
                && state != PlayerState.PLOT
                && state != PlayerState.PRACTICE) {
            commandSender.sendMessage(Message.ERROR_NO_PERMISSION.getWarningMessage());
            return true;
        }
        Location location = player.getLocation();
        
        if (strings.length == 0) {
            player.sendMessage(Message.TP_USAGE.getWarningMessage());
            return true;
        }
        
        try {
            if (strings.length == 1) {
                Player tpTo = Bukkit.getPlayer(strings[0]);
                if (tpTo != null) {
                    player.teleport(tpTo.getLocation());
                } else {
                    commandSender.sendMessage(Message.PLAYER_NOT_ONLINE.getWarningMessage());
                }
                return true;
            } else if (strings.length == 2) {
                Player tpPlayer = Bukkit.getPlayer(strings[0]);
                Player tpTo = Bukkit.getPlayer(strings[1]);
                if (tpTo != null && tpPlayer != null) {
                    tpPlayer.teleport(tpTo.getLocation());
                } else {
                    commandSender.sendMessage(Message.PLAYER_NOT_ONLINE.getWarningMessage());
                }
                return true;
            } else if (strings.length == 3) {
                double x = Double.parseDouble(strings[0]);
                double y = Double.parseDouble(strings[1]);
                double z = Double.parseDouble(strings[2]);
                location.setX(x);
                location.setY(y);
                location.setZ(z);
            } else if (strings.length == 4) {
                double x = Double.parseDouble(strings[0]);
                double y = Double.parseDouble(strings[1]);
                double z = Double.parseDouble(strings[2]);
                float yaw = Float.parseFloat(strings[3]);
                location.setX(x);
                location.setY(y);
                location.setZ(z);
                location.setYaw(yaw);
            } else if (strings.length == 5) {
                double x = Double.parseDouble(strings[0]);
                double y = Double.parseDouble(strings[1]);
                double z = Double.parseDouble(strings[2]);
                float yaw = Float.parseFloat(strings[3]);
                float pitch = Float.parseFloat(strings[4]);
                location.setX(x);
                location.setY(y);
                location.setZ(z);
                location.setYaw(yaw);
                location.setPitch(pitch);
            }
        } catch (NumberFormatException e) {
            commandSender.sendMessage(Message.NOT_A_NUMBER.getWarningMessage());
            return false;
        }
        
        player.teleport(location);
        player.sendMessage(Message.TELEPORT.getSuccessMessage());
        return false;
    }
    
}
