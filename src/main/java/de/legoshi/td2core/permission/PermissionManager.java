package de.legoshi.td2core.permission;

import de.legoshi.td2core.TD2Core;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import java.util.HashMap;
import java.util.UUID;

public class PermissionManager {

    private final HashMap<UUID, PermissionAttachment> permissions = new HashMap<>();

    public void addPlayer(Player player) {
        permissions.put(player.getUniqueId(), player.addAttachment(TD2Core.getInstance()));
    }

    public void removePlayer(Player player) {
        player.removeAttachment(permissions.get(player.getUniqueId()));
        permissions.remove(player.getUniqueId());
    }

    public void allowFly(Player player) {
        permissions.get(player.getUniqueId()).setPermission("replay.ignore", true);
    }

    public void disallowFly(Player player) {
        permissions.get(player.getUniqueId()).unsetPermission("replay.ignore");
    }

    public void allowPlotCommands(Player player) {
        permissions.get(player.getUniqueId()).setPermission("worldguard.build.*", true);
        permissions.get(player.getUniqueId()).setPermission("plots.permpack.basic", true);
        permissions.get(player.getUniqueId()).setPermission("plots.permpack.basicflags", true);
        permissions.get(player.getUniqueId()).setPermission("plots.permpack.basicinbox", true);

        allowFly(player);
    }

    public void disallowPlotCommands(Player player) {
        permissions.get(player.getUniqueId()).unsetPermission("worldguard.build.*");
        permissions.get(player.getUniqueId()).unsetPermission("plots.permpack.basic");
        permissions.get(player.getUniqueId()).unsetPermission("plots.permpack.basicflags");
        permissions.get(player.getUniqueId()).unsetPermission("plots.permpack.basicinbox");

        disallowFly(player);
    }

}
