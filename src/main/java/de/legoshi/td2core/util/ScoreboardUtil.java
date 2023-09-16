package de.legoshi.td2core.util;

import de.legoshi.td2core.TD2Core;
import de.legoshi.td2core.player.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class ScoreboardUtil {
    
    public static void initializeScoreboard(Player player) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("td2core", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        
        scoreboard.registerNewTeam("noCollide").addEntry("noCollide_dummy");
        scoreboard.registerNewTeam("rank").addEntry(ChatColor.YELLOW + "" + ChatColor.WHITE);
        scoreboard.registerNewTeam("hour").addEntry(ChatColor.GRAY + "" + ChatColor.WHITE);
        scoreboard.registerNewTeam("jump").addEntry(ChatColor.GREEN + "" + ChatColor.WHITE);
        scoreboard.registerNewTeam("ip").addEntry(ChatColor.GOLD+ "" + ChatColor.WHITE);
        
        objective.setDisplayName("" + ChatColor.DARK_AQUA + ChatColor.BOLD + "TD2.mcpro.io");
        
        Team noCollide = scoreboard.getTeam("noCollide");
        noCollide.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
        noCollide.addEntry(player.getName());
        
        objective.getScore(ChatColor.YELLOW + " ").setScore(13);
        objective.getScore("" + ChatColor.GRAY + ChatColor.BOLD + "Completed").setScore(12);
        scoreboard.getTeam("rank").setPrefix("" + ChatColor.GRAY + "0.00 %");
        objective.getScore(ChatColor.YELLOW + "" + ChatColor.WHITE).setScore(11);
        objective.getScore(ChatColor.DARK_BLUE + " ").setScore(10);
        objective.getScore("" + ChatColor.GRAY + ChatColor.BOLD + "Playtime").setScore(9);
        scoreboard.getTeam("hour").setPrefix("" + ChatColor.GRAY + "-");
        objective.getScore(ChatColor.GRAY + "" + ChatColor.WHITE).setScore(8);
        objective.getScore(ChatColor.BLUE + " ").setScore(7);
        objective.getScore("" + ChatColor.GRAY + ChatColor.BOLD + "Jumps").setScore(6);
        scoreboard.getTeam("jump").setPrefix("" + ChatColor.GRAY + "-");
        objective.getScore(ChatColor.GREEN + "" + ChatColor.WHITE).setScore(5);
        
        player.setScoreboard(scoreboard);
    }
    
    public static void setSpawnScoreboardValue(Player player) {
        updatePercentage(player);
        updateJumpAndTime(player);
    }
    
    public static void updatePercentage(Player player) {
        Scoreboard scoreboard = player.getScoreboard();
        if (scoreboard == null) return;
        double percentage = TD2Core.getInstance().playerManager.get(player).getPercentage();
        scoreboard.getTeam("rank").setPrefix("" + ChatColor.WHITE + ((int) (percentage*100))/100.0 + " %");
    }
    
    public static void updateJumpAndTime(Player player) {
        Scoreboard scoreboard = player.getScoreboard();
        if (scoreboard == null) return;
        scoreboard.getTeam("hour").setPrefix("" + ChatColor.WHITE + player.getStatistic(Statistic.PLAY_ONE_TICK)/(20*60*60)+ " h");
        scoreboard.getTeam("jump").setPrefix("" + ChatColor.WHITE + player.getStatistic(Statistic.JUMP));
    }
    
}
