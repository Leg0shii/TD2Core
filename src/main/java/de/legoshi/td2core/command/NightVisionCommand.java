package de.legoshi.td2core.command;

import de.legoshi.td2core.util.Message;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Collections;

public class NightVisionCommand implements CommandExecutor {
    
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(Message.ERROR_NOT_A_PLAYER.getWarningMessage());
            return false;
        }
        
        Player player = (Player) commandSender;
        
        if (player.hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
            player.removePotionEffect(PotionEffectType.NIGHT_VISION);
            player.sendMessage(Message.NIGHT_VISION_OFF.getInfoMessage());
        } else {
            PotionEffect potionEffect = new PotionEffect(PotionEffectType.NIGHT_VISION, 10000000, 1);
            player.addPotionEffects(Collections.singleton(potionEffect));
            player.sendMessage(Message.NIGHT_VISION_ON.getInfoMessage());
        }
        return false;
    }
    
}
