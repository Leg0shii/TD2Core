package de.legoshi.td2core.command;

import de.legoshi.td2core.TD2Core;
import de.legoshi.td2core.player.ParkourPlayer;
import de.legoshi.td2core.player.PlayerManager;
import de.legoshi.td2core.player.PlayerState;
import de.legoshi.td2core.util.Message;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StaffCommand implements CommandExecutor {
    
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!commandSender.hasPermission("td2core.staff")) {
            commandSender.sendMessage(Message.ERROR_NO_PERMISSION.getWarningMessage());
            return false;
        }
        
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(Message.ERROR_NOT_A_PLAYER.getWarningMessage());
            return false;
        }
        
        Player player = (Player) commandSender;
        ParkourPlayer parkourPlayer = PlayerManager.get(player);
        if (parkourPlayer.getPlayerState() == PlayerState.STAFF_MODE) {
            if (parkourPlayer.getBukkitTask() != null) {
                parkourPlayer.getBukkitTask().cancel();
            }
            parkourPlayer.clearPotionEffects();
            parkourPlayer.setCurrentParkourMap(null);
    
            parkourPlayer.switchPlayerState(PlayerState.LOBBY);
            player.sendMessage(Message.STAFF_MODE_OFF.getInfoMessage());
            player.setGameMode(GameMode.ADVENTURE);
            player.teleport(TD2Core.getInstance().spawnLocation);
        } else {
            parkourPlayer.switchPlayerState(PlayerState.STAFF_MODE);
            player.setGameMode(GameMode.CREATIVE);
            player.sendMessage(Message.STAFF_MODE_ON.getInfoMessage());
        }
        
        return false;
    }
    
}