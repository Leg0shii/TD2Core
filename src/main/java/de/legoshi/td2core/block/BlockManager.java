package de.legoshi.td2core.block;

import de.legoshi.td2core.TD2Core;
import de.legoshi.td2core.gui.CheckPointGUI;
import de.legoshi.td2core.gui.PlotCheckPointGUI;
import de.legoshi.td2core.player.ParkourPlayer;
import de.legoshi.td2core.player.PlayerManager;
import de.legoshi.td2core.player.PlayerState;
import de.legoshi.td2core.util.Message;
import de.legoshi.td2core.util.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class BlockManager implements Listener {
    
    private final PlayerManager playerManager;
    private final HashMap<Location, BlockData> blockInformation;
    
    private final HashMap<Player, Location> playerPreciseData;
    private final HashMap<Player, Location> playerNextData;
    private final HashMap<Player, Location> playerTimeTillNextData;
    private final HashMap<Player, Location> playerCPData;
    private final HashMap<Player, Location> playerPotionData;

    public BlockManager(PlayerManager playerManager) {
        this.playerManager = playerManager;
        this.blockInformation = new HashMap<>();
        this.playerPreciseData = new HashMap<>();
        this.playerNextData = new HashMap<>();
        this.playerCPData = new HashMap<>();
        this.playerTimeTillNextData = new HashMap<>();
        this.playerPotionData = new HashMap<>();
    }
    
    @EventHandler
    public void blockInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        
        Material blockType = event.getClickedBlock().getType();
        if (blockType != Material.IRON_PLATE & blockType != Material.WOOD_PLATE && blockType != Material.STONE_PLATE) return;

        Player player = event.getPlayer();
        ParkourPlayer parkourPlayer = playerManager.get(player);
        Location clickedBlock = event.getClickedBlock().getLocation();
        if (parkourPlayer.getPlayerState() == PlayerState.STAFF) {
            new CheckPointGUI().openGui(player, this, clickedBlock);
        } else if (parkourPlayer.getPlayerState() == PlayerState.PLOT) {
            new PlotCheckPointGUI().openGui(player, this, clickedBlock);
        }
    }
    
    @EventHandler
    public void blockInteract(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Location clickedLoc = event.getBlock().getLocation();
        ParkourPlayer parkourPlayer = playerManager.get(player);
        
        if (!(parkourPlayer.getPlayerState() == PlayerState.STAFF || parkourPlayer.getPlayerState() == PlayerState.PLOT)) {
            player.sendMessage(Message.CANT_DESTROY_BLOCK_DATA.getWarningMessage());
            event.setCancelled(true);
            return;
        }
        
        if (playerNextData.containsKey(player)) {
            Location startingLoc = playerNextData.get(player);
            
            BlockData blockData = blockInformation.getOrDefault(startingLoc, new BlockData());
            blockData.setNextCheckpoint(clickedLoc);
            
            saveBlockInformation(startingLoc, blockData);
            playerNextData.remove(player);
            
            player.sendMessage(Message.BLOCK_DATA_SUCCESS.getSuccessMessage());
            event.setCancelled(true);
            return;
        }
        
        // player destroys a pressure plate: delete it
        Material type = event.getBlock().getType();
        if (!blockInformation.containsKey(clickedLoc)) return;
        if (type.equals(Material.IRON_PLATE) || type.equals(Material.WOOD_PLATE) || type.equals(Material.STONE_PLATE)) {
            deleteBlockData(clickedLoc, player);
        }
    }
    
    public Location getTeleportPos(Location location) {
        if (hasEntry(location)) {
            return blockInformation.get(location).getTeleportLoc();
        }
        return null;
    }

    public Collection<PotionEffect> getPotionEffects(Location location) {
        if (hasEntry(location)) {
            return blockInformation.get(location).getPotionEffects();
        }
        return new ArrayList<>();
    }
    
    public void saveSPC(Player player, Location teleportLocation) {
        Location checkpoint = playerPreciseData.get(player);
        BlockData blockData = blockInformation.getOrDefault(checkpoint, new BlockData());
        blockData.setTeleportLoc(teleportLocation);
        
        playerPreciseData.remove(player);
        saveBlockInformation(checkpoint, blockData);
    }
    
    public void saveCPIndex(Player player, int cpIndex) {
        Location checkpoint = playerCPData.get(player);
        BlockData blockData = blockInformation.getOrDefault(checkpoint, new BlockData());
        blockData.setCpIndex(cpIndex);
        
        playerCPData.remove(player);
        saveBlockInformation(checkpoint, blockData);
    }

    public void savePotion(Player player, PotionEffect potionEffect) {
        Location checkpoint = playerPotionData.get(player);
        BlockData blockData = blockInformation.getOrDefault(checkpoint, new BlockData());
        blockData.getPotionEffects().add(potionEffect);

        playerPotionData.remove(player);
        saveBlockInformation(checkpoint, blockData);
    }

    public void removePotion(Player player, PotionEffectType potionEffectType) {
        Location checkpoint = playerPotionData.get(player);
        BlockData blockData = blockInformation.getOrDefault(checkpoint, new BlockData());
        blockData.getPotionEffects().removeIf(p -> p.getType().equals(potionEffectType));

        playerPotionData.remove(player);
        saveBlockInformation(checkpoint, blockData);
    }

    public void saveTimeTillNext(Player player, int time) {
        Location checkpoint = playerTimeTillNextData.get(player);
        BlockData blockData = blockInformation.getOrDefault(checkpoint, new BlockData());
        blockData.setTimeTillNextCheckpoint(time);

        playerTimeTillNextData.remove(player);
        saveBlockInformation(checkpoint, blockData);
    }
    
    public Location getNextCPLocation(Location location) {
        if (hasEntry(location)) {
            return blockInformation.get(location).getNextCheckpoint();
        }
        return null;
    }
    
    public int getClickedCPIndex(Location location) {
        if (hasEntry(location)) {
            return blockInformation.get(location).getCpIndex();
        }
        return -1;
    }

    public int getTimeTillNext(Location location) {
        if (hasEntry(location)) {
            return blockInformation.get(location).getTimeTillNextCheckpoint();
        }
        return -1;
    }
    
    public boolean hasTP(Location location) {
        return getTeleportPos(location) != null;
    }
    
    public boolean hasNext(Location location) {
        return getNextCPLocation(location) != null;
    }
    
    public boolean hasEntry(Location location) {
        return blockInformation.containsKey(location);
    }
    
    public boolean isCheckpoint(Location location) {
        return blockInformation.getOrDefault(location, new BlockData()).isCheckpoint();
    }

    public boolean isNoSprint(Location location) {
        return blockInformation.getOrDefault(location, new BlockData()).isNoSprint();
    }

    public void addPotionData(Player player, Location location) {
        playerPotionData.put(player, location);
    }
    
    public void addTempPreciseData(Player player, Location location) {
        playerPreciseData.put(player, location);
    }
    
    public void addTempNextData(Player player, Location location) {
        playerNextData.put(player, location);
    }
    
    public void addIsCheckpoint(Location location, boolean value) {
        BlockData blockData = blockInformation.getOrDefault(location, new BlockData());
        blockData.setCheckpoint(value);
        saveBlockInformation(location, blockData);
    }

    public void addIsNoSprint(Location location, boolean value) {
        BlockData blockData = blockInformation.getOrDefault(location, new BlockData());
        blockData.setNoSprint(value);
        saveBlockInformation(location, blockData);
    }
    
    public void addIsCPIndex(Player player, Location location) {
        playerCPData.put(player, location);
    }

    public void addTimeTillNext(Player player, Location location) {
        playerTimeTillNextData.put(player, location);
    }

    public boolean hasTime(Player player) {
        return playerTimeTillNextData.containsKey(player);
    }

    public boolean hasSPCTemp(Player player) {
        return playerPreciseData.containsKey(player);
    }
    
    public boolean hasCPTemp(Player player) {
        return playerCPData.containsKey(player);
    }

    public boolean hasPotionTemp(Player player) {
        return playerPotionData.containsKey(player);
    }
    
    public void loadBlockData() {
        String sqlQuery = "SELECT * FROM block_data";
        try {
            PreparedStatement preparedStatement = TD2Core.sql().prepare(sqlQuery);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Location location = Utils.getLocationFromString(resultSet.getString("block_location"));
                Location teleportLocation = Utils.getLocationFromString(resultSet.getString("teleport_location"));
                Location nextCheckpoint = Utils.getLocationFromString(resultSet.getString("next_checkpoint"));
                int cpIndex = resultSet.getInt("cp_index");
                int timeTillNextCheckpoint = resultSet.getInt("time_till_next");
                boolean isCheckpoint = resultSet.getBoolean("is_checkpoint");
                boolean isNoSprint = resultSet.getBoolean("is_nosprint");

                String potionString = resultSet.getString("cp_effect");
                List<PotionEffect> potionEffects = Utils.parsePotions(potionString);
                blockInformation.put(location, new BlockData(teleportLocation, nextCheckpoint, cpIndex,
                        timeTillNextCheckpoint, isCheckpoint, isNoSprint, potionEffects));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error fetching block data.", e);
        }
    }
    
    private void saveBlockInformation(Location clickedLoc, BlockData blockData) {
        if (blockInformation.containsKey(clickedLoc)) {
            String sqlQuery = "UPDATE block_data SET teleport_location = ?, next_checkpoint = ?, time_till_next = ?, is_checkpoint = ?, cp_index = ?, is_nosprint = ?, cp_effect = ? WHERE block_location = ?";
            blockInformation.put(clickedLoc, blockData);
        
            try {
                PreparedStatement preparedStatement = TD2Core.sql().prepare(sqlQuery);
                preparedStatement.setString(1, Utils.getStringFromLocation(blockData.getTeleportLoc()));
                preparedStatement.setString(2, Utils.getStringFromLocation(blockData.getNextCheckpoint()));
                preparedStatement.setInt(3, blockData.getTimeTillNextCheckpoint());
                preparedStatement.setBoolean(4, blockData.isCheckpoint());
                preparedStatement.setInt(5, blockData.getCpIndex());
                preparedStatement.setBoolean(6, blockData.isNoSprint());

                String potionEffects = Utils.parseStringPotion(blockData.getPotionEffects());
                preparedStatement.setString(7, potionEffects);
                preparedStatement.setString(8, Utils.getStringFromLocation(clickedLoc));
                preparedStatement.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            String sqlQuery = "INSERT INTO block_data (block_location, teleport_location, next_checkpoint, time_till_next, is_checkpoint, cp_index, is_nosprint, cp_effect) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            blockInformation.put(clickedLoc, blockData);
            
            try {
                PreparedStatement preparedStatement = TD2Core.sql().prepare(sqlQuery);
                preparedStatement.setString(1, Utils.getStringFromLocation(clickedLoc));
                preparedStatement.setString(2, Utils.getStringFromLocation(blockData.getTeleportLoc()));
                preparedStatement.setString(3, Utils.getStringFromLocation(blockData.getNextCheckpoint()));
                preparedStatement.setInt(4, blockData.getTimeTillNextCheckpoint());
                preparedStatement.setBoolean(5, blockData.isCheckpoint());
                preparedStatement.setInt(6, blockData.getCpIndex());
                preparedStatement.setBoolean(7, blockData.isNoSprint());
                String potionEffects = Utils.parseStringPotion(blockData.getPotionEffects());
                preparedStatement.setString(8, potionEffects);
                preparedStatement.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    private void deleteBlockData(Location location, Player player) {
        blockInformation.remove(location);
        String sqlQuery = "DELETE FROM block_data WHERE block_location = ?";
        try {
            PreparedStatement preparedStatement = TD2Core.sql().prepare(sqlQuery);
            preparedStatement.setString(1, Utils.getStringFromLocation(location));
            preparedStatement.execute();
            player.sendMessage(Message.SUCCESS_REMOVE_BLOCK_DATA.getSuccessMessage());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
