package de.legoshi.td2core.player;

import com.viaversion.viaversion.api.Via;
import de.legoshi.td2core.TD2Core;
import de.legoshi.td2core.kit.*;
import de.legoshi.td2core.listener.item.ItemInteractManager;
import de.legoshi.td2core.map.MapManager;
import de.legoshi.td2core.map.ParkourMap;
import de.legoshi.td2core.map.session.ParkourSession;
import de.legoshi.td2core.map.session.SessionManager;
import de.legoshi.td2core.player.tag.PlayerTag;
import de.legoshi.td2core.player.tag.TagCreator;
import de.legoshi.td2core.util.Message;
import de.legoshi.td2core.util.ScoreboardUtil;
import de.legoshi.td2core.util.Utils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Getter
@Setter
public class ParkourPlayer {
    
    private final PlayerManager playerManager;
    private final SessionManager sessionManager;
    private final MapManager mapManager;
    private final KitManager kitManager;
    
    private final Player player;
    private PlayerTag playerTag;
    private PlayerState playerState;
    private int rank;
    
    private ParkourMap currentParkourMap;
    
    private Kit playerKit;
    private BukkitTask bukkitTask;
    private BukkitTask cpTask;
    private boolean sendMessage;
    private double percentage;
    private int version;
    
    public ParkourPlayer(PlayerManager playerManager, SessionManager sessionManager, KitManager kitManager, Player player) {
        this.playerManager = playerManager;
        this.sessionManager = sessionManager;
        this.mapManager = playerManager.getMapManager();
        this.kitManager = kitManager;
        
        this.player = player;
        this.version = Via.getAPI().getPlayerVersion(player.getUniqueId());
        this.playerState = PlayerState.LOBBY;
        
        this.playerKit = kitManager.getPlayerKit(this, LobbyKit.class);
        setKit();
        
        this.percentage = 0;
        this.rank = 99999;
        
        startCPScheduler();
    }
    
    public void serverJoin() {
        playerManager.put(this);
        ScoreboardUtil.initializeScoreboard(player);
    
        playerManager.loadPercentage(player).thenApply(val -> {
            ScoreboardUtil.setSpawnScoreboardValue(player);
            Bukkit.getOnlinePlayers().forEach(TagCreator::updateRank);
            return null;
        });
    
        sessionManager.init(player);
        
        player.teleport(TD2Core.getSpawn());
        player.setAllowFlight(false);
        Bukkit.broadcastMessage(Message.PLAYER_JOIN.getInfoMessage(player.getName()));
    }
    
    public void serverLeave(boolean shutdown) {
        ParkourSession session = sessionManager.get(player, currentParkourMap);
        if (playerState == PlayerState.STAFF) {
            if (bukkitTask != null) {
                bukkitTask.cancel();
            }
        }
        if (playerState == PlayerState.PARKOUR && session != null) {
            session.setLastMapLocation(player.getLocation());
        }
        if (shutdown) {
            kitManager.savePlayerKit(this);
            playerManager.saveIndividualStats(player, currentParkourMap);
        } else {
            Bukkit.getScheduler().runTaskAsynchronously(TD2Core.getInstance(), () -> {
                kitManager.savePlayerKit(this);
                playerManager.saveIndividualStats(player, currentParkourMap);
                if (bukkitTask != null) bukkitTask.cancel();
                playerManager.remove(this);
                sessionManager.removeAll(player);
                Bukkit.getOnlinePlayers().forEach(all -> all.sendMessage(Message.PLAYER_LEAVE.getInfoMessage(player.getName())));
            });
        }
        cpTask.cancel();
    }
    
    public void mapJoin(ParkourMap map) {
        this.currentParkourMap = map;
        if (playerState != PlayerState.STAFF) {
            updateState(PlayerState.PARKOUR);
            player.setGameMode(GameMode.ADVENTURE);
            player.setAllowFlight(false);
        }
    
        playerManager.loadIndividualStats(player, map).thenApply(value -> {
            ParkourSession session = sessionManager.get(player, map);
            session.setSessionStarted(new Date(System.currentTimeMillis()));
            
            Bukkit.getScheduler().runTask(TD2Core.getInstance(), () -> {
                bukkitTask = Bukkit.getScheduler().runTaskTimerAsynchronously(TD2Core.getInstance(), () -> updateActionBar(session), 0L, 20L);
                
                if (playerState == PlayerState.STAFF) {
                    player.setGameMode(GameMode.CREATIVE);
                } else {
                    if (session.getLastMapLocation() != null) {
                        player.teleport(session.getLastMapLocation());
                    } else if (session.getLastCheckpointLocation() != null) {
                        player.teleport(session.getLastCheckpointLocation());
                    } else {
                        player.teleport(map.getStartLocation());
                    }
                }
                
                player.addPotionEffects(map.getPotionEffects());
                player.sendMessage(Message.PLAYER_MAP_JOIN.getInfoMessage(map.getMapName()));
            });
            return null;
        });
    }
    
