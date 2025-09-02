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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public class ResetCommand implements CommandExecutor {
    
    private final MapManager mapManager;
    private final PlayerManager playerManager;
    private final SessionManager sessionManager;

    private final Map<UUID, Long> pendingResets = new HashMap<>();
    private static final long CONFIRMATION_TIMEOUT = 10000;
    
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        cleanupExpiredConfirmations();

        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(Message.ERROR_NOT_A_PLAYER.getWarningMessage());
            return false;
        }
        
        Player player = (Player) commandSender;
        UUID playerUUID = player.getUniqueId();
        ParkourPlayer parkourPlayer = playerManager.get(player);
        
        if (parkourPlayer.getPlayerState() != PlayerState.PARKOUR) {
            player.sendMessage(Message.PLAYER_NOT_IN_MAP.getWarningMessage());
            return false;
        }
        ParkourMap parkourMap = parkourPlayer.getCurrentParkourMap();

        Long confirmationTime = pendingResets.get(playerUUID);
        if (confirmationTime == null) {
            pendingResets.put(playerUUID, System.currentTimeMillis());
            player.sendMessage(Message.INFO_RESET_MESSAGE.getInfoMessage());
            return true;
        }

        if (System.currentTimeMillis() - confirmationTime > CONFIRMATION_TIMEOUT) {
            pendingResets.put(playerUUID, System.currentTimeMillis());
            player.sendMessage(Message.WARNING_RESET_MESSAGE.getWarningMessage());
            return true;
        }
    
        ParkourSession parkourSession = sessionManager.get(player, parkourMap);
        parkourSession.reset();
        player.teleport(parkourMap.getStartLocation());
    
        mapManager.resetPlay(player.getUniqueId().toString(), parkourMap);
        player.sendMessage(Message.SUCCESS_RESET.getSuccessMessage());

        pendingResets.remove(playerUUID);
        return false;
    }

    public void cleanupExpiredConfirmations() {
        long currentTime = System.currentTimeMillis();
        pendingResets.entrySet().removeIf(entry -> currentTime - entry.getValue() > CONFIRMATION_TIMEOUT);
    }

}
