package de.legoshi.td2core.kit;

import de.legoshi.td2core.config.ConfigAccessor;
import de.legoshi.td2core.config.ConfigManager;
import de.legoshi.td2core.config.PlayerInventoryConfig;
import de.legoshi.td2core.player.ParkourPlayer;
import de.legoshi.td2core.util.Utils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.*;

public class KitManager {

    private final ConfigAccessor configAccessor;
    private final FileConfiguration configuration;
    private final HashMap<Player, List<Kit>> playerListHashMap;
    private final Set<Class<? extends Kit>> kitTypes;

    public KitManager(ConfigManager configManager) {
        this.configAccessor = configManager.getConfigAccessor(PlayerInventoryConfig.class);
        this.configuration = configAccessor.getConfig();
        this.playerListHashMap = new HashMap<>();
        this.kitTypes = new HashSet<>();
        loadTypes();
    }

    private void loadTypes() {
        kitTypes.add(LobbyKit.class);
        kitTypes.add(ParkourKit.class);
        kitTypes.add(PlotKit.class);
        kitTypes.add(PracticeKit.class);
        kitTypes.add(StaffKit.class);
    }

    public Kit getPlayerKit(ParkourPlayer parkourPlayer, Class<? extends Kit> clazz) {
        Player player = parkourPlayer.getPlayer();
        if (playerListHashMap.containsKey(player)) {
            List<Kit> kits = playerListHashMap.get(player);
            for (Kit kit : kits) {
                if (clazz == kit.getClass()) {
                    return kit;
                }
            }
        }
        return loadPlayerKit(parkourPlayer, clazz);
    }
    
    public void updatePlayerKitOrder(ParkourPlayer parkourPlayer) {
        Kit loadedKit = parkourPlayer.getPlayerKit();
        Player player = parkourPlayer.getPlayer();
        PlayerInventory inventory = player.getInventory();
        ItemStack[] contents = inventory.getContents();

        ItemInventoryMap inventoryMap = new ItemInventoryMap();
        for (int slot = 0; slot < contents.length; slot++) {
            ItemStack item = contents[slot];
            if (item != null && !item.getType().equals(Material.COMPASS)) {
                inventoryMap.setItem(slot, item);
            }
        }
        loadedKit.setInventoryMap(inventoryMap);
    }

    public void savePlayerKit(ParkourPlayer parkourPlayer) {
        String playerUUID = parkourPlayer.getPlayer().getUniqueId().toString();
        configuration.set(playerUUID, null);

        for (Kit kit : playerListHashMap.get(parkourPlayer.getPlayer())) {
            updatePlayerKitOrder(parkourPlayer);
            for (int slot : kit.getSlots()) {
                ItemStack item = kit.getItem(slot);
                if (item == null || item.getType().equals(Material.COMPASS)) {
                    continue;
                }
                configuration.set(
                        playerUUID + "." + kit.getClass().getSimpleName() + "." + slot,
                        Utils.getStringFromItemStack(item)
                );
            }
        }
        configAccessor.saveConfig();
    }
    
    private Kit loadPlayerKit(ParkourPlayer parkourPlayer, Class<? extends Kit> clazz) {
        Kit loadedKit;
        ItemInventoryMap inventoryMap = null;
        String playerUUID = parkourPlayer.getPlayer().getUniqueId().toString();
        
        ConfigurationSection itemConfigSection = configuration.getConfigurationSection(playerUUID + "." + clazz.getSimpleName());
        if (itemConfigSection == null) {
            System.err.println("Item Config Null. Returning Standard Kit Config");
        } else {
            inventoryMap = new ItemInventoryMap();
            for (String itemConfig : itemConfigSection.getKeys(false)) {
                String itemConfigValue = itemConfigSection.getString(itemConfig);
                ItemStack item = Utils.getItemStackFromString(itemConfigValue);
                inventoryMap.setItem(Integer.parseInt(itemConfig), item);
            }
        }
    
        loadedKit = getKitInstance(clazz, inventoryMap, parkourPlayer.getVersion());
        addKitToList(parkourPlayer.getPlayer(), loadedKit);
        return loadedKit;
    }
    
    public void resetPlayerKits(ParkourPlayer parkourPlayer) {
        List<Kit> playerKits = new ArrayList<>();
        configuration.set(parkourPlayer.getPlayer().getUniqueId().toString(), null);
        
        for (Class<? extends Kit> clazz : kitTypes) {
            Kit newKit = getKitInstance(clazz, null, parkourPlayer.getVersion());
            playerKits.add(newKit);
            if (parkourPlayer.getPlayerKit().getClass().getSimpleName().equals(clazz.getSimpleName())) {
                parkourPlayer.setPlayerKit(newKit);
            }
        }
        playerListHashMap.put(parkourPlayer.getPlayer(), playerKits);
    }
    
    private void addKitToList(Player player, Kit kit) {
        if (!playerListHashMap.containsKey(player)) {
            playerListHashMap.put(player, new ArrayList<>());
        }
        playerListHashMap.get(player).add(kit);
    }
    
    private Kit getKitInstance(Class<? extends Kit> clazz, ItemInventoryMap map, int version) {
        Kit loadedKit;
        try {
            if (map == null) {
                loadedKit = clazz.getDeclaredConstructor(Integer.class).newInstance(version);
            } else {
                loadedKit = clazz.getDeclaredConstructor(ItemInventoryMap.class, Integer.class).newInstance(map, version);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return loadedKit;
    }
    
}