    public void mapLeave(boolean cleared) {
        if (currentParkourMap == null) return;
        
        ParkourSession session = sessionManager.get(player, currentParkourMap);
        if (playerState == PlayerState.PARKOUR && session != null && !cleared) {
            session.setLastMapLocation(player.getLocation());
        }
        
        Bukkit.getScheduler().runTaskAsynchronously(TD2Core.getInstance(), () -> {
            playerManager.saveIndividualStats(player, currentParkourMap);
            currentParkourMap = null;
            if (playerState != PlayerState.STAFF) {
                updateState(PlayerState.LOBBY);
            }
            bukkitTask.cancel();
            clearPotionEffects();
        });
        
        player.playSound(player.getLocation(), Sound.BLOCK_PORTAL_TRAVEL, 1.0F, 1.0F);
        
        if (playerState != PlayerState.STAFF) {
            player.teleport(TD2Core.getSpawn());
        }
    }
    
    public void activateCheckPoint(Location location, boolean custom, boolean isCP) {
        ParkourSession session = sessionManager.get(player, currentParkourMap);
        
        if (!custom) {
            location.setYaw(player.getLocation().getYaw());
            location.setPitch(player.getLocation().getPitch());
            
            Location lastLocation = session.getLastCheckpointLocation();
            if (session.getLastCheckpointLocation() != null && lastLocation.getX() == location.getX()
                && lastLocation.getY() == location.getY() && lastLocation.getZ() == location.getZ()) {
                session.setLastCheckpointLocation(location);
                return;
            }
        }
        
        session.setLastCheckpointLocation(location);
        if (playerState == PlayerState.STAFF) {
            session.setPracCPLocation(null);
        }
        if (isCP) {
            if (ItemInteractManager.hasClickedCP(player)) return;
            ItemInteractManager.addCP(player);
            player.sendMessage(Message.CHECKPOINT_REACHED.getInfoMessage());
        }
    }
    
