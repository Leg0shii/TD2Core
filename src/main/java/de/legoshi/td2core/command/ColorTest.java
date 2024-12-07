package de.legoshi.td2core.command;

import de.legoshi.td2core.player.tag.TagCreator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ColorTest implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) return false;
        Player player = (Player) commandSender;

        if (!player.isOp()) return false;
        List<Double> numberList = new ArrayList<>();
        double number = 10.1;
        numberList.add(0.0);
        numberList.add(0.1);
        numberList.add(5.1);

        for (;number < 91; number = number + 10.0) {
            numberList.add(number);
        }

        numberList.add(95.1);
        numberList.add(100.0);
        numberList.forEach(n -> Bukkit.broadcastMessage(TagCreator.getColoredNumber(n) + ChatColor.RESET + ChatColor.GRAY + " Leg0shi_: "));
        return false;
    }

}
