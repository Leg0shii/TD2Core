package de.legoshi.td2core.command;

import de.legoshi.td2core.block.BlockManager;
import de.legoshi.td2core.util.Message;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@RequiredArgsConstructor
public class CPEffectCommand implements CommandExecutor {

    private final BlockManager blockManager;

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!commandSender.hasPermission("td2core.edit_cp")) {
            commandSender.sendMessage(Message.ERROR_NO_PERMISSION.getWarningMessage());
            return false;
        }

        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(Message.ERROR_NOT_A_PLAYER.getWarningMessage());
            return false;
        }

        Player player = (Player) commandSender;
        if (!blockManager.hasPotionTemp(player)) {
            commandSender.sendMessage(Message.ERROR_NO_SPC.getWarningMessage());
            return false;
        }

        if (strings.length != 2) {
            commandSender.sendMessage(Message.POTION_USAGE.getWarningMessage());
            return false;
        }

        String potion = strings[0];
        int amplifier;
        try {
            amplifier = Integer.parseInt(strings[1]) - 1;
        } catch (NumberFormatException e) {
            commandSender.sendMessage(Message.NOT_A_NUMBER.getWarningMessage());
            return false;
        }

        PotionEffectType potionEffectType;
        switch (potion) {
            case "speed":
                potionEffectType = PotionEffectType.SPEED;
                break;
            case "slowness":
                potionEffectType = PotionEffectType.SLOW;
                break;
            case "jumpboost":
                potionEffectType = PotionEffectType.JUMP;
                break;
            default:
                return false;
        }

        if (amplifier < 0) {
            blockManager.removePotion(player, potionEffectType);
        } else {
            blockManager.savePotion(player, new PotionEffect(potionEffectType, Integer.MAX_VALUE, amplifier));
        }

        player.sendMessage(Message.SUCC_APPLIED_POTION.getSuccessMessage());
        return false;
    }

}
