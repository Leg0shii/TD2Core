package de.legoshi.td2core.command;

import de.legoshi.td2core.map.MapManager;
import de.legoshi.td2core.map.ParkourMap;
import de.legoshi.td2core.map.session.ParkourSession;
import de.legoshi.td2core.map.session.SessionManager;
import de.legoshi.td2core.player.ParkourPlayer;
import de.legoshi.td2core.player.PlayerManager;
import de.legoshi.td2core.player.PlayerState;
import de.legoshi.td2core.util.Message;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class ResetCommand implements CommandExecutor {
    
    private final MapManager mapManager;
    
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(Message.ERROR_NOT_A_PLAYER.getWarningMessage());
            return false;
        }
        
        Player player = (Player) commandSender;
        ParkourPlayer parkourPlayer = PlayerManager.get(player);
        ParkourMap parkourMap = parkourPlayer.getCurrentParkourMap();
        
        if (parkourPlayer.getPlayerState() != PlayerState.PARKOUR) {
            player.sendMessage(Message.PLAYER_NOT_IN_MAP.getWarningMessage());
            return false;
        }
    
        ParkourSession parkourSession = SessionManager.get(player, parkourMap);
        parkourSession.reset();
        player.teleport(parkourMap.getStartLocation());
    
        mapManager.deletePlay(player.getUniqueId().toString(), parkourMap.mapName);
        player.sendMessage(Message.SUCCESS_RESET.getSuccessMessage());
        return false;
    }
    
}
