package de.legoshi.td2core.command;

import de.legoshi.td2core.block.BlockManager;
import de.legoshi.td2core.player.ParkourPlayer;
import de.legoshi.td2core.player.PlayerManager;
import de.legoshi.td2core.player.PlayerState;
import de.legoshi.td2core.util.Message;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class SPCCommand implements CommandExecutor {
    
    private final BlockManager blockManager;
    private final PlayerManager playerManager;
    
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(Message.ERROR_NOT_A_PLAYER.getWarningMessage());
            return false;
        }
        
        Player player = (Player) commandSender;
        ParkourPlayer parkourPlayer = playerManager.get(player);
        if (!(parkourPlayer.getPlayerState() == PlayerState.STAFF || parkourPlayer.getPlayerState() == PlayerState.PLOT)) {
            commandSender.sendMessage(Message.ERROR_NO_PERMISSION.getWarningMessage());
            return false;
        }

        if (!blockManager.hasSPCTemp(player)) {
            commandSender.sendMessage(Message.ERROR_NO_SPC.getWarningMessage());
            return false;
        }
        
        Location location;
        if (strings.length == 0) {
            location = player.getLocation();
        } else if (strings.length == 3) {
            double x = Double.parseDouble(strings[0]);
            double y = Double.parseDouble(strings[1]);
            double z = Double.parseDouble(strings[2]);
            location = new Location(player.getWorld(), x, y, z);
        } else if (strings.length == 4) {
            double x = Double.parseDouble(strings[0]);
            double y = Double.parseDouble(strings[1]);
            double z = Double.parseDouble(strings[2]);
            float yaw = Float.parseFloat(strings[3]);
            float pitch = player.getLocation().getPitch();
            location = new Location(player.getWorld(), x, y, z, yaw, pitch);
        } else if (strings.length == 5) {
            double x = Double.parseDouble(strings[0]);
            double y = Double.parseDouble(strings[1]);
            double z = Double.parseDouble(strings[2]);
            float yaw = Float.parseFloat(strings[3]);
            float pitch = Float.parseFloat(strings[4]);
            location = new Location(player.getWorld(), x, y, z, yaw, pitch);
        } else {
            commandSender.sendMessage(Message.BLOCK_DATA_ERROR_SYNTAX.getWarningMessage());
            return false;
        }
        
        blockManager.saveSPC(player, location);
        commandSender.sendMessage(Message.BLOCK_DATA_SUCCESS.getSuccessMessage());
        return false;
    }
}
