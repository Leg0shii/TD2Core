package de.legoshi.td2core.permission;

import de.legoshi.td2core.TD2Core;
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
        permissions.get(player.getUniqueId()).setPermission("replay.ignore", false);
    }

}
