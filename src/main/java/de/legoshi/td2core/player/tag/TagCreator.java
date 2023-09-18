package de.legoshi.td2core.player.tag;

import de.legoshi.td2core.TD2Core;
import de.legoshi.td2core.player.ParkourPlayer;
import de.legoshi.td2core.player.PlayerManager;
import de.legoshi.td2core.player.PlayerState;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class TagCreator {
    
    public static void updateRank(Player playerToUpdate) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(TD2Core.getInstance(), () -> {
            Scoreboard scoreboard = playerToUpdate.getScoreboard(); // Get the scoreboard of the player to update
            
            for (Player all : Bukkit.getOnlinePlayers()) {
                ParkourPlayer parkourPlayer = TD2Core.getInstance().playerManager.get(all);
                PlayerTag playerTag = getPlayerTag((
                    (int) (parkourPlayer.getPercentage() * 10)) / 10.0,
                    parkourPlayer.getRank(),
                    parkourPlayer.getPlayerState().equals(PlayerState.STAFF)
                );
                String team = playerTag.getTeam();
                
                if (scoreboard.getTeam(team) == null) scoreboard.registerNewTeam(team);
                
                scoreboard.getTeam(team).setPrefix(playerTag.getPrefix());
                scoreboard.getTeam(team).setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
                scoreboard.getTeam(team).addEntry(all.getName());
                
                parkourPlayer.setPlayerTag(playerTag);
            }
        }, 20L);
    }
    
    public static String getPrefix(double percentage) {
        String prefix = "" + ChatColor.GRAY + ChatColor.BOLD;
        if (percentage > 0 && percentage <= 10) prefix = "" + ChatColor.AQUA + ChatColor.BOLD;            // Aqua for 0-10%
        if (percentage > 10 && percentage <= 20) prefix = "" + ChatColor.BLUE + ChatColor.BOLD;        // Blue for 11-20%
        if (percentage > 20 && percentage <= 30) prefix = "" + ChatColor.GREEN + ChatColor.BOLD;      // Green for 21-30%
        if (percentage > 30 && percentage <= 40) prefix = "" + ChatColor.YELLOW + ChatColor.BOLD;     // Yellow for 31-40%
        if (percentage > 40 && percentage <= 50) prefix = "" + ChatColor.GOLD + ChatColor.BOLD;       // Gold for 41-50%
        if (percentage > 50 && percentage <= 60) prefix = "" + ChatColor.RED + ChatColor.BOLD;        // Red for 51-60%
        if (percentage > 60 && percentage <= 70) prefix = "" + ChatColor.DARK_PURPLE + ChatColor.BOLD; // Dark Purple for 61-70%
        if (percentage > 70 && percentage <= 80) prefix = "" + ChatColor.DARK_GREEN + ChatColor.BOLD;  // Dark Green for 71-80%
        if (percentage > 80 && percentage <= 90) prefix = "" + ChatColor.DARK_BLUE + ChatColor.BOLD;  // Dark Blue for 81-90%
        if (percentage > 90) prefix = "" + ChatColor.DARK_GRAY + ChatColor.BOLD;                      // Dark Gray for 91-100%
        return (prefix);
    }
    
    private static PlayerTag getPlayerTag(double percentage, int rank, boolean isStaff) {
        String color = getPrefix(percentage);
        String prefix = color + percentage;
    
        if (isStaff) {
            prefix = "" + ChatColor.RED + ChatColor.BOLD + "Staff";
            rank = 0;
        }
        
        return new PlayerTag(
            prefix + ChatColor.GRAY + " ",
            Double.toString(1000000 + rank)
        );
    }
}
