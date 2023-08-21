package de.legoshi.td2core.player;

import com.viaversion.viaversion.api.Via;
import de.legoshi.td2core.TD2Core;
import de.legoshi.td2core.kit.*;
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

@Getter
@Setter
public class ParkourPlayer {
    
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
    
    public ParkourPlayer(Player player) {
        this.player = player;
        this.version = Via.getAPI().getPlayerVersion(player.getUniqueId());
        this.playerState = PlayerState.LOBBY;
        this.playerKit = new ParkourKit(version);
        this.percentage = 0;
        this.rank = 99999;
        
        startCPScheduler();
    }
    
    public void serverJoin() {
        PlayerManager.put(this);
        ScoreboardUtil.initializeScoreboard(player);
        
        PlayerManager.loadPercentage(player).thenApply(val -> {
            ScoreboardUtil.setSpawnScoreboardValue(player);
            Bukkit.getOnlinePlayers().forEach(TagCreator::updateRank);
            return null;
        });
        
        SessionManager.init(player);
        
        updateState(PlayerState.LOBBY);
        
        player.teleport(TD2Core.getInstance().spawnLocation);
        player.setAllowFlight(false);
        Bukkit.broadcastMessage(Message.PLAYER_JOIN.getInfoMessage(player.getName()));
    }
    
    public void serverLeave(boolean shutdown) {
        ParkourSession session = SessionManager.get(player, currentParkourMap);
        if (playerState == PlayerState.STAFF_MODE) {
            if (bukkitTask != null) {
                bukkitTask.cancel();
            }
        }
        if (playerState == PlayerState.PARKOUR && session != null) {
            session.setLastMapLocation(player.getLocation());
        }
        if (shutdown) {
            PlayerManager.saveIndividualStats(player, currentParkourMap);
        } else {
            Bukkit.getScheduler().runTaskAsynchronously(TD2Core.getInstance(), () -> {
                PlayerManager.saveIndividualStats(player, currentParkourMap);
                if (bukkitTask != null) bukkitTask.cancel();
                PlayerManager.remove(this);
                SessionManager.removeAll(player);
                Bukkit.getOnlinePlayers().forEach(all -> all.sendMessage(Message.PLAYER_LEAVE.getInfoMessage(player.getName())));
            });
        }
        cpTask.cancel();
    }
    
