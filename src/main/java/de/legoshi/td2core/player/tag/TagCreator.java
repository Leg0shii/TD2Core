package de.legoshi.td2core.player.tag;

import de.legoshi.td2core.TD2Core;
import de.legoshi.td2core.player.ParkourPlayer;
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

    public static String getColoredNumber(double percentage) {
        String prefix = "" + ChatColor.GRAY + ChatColor.BOLD; // Default for 0%

        if (percentage > 0 && percentage <= 5) prefix = "" + ChatColor.WHITE + ChatColor.BOLD;          // White for 0-5%
        if (percentage > 5 && percentage <= 10) prefix = "" + ChatColor.AQUA + ChatColor.BOLD;          // Aqua for 6-10%
        if (percentage > 10 && percentage <= 20) prefix = "" + ChatColor.BLUE + ChatColor.BOLD;         // Blue for 11-20%
        if (percentage > 20 && percentage <= 30) prefix = "" + ChatColor.GREEN + ChatColor.BOLD;        // Green for 21-30%
        if (percentage > 30 && percentage <= 40) prefix = "" + ChatColor.YELLOW + ChatColor.BOLD;       // Yellow for 31-40%
        if (percentage > 40 && percentage <= 50) prefix = "" + ChatColor.GOLD + ChatColor.BOLD;         // Gold for 41-50%
        if (percentage > 50 && percentage <= 60) prefix = "" + ChatColor.RED + ChatColor.BOLD;          // Red for 51-60%
        if (percentage > 60 && percentage <= 70) prefix = "" + ChatColor.LIGHT_PURPLE + ChatColor.BOLD;  // Dark Purple for 61-70%

        String coloredNumber = prefix + percentage;

        // Multicolor for 70.0 - 99.9%
        if (percentage > 70 && percentage < 100) {
            int wholePart = (int) percentage; // Whole number part (e.g., 71)
            String fractionPartString = (percentage + "").split("\\.")[1];

            String darkColor = getUniqueDarkColor(percentage);       // Get dark color for the whole part
            String lightColor = getUniqueLightColor(percentage); // Get light color for the fractional part

            prefix = "" + lightColor + ChatColor.BOLD + wholePart +
                    "." + darkColor + ChatColor.BOLD + fractionPartString;
            coloredNumber = prefix;
        }

        // Gradient for 100.0
        if (percentage == 100) {
            prefix = "" + ChatColor.DARK_RED + ChatColor.BOLD + "1" + ChatColor.RESET +  // Dark Red for '1'
                    ChatColor.RED + ChatColor.BOLD + "0" + ChatColor.RESET +             // Red for first '0'
                    ChatColor.GOLD + ChatColor.BOLD + "0" + ChatColor.RESET +           // Gold for second '0'
                    ChatColor.YELLOW + ChatColor.BOLD + "." + ChatColor.RESET +         // Yellow for '.'
                    ChatColor.YELLOW + ChatColor.BOLD + "0";                             // Green for last '0'
            coloredNumber = prefix;
        }

        return coloredNumber;
    }

    // Unique dark colors for 70.0 - 99.9%
    private static String getUniqueDarkColor(double percentage) {
        if (percentage > 70 && percentage <= 80) return "" + ChatColor.DARK_GREEN;   // Unique dark green
        if (percentage > 80 && percentage <= 90) return "" + ChatColor.DARK_AQUA;    // Unique dark aqua
        if (percentage > 90 && percentage <= 95) return "" + ChatColor.DARK_PURPLE;  // Unique dark purple
        if (percentage > 95 && percentage <= 99.9) return "" + ChatColor.DARK_RED;   // Unique dark red
        return "" + ChatColor.GRAY; // Default fallback
    }

    // Unique light colors for the fractional part
    private static String getUniqueLightColor(double percentage) {
        if (percentage > 70 && percentage <= 80) return "" + ChatColor.GREEN;        // Lighter green
        if (percentage > 80 && percentage <= 90) return "" + ChatColor.AQUA;         // Lighter aqua
        if (percentage > 90 && percentage <= 95) return "" + ChatColor.LIGHT_PURPLE; // Lighter purple
        if (percentage > 95 && percentage <= 99.9) return "" + ChatColor.RED;        // Lighter red
        return "" + ChatColor.GRAY; // Default fallback
    }


    private static PlayerTag getPlayerTag(double percentage, int rank, boolean isStaff) {
        String coloredNumber = getColoredNumber(percentage);
    
        if (isStaff) {
            coloredNumber = "" + ChatColor.RED + ChatColor.BOLD + "Staff";
            rank = 0;
        }
        
        return new PlayerTag(
                coloredNumber + ChatColor.GRAY + " ",
            Double.toString(1000000 + rank)
        );
    }
}
