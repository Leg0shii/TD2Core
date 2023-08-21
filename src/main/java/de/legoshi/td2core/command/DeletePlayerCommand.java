package de.legoshi.td2core.command;

import de.legoshi.td2core.TD2Core;
import de.legoshi.td2core.map.MapManager;
import de.legoshi.td2core.util.Message;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

public class DeletePlayerCommand implements CommandExecutor {
    
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!commandSender.hasPermission("td2core.deleteplayer")) {
            commandSender.sendMessage(Message.ERROR_NO_PERMISSION.getWarningMessage());
            return false;
        }
        
        String deleteName = args[0];
        String deleteUUID;
        Player player = Bukkit.getPlayer(deleteName);
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(deleteName);
        
        if (player != null) {
            commandSender.sendMessage(Message.ERROR_PLAYER_ONLINE.getWarningMessage(deleteName));
            return false;
        } else if (offlinePlayer != null) {
            deleteUUID = offlinePlayer.getUniqueId().toString();
        } else {
            commandSender.sendMessage(Message.ERROR_PLAYER_DELETE.getWarningMessage(deleteName));
            return false;
        }
    
        String finalDeleteUUID = deleteUUID;
        Bukkit.getScheduler().runTaskAsynchronously(TD2Core.getInstance(), () -> {
            try {
                String sql = "DELETE FROM player_log WHERE userid = ?";
                PreparedStatement preparedStatement = TD2Core.sql().prepare(sql);
                preparedStatement.setString(1, finalDeleteUUID);
                preparedStatement.execute();
    
                sql = "DELETE FROM collected_cp WHERE userid = ?";
                PreparedStatement preparedStatement2 = TD2Core.sql().prepare(sql);
                preparedStatement2.setString(1, finalDeleteUUID);
                preparedStatement2.execute();
                
                commandSender.sendMessage(Message.SUCCESS_PLAYER_DELETE.getSuccessMessage(deleteName));
            } catch (SQLException e) {
                e.printStackTrace();
                commandSender.sendMessage(Message.ERROR_PLAYER_DELETE.getWarningMessage(deleteName));
            }
        });
        
        MapManager.getPkMapHashMap().keySet().forEach(MapManager::loadMapStats);
        return false;
    }
    
}
