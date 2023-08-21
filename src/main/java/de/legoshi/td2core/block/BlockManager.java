package de.legoshi.td2core.block;

import de.legoshi.td2core.TD2Core;
import de.legoshi.td2core.gui.CheckPointGUI;
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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class BlockManager implements Listener {
    
    private final HashMap<Location, BlockData> blockInformation;
    
    private final HashMap<Player, Location> playerPreciseData;
    private final HashMap<Player, Location> playerNextData;
    
    public BlockManager() {
        this.blockInformation = new HashMap<>();
        this.playerPreciseData = new HashMap<>();
        this.playerNextData = new HashMap<>();
    }
    
    @EventHandler
    public void blockInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        
        Material blockType = event.getClickedBlock().getType();
        if (blockType != Material.IRON_PLATE & blockType != Material.WOOD_PLATE && blockType != Material.STONE_PLATE) return;
        
        
        Player player = event.getPlayer();
        ParkourPlayer parkourPlayer = PlayerManager.get(player);
        if (parkourPlayer.getPlayerState() == PlayerState.STAFF_MODE || player.hasPermission("td2core.edit_cp")) {
            Location clickedBlock = event.getClickedBlock().getLocation();
            new CheckPointGUI().openGui(player, this, clickedBlock);
        }
    }
    
    @EventHandler
    public void blockInteract(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Location clickedLoc = event.getBlock().getLocation();
        
        if (blockInformation.containsKey(clickedLoc) && !player.hasPermission("td2core.edit_cp")) {
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
    
    public void saveSPC(Player player, Location teleportLocation) {
        Location checkpoint = playerPreciseData.get(player);
        BlockData blockData = blockInformation.getOrDefault(checkpoint, new BlockData());
        blockData.setTeleportLoc(teleportLocation);
        
        playerPreciseData.remove(player);
        saveBlockInformation(checkpoint, blockData);
    }
    
    public Location getNextCPLocation(Location location) {
        if (hasEntry(location)) {
            return blockInformation.get(location).getNextCheckpoint();
        }
        return null;
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
    
    public boolean hasSPCTemp(Player player) {
        return playerPreciseData.containsKey(player);
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
                int timeTillNextCheckpoint = resultSet.getInt("time_till_next");
                boolean isCheckpoint = resultSet.getBoolean("is_checkpoint");
                blockInformation.put(location, new BlockData(teleportLocation, nextCheckpoint, timeTillNextCheckpoint, isCheckpoint));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error fetching block data.", e);
        }
    }
    
    private void saveBlockInformation(Location clickedLoc, BlockData blockData) {
        if (blockInformation.containsKey(clickedLoc)) {
            String sqlQuery = "UPDATE block_data SET teleport_location = ?, next_checkpoint = ?, time_till_next = ?, is_checkpoint = ? WHERE block_location = ?";
            blockInformation.put(clickedLoc, blockData);
        
            try {
                PreparedStatement preparedStatement = TD2Core.sql().prepare(sqlQuery);
                preparedStatement.setString(1, Utils.getStringFromLocation(blockData.getTeleportLoc()));
                preparedStatement.setString(2, Utils.getStringFromLocation(blockData.getNextCheckpoint()));
                preparedStatement.setInt(3, blockData.getTimeTillNextCheckpoint());
                preparedStatement.setBoolean(4, blockData.isCheckpoint());
                preparedStatement.setString(5, Utils.getStringFromLocation(clickedLoc));
                preparedStatement.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            String sqlQuery = "INSERT INTO block_data (block_location, teleport_location, next_checkpoint, time_till_next, is_checkpoint) VALUES (?, ?, ?, ?, ?)";
            blockInformation.put(clickedLoc, blockData);
            
            try {
                PreparedStatement preparedStatement = TD2Core.sql().prepare(sqlQuery);
                preparedStatement.setString(1, Utils.getStringFromLocation(clickedLoc));
                preparedStatement.setString(2, Utils.getStringFromLocation(blockData.getTeleportLoc()));
                preparedStatement.setString(3, Utils.getStringFromLocation(blockData.getNextCheckpoint()));
                preparedStatement.setInt(4, blockData.getTimeTillNextCheckpoint());
                preparedStatement.setBoolean(5, blockData.isCheckpoint());
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