    public void checkClickedBlock(Location clickedBlock, boolean isCP, int cpIndex) {
        if (playerState == PlayerState.STAFF) return;
        if (playerState != PlayerState.PARKOUR) return;
        if (clickedBlock.add(0, -1, 0).getBlock().getType() == Material.BEDROCK) return;
        
        ParkourSession session = sessionManager.get(player, currentParkourMap);
        if (session.isPassed()) return;
        
        Bukkit.getScheduler().runTaskAsynchronously(TD2Core.getInstance(), () -> {
            String checkQuery = "SELECT 1 FROM collected_cp WHERE userid = ? AND mapname = ? AND block_location = ?";
            
            try {
                PreparedStatement checkStatement = TD2Core.sql().prepare(checkQuery);
                checkStatement.setString(1, player.getUniqueId().toString());
                checkStatement.setString(2, currentParkourMap.mapName);
                checkStatement.setString(3, Utils.getStringFromLocation(clickedBlock));
                
                ResultSet resultSet = checkStatement.executeQuery();
                
                // If the checkpoint is already present
                if (!resultSet.next()) {
                    String insertQuery = "INSERT INTO collected_cp (userid, mapname, block_location) VALUES (?, ?, ?)";
                    PreparedStatement insertStatement = TD2Core.sql().prepare(insertQuery);
                    insertStatement.setString(1, player.getUniqueId().toString());
                    insertStatement.setString(2, currentParkourMap.mapName);
                    insertStatement.setString(3, Utils.getStringFromLocation(clickedBlock));
                    insertStatement.executeUpdate();
                    
                    updatePercentage(currentParkourMap, cpIndex);
                    Bukkit.getScheduler().runTaskLaterAsynchronously(TD2Core.getInstance(), () -> {
                        TD2Core.getInstance().getDiscordManager().getRoleManager().cpRoleCheck(currentParkourMap.getMapName(), player.getUniqueId().toString());
                    }, 20L*5);
                    Bukkit.getScheduler().runTaskAsynchronously(TD2Core.getInstance(), () -> {
                        TD2Core.getInstance().getDiscordManager().sendCheckPointMessage(player);
                    });
                    if (!isCP) {
                        player.sendMessage(Message.PLAYER_STEP_PROGRESS.getInfoMessage());
                    }
                }
                
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
    
    public void activateGoal(Location location) {
        if (currentParkourMap.getEndLocation().equals(location)) {
            
            // do not execute multiple times!
            if (ItemInteractManager.hasClickedCP(player)) return;
            ItemInteractManager.addCP(player);
            
            ParkourSession session = sessionManager.get(player, currentParkourMap);
            
            Location lastLocation = session.getLastCheckpointLocation();
            if (session.getLastCheckpointLocation() != null && lastLocation.getX() == location.getX()
                && lastLocation.getY() == location.getY() && lastLocation.getZ() == location.getZ()) {
                session.setLastCheckpointLocation(location);
                return;
            }
            
            session.setLastPracLocation(null);
            session.setLastMapLocation(null);
            session.setFinished(new Date(System.currentTimeMillis()));
            
            String mapName = currentParkourMap.getDisplayName();
            ParkourMap copyPKMap = currentParkourMap;
            
            mapLeave(true);
            
            updatePercentage(copyPKMap, copyPKMap.getCpCount());
            Bukkit.getScheduler().runTaskLaterAsynchronously(TD2Core.getInstance(), () -> {
                TD2Core.getInstance().getDiscordManager().getRoleManager().cpRoleCheck(copyPKMap.getMapName(), player.getUniqueId().toString());
            }, 20L*5);
            Bukkit.broadcastMessage(Message.GOAL_REACHED.getInfoMessage(player.getName(), mapName));
            Bukkit.getScheduler().runTaskAsynchronously(TD2Core.getInstance(), () -> {
                TD2Core.getInstance().getDiscordManager().sendCompletionMessage(player);
            });
        }
    }
    
    private void updatePercentage(ParkourMap parkourMap, int cpIndex) {
        TD2Core.getInstance().mapLBCache.addCPCount(player.getUniqueId(), currentParkourMap, cpIndex);
        playerManager.loadPercentage(player).thenApplyAsync(val -> {
            TD2Core.getInstance().mapLBCache.reloadCache(player.getUniqueId(), parkourMap);
            TD2Core.getInstance().globalLBCache.reloadCache();
            ScoreboardUtil.updatePercentage(player);
            Bukkit.getOnlinePlayers().forEach(TagCreator::updateRank);
            return null;
        });
    }
    
    public void triggerFail() {
        if (playerState == PlayerState.PRACTICE || playerState == PlayerState.STAFF) return;
        ParkourSession session = sessionManager.get(player, currentParkourMap);
        session.setFails(session.getFails() + 1);
        updateActionBar(session);
    }
    
    public void setKit() {
        player.getInventory().clear();
        for (int i = 0; i < 40; i++) {
            ItemStack item = playerKit.getItem(i);
            if (item != null && item.getType() != Material.AIR) {
                if (item.getType().equals(Material.GOLD_HELMET) || item.getType().equals(Material.DIAMOND_HELMET)) {
                    player.getInventory().setHelmet(item);
                } else {
                    player.getInventory().setItem(i, item);
                }
            }
        }
    }
    
    public void switchPlayerState(PlayerState state) {
        ParkourSession session = sessionManager.get(player, currentParkourMap);
        PlayerState prevState = playerState;
        
        switch (state) {
            case PRACTICE: {
                if (!Utils.isOnGround(player)) {
                    player.sendMessage(Message.PLAYER_NOT_ON_GROUND.getWarningMessage());
                    return;
                }
                
                updateState(state);
                player.setAllowFlight(true);
                session.setPlayTime(session.getPlayTime());
                session.setLastMapLocation(player.getLocation());
                session.setLastPracLocation(player.getLocation());
                
                if (currentParkourMap.isRedstone()) {
                    player.teleport(player.getLocation().add(100000, 0, 100000));
                }
                
                session.setPracCPLocation(player.getLocation());
                player.sendMessage(Message.PLAYER_SWITCH_TO_PRACTICE.getInfoMessage());
    
                if (prevState == PlayerState.STAFF) {
                    Bukkit.getOnlinePlayers().forEach(TagCreator::updateRank);
                }
                break;
            }
            case PARKOUR: {
                updateState(state);
                session.setSessionStarted(new Date(System.currentTimeMillis()));
                player.setAllowFlight(false);
                player.teleport(session.getLastPracLocation());
                player.sendMessage(Message.PLAYER_SWITCH_TO_PARKOUR.getInfoMessage());
    
                if (prevState == PlayerState.STAFF) {
                    Bukkit.getOnlinePlayers().forEach(TagCreator::updateRank);
                }
                break;
            }
            case STAFF: {
                if (playerState == PlayerState.PARKOUR && session != null) {
                    session.setLastMapLocation(player.getLocation());
                }
                
                Bukkit.getScheduler().runTaskAsynchronously(TD2Core.getInstance(), () -> {
                    if (currentParkourMap != null) {
                        playerManager.saveIndividualStats(player, currentParkourMap);
                    }
    
                    clearPotionEffects();
                    updateState(state);
    
                    Bukkit.getOnlinePlayers().forEach(TagCreator::updateRank);
                    ParkourMap parkourMap = mapManager.get("Initial TD2");
                    currentParkourMap = parkourMap;
                    
                    ParkourSession parkourSession = new ParkourSession();
                    parkourSession.setLastCheckpointLocation(player.getLocation());
    
                    sessionManager.put(player, parkourMap, parkourSession);
                    updateActionBar(session);
                    if (this.bukkitTask == null || this.bukkitTask.isCancelled()) {
                        bukkitTask = Bukkit.getScheduler().runTaskTimerAsynchronously(TD2Core.getInstance(), () -> updateActionBar(session), 0L, 20L);
                    }
                });
                
                player.setAllowFlight(true);
                break;
            }
            case LOBBY: {
                updateState(state);
    
                if (prevState == PlayerState.STAFF) {
                    Bukkit.getOnlinePlayers().forEach(TagCreator::updateRank);
                }
            }
        }
    }
    
    public void startCPScheduler() {
        this.cpTask = Bukkit.getScheduler().runTaskTimerAsynchronously(TD2Core.getInstance(), () -> {
            ParkourSession session = sessionManager.get(player, currentParkourMap);
            if (session != null && session.getNextCP() != null && (playerState == PlayerState.PARKOUR || playerState == PlayerState.STAFF)) {
                player.spawnParticle(Particle.VILLAGER_HAPPY, session.getNextCP().clone().add(0.5, 0, 0.5), 50);
            }
        }, 0L, 20L);
    }
    
    public boolean isNextCP(Location location) {
        ParkourSession session = sessionManager.get(player, currentParkourMap);
        if (session.getNextCP() == null) return true;
        return session.getNextCP().equals(location);
    }
    
    private void updateState(PlayerState state) {
        kitManager.updatePlayerKitOrder(this);
        this.playerState = state;
        switch (playerState) {
            case PARKOUR:
                playerKit = kitManager.getPlayerKit(this, ParkourKit.class);
                break;
            case PRACTICE:
                playerKit = kitManager.getPlayerKit(this, PracticeKit.class);
                break;
            case LOBBY:
                playerKit = kitManager.getPlayerKit(this, LobbyKit.class);
                break;
            case STAFF:
                playerKit = kitManager.getPlayerKit(this, StaffKit.class);
                break;
        }
        setKit();
    }
    
    public void updateActionBar(ParkourSession session) {
        if (playerState == PlayerState.STAFF) {
            Utils.sendActionBar(player, "§cSTAFF Mode");
            return;
        }
        long playTime = (session.getPlayTime() / 1000);
        if (playerState == PlayerState.PRACTICE) playTime = session.getPausedTime() / 1000;
        String nextCPString = "";
        String infoString = "§6Time: §7" + Utils.secondsToTime((int) playTime) + " - §cFails: §7" + session.getFails();
        if (session.getNextCP() != null)
            nextCPString = "§7 - " + Message.NEXT_CP.getMessage((int) session.getNextCP().getX() + "", (int) session.getNextCP().getY() + "", (int) session.getNextCP().getZ() + "");
        Utils.sendActionBar(player, infoString + nextCPString);
    }
    
    public void clearPotionEffects() {
        player.removePotionEffect(PotionEffectType.SPEED);
    }
    
}
