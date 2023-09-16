package de.legoshi.td2core.kit;

import de.legoshi.td2core.config.ConfigManager;
import de.legoshi.td2core.config.PlayerInventoryConfig;
import de.legoshi.td2core.player.ParkourPlayer;
import de.legoshi.td2core.player.PlayerState;
import de.legoshi.td2core.util.Utils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.swing.*;
import java.util.*;

public class KitManager {

    private final FileConfiguration configuration;
    private final HashMap<Player, List<Kit>> playerListHashMap;
    private final Set<Class<? extends Kit>> kitTypes;

    public KitManager(ConfigManager configManager) {
        this.configuration = configManager.getConfig(PlayerInventoryConfig.class);
        this.playerListHashMap = new HashMap<>();
        this.kitTypes = new HashSet<>();
        loadTypes();
    }

    private void loadTypes() {
        kitTypes.add(LobbyKit.class);
        kitTypes.add(ParkourKit.class);
        kitTypes.add(PracticeKit.class);
        kitTypes.add(StaffKit.class);
    }

    public Kit getPlayerKit(ParkourPlayer parkourPlayer) {
        Player player = parkourPlayer.getPlayer();
        PlayerState state = parkourPlayer.getPlayerState();

        if (playerListHashMap.containsKey(player)) {
            List<Kit> kits = playerListHashMap.get(player);
            return kits.stream()
                    .filter(k -> state.toString().toLowerCase().contains(k.getClass().getSimpleName().toLowerCase()))
                    .findFirst()
                    .orElse(null);
        }
        return loadKitFromConfig(parkourPlayer);
    }

    public void savePlayerKit(ParkourPlayer parkourPlayer) {
        String playerUUID = parkourPlayer.getPlayer().getUniqueId().toString();
        for (Kit kit : playerListHashMap.get(parkourPlayer.getPlayer())) {
            for (ItemStack item : kit.getInventory()) {
                configuration.set(
                        playerUUID + "." + kit.getClass().getSimpleName(),
                        Utils.getStringFromItemStack(item)
                );
            }
        }
    }

    private Kit loadKitFromConfig(ParkourPlayer parkourPlayer) {
        for (Class<? extends Kit> clazz : kitTypes) {
            for (String key : configuration.getKeys(false)) {
                String itemConfig = configuration.getString(key);

            }
             try {
                 clazz.getConstructor().newInstance(parkourPlayer.getVersion());
             } catch (Exception e) {
                 e.printStackTrace();
             }
        }

    }

}