    public void mapJoin(ParkourMap map) {
        this.currentParkourMap = map;
        if (playerState != PlayerState.STAFF_MODE) {
            updateState(PlayerState.PARKOUR);
            player.setGameMode(GameMode.ADVENTURE);
            player.setAllowFlight(false);
        }
        
        PlayerManager.loadIndividualStats(player, map).thenApply(value -> {
            ParkourSession session = SessionManager.get(player, map);
            session.setSessionStarted(new Date(System.currentTimeMillis()));
            
            Bukkit.getScheduler().runTask(TD2Core.getInstance(), () -> {
                bukkitTask = Bukkit.getScheduler().runTaskTimerAsynchronously(TD2Core.getInstance(), () -> updateActionBar(session), 0L, 20L);
                
                if (playerState == PlayerState.STAFF_MODE) {
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
        
        ParkourSession session = SessionManager.get(player, currentParkourMap);
        if (playerState == PlayerState.PARKOUR && session != null && !cleared) {
            session.setLastMapLocation(player.getLocation());
        }
        
        Bukkit.getScheduler().runTaskAsynchronously(TD2Core.getInstance(), () -> {
            PlayerManager.saveIndividualStats(player, currentParkourMap);
            currentParkourMap = null;
            if (playerState != PlayerState.STAFF_MODE) {
                updateState(PlayerState.LOBBY);
            }
            bukkitTask.cancel();
            clearPotionEffects();
        });
        
        player.playSound(player.getLocation(), Sound.BLOCK_PORTAL_TRAVEL, 1.0F, 1.0F);
        
        if (playerState != PlayerState.STAFF_MODE) {
            player.teleport(TD2Core.getInstance().spawnLocation);
        }
    }
    
    public void activateCheckPoint(Location location, boolean custom) {
        ParkourSession session = SessionManager.get(player, currentParkourMap);
        
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
        if (playerState == PlayerState.STAFF_MODE) {
            session.setPracCPLocation(null);
        }
    }
    
    public void checkClickedBlock(Location clickedBlock, boolean isCP) {
        if (playerState == PlayerState.STAFF_MODE) return;
        if (playerState != PlayerState.PARKOUR) return;
        if (clickedBlock.add(0, -1, 0).getBlock().getType() == Material.BEDROCK) return;
        
        ParkourSession session = SessionManager.get(player, currentParkourMap);
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
    
                    if (isCP) {
                        player.sendMessage(Message.CHECKPOINT_REACHED.getInfoMessage());
                    } else {
                        player.sendMessage(Message.PLAYER_STEP_PROGRESS.getInfoMessage());
                    }
                    
                    updatePercentage(currentParkourMap);
                    Bukkit.getScheduler().runTaskAsynchronously(TD2Core.getInstance(), () -> {
                        TD2Core.getInstance().getDiscordManager().sendCheckPointMessage(player);
                    });
                }
                
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
    
    public void activateGoal(Location location) {
        if (currentParkourMap.getEndLocation().equals(location)) {
            ParkourSession session = SessionManager.get(player, currentParkourMap);
            
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
            
            updatePercentage(copyPKMap);
            Bukkit.broadcastMessage(Message.GOAL_REACHED.getInfoMessage(player.getName(), mapName));
            Bukkit.getScheduler().runTaskAsynchronously(TD2Core.getInstance(), () -> {
                TD2Core.getInstance().getDiscordManager().sendCompletionMessage(player);
            });
        }
    }
    
    private void updatePercentage(ParkourMap parkourMap) {
        TD2Core.getInstance().mapLBCache.addCPCount(player.getUniqueId(), currentParkourMap);
        PlayerManager.loadPercentage(player).thenApplyAsync(val -> {
            TD2Core.getInstance().mapLBCache.reloadCache(player.getUniqueId(), parkourMap);
            TD2Core.getInstance().globalLBCache.reloadCache();
            ScoreboardUtil.updatePercentage(player);
            Bukkit.getOnlinePlayers().forEach(TagCreator::updateRank);
            return null;
        });
    }
    
    public void triggerFail() {
        if (playerState == PlayerState.PRACTICE || playerState == PlayerState.STAFF_MODE) return;
        ParkourSession session = SessionManager.get(player, currentParkourMap);
        session.setFails(session.getFails() + 1);
        updateActionBar(session);
    }
    
    public void setKit() {
        player.getInventory().clear();
        for (int i = 0; i < 9; i++) {
            ItemStack item = playerKit.getInventory().getItem(i);
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
        ParkourSession session = SessionManager.get(player, currentParkourMap);
        
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
                session.setPracCPLocation(player.getLocation());
                player.sendMessage(Message.PLAYER_SWITCH_TO_PRACTICE.getInfoMessage());
                break;
            }
            case PARKOUR: {
                updateState(state);
                session.setSessionStarted(new Date(System.currentTimeMillis()));
                player.setAllowFlight(false);
                player.teleport(session.getLastPracLocation());
                player.sendMessage(Message.PLAYER_SWITCH_TO_PARKOUR.getInfoMessage());
                break;
            }
            case STAFF_MODE: {
                if (playerState == PlayerState.PARKOUR && session != null) {
                    session.setLastMapLocation(player.getLocation());
                }
                
                Bukkit.getScheduler().runTaskAsynchronously(TD2Core.getInstance(), () -> {
                    if (currentParkourMap != null) {
                        PlayerManager.saveIndividualStats(player, currentParkourMap);
                    }
    
                    clearPotionEffects();
                    updateState(state);
                    ParkourMap parkourMap = MapManager.get("Initial TD2");
                    currentParkourMap = parkourMap;
                    
                    ParkourSession parkourSession = new ParkourSession();
                    parkourSession.setLastCheckpointLocation(player.getLocation());
                    
                    SessionManager.put(player, parkourMap, parkourSession);
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
            }
        }
    }
    
    public void startCPScheduler() {
        this.cpTask = Bukkit.getScheduler().runTaskTimerAsynchronously(TD2Core.getInstance(), () -> {
            ParkourSession session = SessionManager.get(player, currentParkourMap);
            if (session != null && session.getNextCP() != null && (playerState == PlayerState.PARKOUR || playerState == PlayerState.STAFF_MODE)) {
                player.spawnParticle(Particle.VILLAGER_HAPPY, session.getNextCP().clone().add(0.5, 0, 0.5), 50);
            }
        }, 0L, 20L);
    }
    
    public boolean isNextCP(Location location) {
        ParkourSession session = SessionManager.get(player, currentParkourMap);
        if (session.getNextCP() == null) return true;
        return session.getNextCP().equals(location);
    }
    
    private void updateState(PlayerState state) {
        this.playerState = state;
        switch (playerState) {
            case PARKOUR:
                playerKit = new ParkourKit(version);
                break;
            case PRACTICE:
                playerKit = new PracticeKit(version);
                break;
            case LOBBY:
                playerKit = new LobbyKit();
                break;
            case STAFF_MODE:
                playerKit = new StaffKit(version);
                break;
        }
        setKit();
    }
    
    public void updateActionBar(ParkourSession session) {
        if (playerState == PlayerState.STAFF_MODE) {
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