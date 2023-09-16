package de.legoshi.td2core.command;

import de.legoshi.td2core.TD2Core;
import de.legoshi.td2core.player.ParkourPlayer;
import de.legoshi.td2core.player.PlayerManager;
import de.legoshi.td2core.player.PlayerState;
import de.legoshi.td2core.util.Message;
import lombok.RequiredArgsConstructor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class StaffCommand implements CommandExecutor {
    
    private final PlayerManager playerManager;
    
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
        ParkourPlayer parkourPlayer = playerManager.get(player);
        if (parkourPlayer.getPlayerState() == PlayerState.STAFF) {
            if (parkourPlayer.getBukkitTask() != null) {
                parkourPlayer.getBukkitTask().cancel();
            }
            parkourPlayer.clearPotionEffects();
            parkourPlayer.setCurrentParkourMap(null);
    
            parkourPlayer.switchPlayerState(PlayerState.LOBBY);
            player.sendMessage(Message.STAFF_MODE_OFF.getInfoMessage());
            player.setGameMode(GameMode.ADVENTURE);
            player.teleport(TD2Core.getSpawn());
        } else {
            parkourPlayer.switchPlayerState(PlayerState.STAFF);
            player.setGameMode(GameMode.CREATIVE);
            player.sendMessage(Message.STAFF_MODE_ON.getInfoMessage());
        }
        
        return false;
    }
    
}
