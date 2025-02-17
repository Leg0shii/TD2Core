package de.legoshi.td2core.listener;

import de.legoshi.td2core.TD2Core;
import de.legoshi.td2core.block.BlockManager;
import de.legoshi.td2core.config.ConfigManager;
import de.legoshi.td2core.config.PlayerConfig;
import de.legoshi.td2core.gui.GlobalLBGUI;
import de.legoshi.td2core.gui.SectionGUI;
import de.legoshi.td2core.gui.SettingsGUI;
import de.legoshi.td2core.listener.item.ItemInteractManager;
import de.legoshi.td2core.map.MapManager;
import de.legoshi.td2core.map.ParkourMap;
import de.legoshi.td2core.map.session.SessionManager;
import de.legoshi.td2core.player.ParkourPlayer;
import de.legoshi.td2core.map.session.ParkourSession;
import de.legoshi.td2core.player.PlayerManager;
import de.legoshi.td2core.player.PlayerState;
import de.legoshi.td2core.util.ItemUtils;
import de.legoshi.td2core.util.Message;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Door;
import org.bukkit.material.TrapDoor;
import org.bukkit.potion.PotionEffect;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
public class ParkourListener implements Listener {
    
    private final BlockManager blockManager;
    private final MapManager mapManager;
    private final PlayerManager playerManager;
    private final SessionManager sessionManager;
    private final ConfigManager configManager;

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        ItemStack itemStack = event.getItem();
        ParkourPlayer parkourPlayer = playerManager.get(event.getPlayer());

        if (parkourPlayer.getPlayerState().equals(PlayerState.STAFF) || parkourPlayer.getPlayerState().equals(PlayerState.PLOT)) {
            onTrapDoorClick(event);
        } else {
            // this disables trapdoors for staff players (ONLY for certain maps) when they are not in staff
            // currently, staff players can always flip trapdoors (which are disabled with world guard for normal players)
            // another solution is to give a perm whenever /staff is run, that allows to flip trapdoors and remove the perm
            // when player is not in /staff
            String[] mapNames = new String[]{"Initial TD2", "InitialTD2 Rework", "Buffed TD1", "LD TD1"};
            if (parkourPlayer.getCurrentParkourMap() != null
                    && Arrays.stream(mapNames).anyMatch(name -> name.equals(parkourPlayer.getCurrentParkourMap().mapName))) {
                onWoodTrapdoorClick(event);
            }
        }
        
        if (event.getHand() != null && event.getHand().equals(EquipmentSlot.OFF_HAND)) return;
        if (event.getAction().equals(Action.LEFT_CLICK_AIR) || event.getAction().equals(Action.LEFT_CLICK_BLOCK)) return;

        if (ItemInteractManager.hasClickedItem(event.getPlayer())) return;
        ItemInteractManager.addItem(event.getPlayer());
        
        itemInteract(itemStack, event);
        pressurePlateInteract(event);
        buttonInteract(event);
    }

    private void itemInteract(ItemStack itemStack, PlayerInteractEvent event) {
        if (ItemUtils.hasNbtId(itemStack, "cp")) checkPointClick(event);
        if (ItemUtils.hasNbtId(itemStack, "prac")) practiceClick(event);
        if (ItemUtils.hasNbtId(itemStack, "unprac")) unPracticeClick(event);
        if (ItemUtils.hasNbtId(itemStack, "fly")) flyClick(event);
        if (ItemUtils.hasNbtId(itemStack, "help")) helpClick(event);
        if (ItemUtils.hasNbtId(itemStack, "setting")) settingClick(event);
        if (ItemUtils.hasNbtId(itemStack, "leave")) mapLeaveClick(event);
        if (ItemUtils.hasNbtId(itemStack, "select")) selectClick(event);
        if (ItemUtils.hasNbtId(itemStack, "leaderboard")) leaderBoardClick(event);
        if (ItemUtils.hasNbtId(itemStack, "set")) setCPClick(event);
    }

    private void settingClick(PlayerInteractEvent event) {
        event.setCancelled(true);
        new SettingsGUI(configManager).openGui(event.getPlayer(), null);
    }

    private void setCPClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        event.setCancelled(true);
        ParkourPlayer parkourPlayer = playerManager.get(player);
        ParkourMap parkourMap = parkourPlayer.getCurrentParkourMap();
        ParkourSession session = sessionManager.get(player, parkourMap);
        session.setPracCPLocation(player.getLocation());
        player.sendMessage(Message.SET_CP.getInfoMessage());
    }
    
    private void checkPointClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        event.setCancelled(true);
        ParkourPlayer parkourPlayer = playerManager.get(player);
        ParkourMap parkourMap = parkourPlayer.getCurrentParkourMap();
        ParkourSession session = sessionManager.get(player, parkourMap);
        parkourPlayer.triggerFail();
        
        Location location = session.getLastCheckpointLocation();
        if (parkourPlayer.getPlayerState() == PlayerState.PRACTICE) {
            location = session.getPracCPLocation();
            if (location == null) {
                location = parkourMap.getStartLocation();
            }
            player.teleport(location);
            return;
        }
        
        if (parkourPlayer.getPlayerState() == PlayerState.STAFF || parkourPlayer.getPlayerState() == PlayerState.PLOT) {
            Location pracLocation = session.getPracCPLocation();
            if (pracLocation == null) {
                if (location == null) {
                    if (parkourPlayer.getPlayerState() == PlayerState.STAFF) {
                        player.teleport(TD2Core.getSpawn());
                    } else {
                        player.teleport(TD2Core.getPlotSpawn());
                    }
                } else {
                    player.teleport(location);
                }
            } else {
                player.teleport(pracLocation);
            }
            return;
        }
        
        if (location == null) {
            location = parkourMap.getStartLocation();
        }

        session.setCurrTimeTillNext(System.currentTimeMillis());
        session.setNextCP(null);
        player.teleport(location);
    }
    
    private void practiceClick(PlayerInteractEvent event) {
        event.setCancelled(true);
        event.getPlayer().performCommand("prac");
    }
    
    private void unPracticeClick(PlayerInteractEvent event) {
        event.setCancelled(true);
        event.getPlayer().performCommand("prac");
    }
    
    private void flyClick(PlayerInteractEvent event) {
        event.setCancelled(true);
        Player player = event.getPlayer();
        ParkourPlayer parkourPlayer = playerManager.get(player);
        if (parkourPlayer.getPlayerState() == PlayerState.PRACTICE || parkourPlayer.getPlayerState() == PlayerState.STAFF
            || parkourPlayer.getPlayerState() == PlayerState.PLOT) {
            player.setAllowFlight(!player.getAllowFlight());
            
            String prefix = "§a";
            if (!player.getAllowFlight()) prefix = "§c";
            
            player.sendMessage(Message.PLAYER_SET_FLY.getInfoMessage(prefix + player.getAllowFlight()));
        }
    }
    
    private void mapLeaveClick(PlayerInteractEvent event) {
        event.setCancelled(true);
        event.getPlayer().performCommand("leave");
    }
    
    private void helpClick(PlayerInteractEvent event) {
        event.setCancelled(true);
        event.getPlayer().performCommand("help");
    }
    
    private void selectClick(PlayerInteractEvent event) {
        event.setCancelled(true);
    
        Player player = event.getPlayer();
        ParkourPlayer parkourPlayer = playerManager.get(player);
        if (parkourPlayer.isTutorial()) {
            player.sendMessage(Message.ERROR_IN_TUTORIAL.getWarningMessage());
            return;
        }
        
        new SectionGUI(mapManager, playerManager, sessionManager, configManager).openGui(event.getPlayer(), null);
    }
    
    private void leaderBoardClick(PlayerInteractEvent event) {
        event.setCancelled(true);
        new GlobalLBGUI(configManager).openGui(event.getPlayer(), null);
    }
    
    private void buttonInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ParkourPlayer parkourPlayer = playerManager.get(player);
        if (parkourPlayer.getPlayerState() == PlayerState.PRACTICE || parkourPlayer.getPlayerState() == PlayerState.PARKOUR
                || parkourPlayer.getPlayerState() == PlayerState.STAFF || parkourPlayer.getPlayerState() == PlayerState.PLOT) return;
        if (event.getItem() != null && (event.getItem().getType().equals(Material.SHIELD) || event.getItem().getType().equals(Material.DIAMOND_SWORD))) return;
        event.setCancelled(true);
    }
    
    private void pressurePlateInteract(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.PHYSICAL)) return;
        event.setCancelled(true);
    
        Player player = event.getPlayer();
        ParkourPlayer parkourPlayer = playerManager.get(player);
    
        Location cpLocation = event.getClickedBlock().getLocation();
        if (parkourPlayer.isTutorial()) {
            tutorialPlateInteract(parkourPlayer, cpLocation);
        }
    
        if (parkourPlayer.getPlayerState() == PlayerState.PARKOUR || parkourPlayer.getPlayerState() == PlayerState.STAFF) {
    
            ParkourSession session = sessionManager.get(player, parkourPlayer.getCurrentParkourMap());
            Material pressurePlate = event.getClickedBlock().getType();
            Location nextCP = session.getNextCP();
        
            if (!parkourPlayer.isNextCP(cpLocation)) {
                return;
            } else {
                player.getInventory().remove(Material.COMPASS);
                session.setNextCP(null);
            }
        
            if (pressurePlate == Material.IRON_PLATE || pressurePlate == Material.WOOD_PLATE || pressurePlate == Material.STONE_PLATE) {
                event.setUseInteractedBlock(Event.Result.DENY);
                
                if (blockManager.hasNext(cpLocation)) {
                    if (nextCP == null) {
                        Location nextCPLocation = blockManager.getNextCPLocation(cpLocation);
                        session.setNextCP(nextCPLocation);
    
                        if (parkourPlayer.getPlayerState() != PlayerState.STAFF
                        && parkourPlayer.getPlayerState() != PlayerState.PLOT) parkourPlayer.updateActionBar(session);
                    
                        player.getInventory().addItem(new ItemStack(Material.COMPASS));
                        player.setCompassTarget(nextCPLocation.clone().add(0.5, 0, 0.5));
                    }
                }
                if (blockManager.hasTP(cpLocation)) {
                    cpLocation = blockManager.getTeleportPos(cpLocation).clone();
                    player.teleport(cpLocation);
                    player.playSound(player.getPlayer().getLocation(), Sound.ENTITY_ENDERMEN_TELEPORT, 1.0F, 1.0F);
                    if (pressurePlate == Material.IRON_PLATE) {
                        boolean isCP = blockManager.isCheckpoint(event.getClickedBlock().getLocation());
                        if (isCP) {
                            parkourPlayer.activateCheckPoint(cpLocation, true, true);
                        }
                        int clickedCP = blockManager.getClickedCPIndex(event.getClickedBlock().getLocation());
                        parkourPlayer.checkClickedBlock(event.getClickedBlock().getLocation(), isCP, clickedCP);
                        return;
                    }
                }
            }

            int time = blockManager.getTimeTillNext(cpLocation);
            if (time != -1) {
                session.setTimeTillNextTicks(time);
                session.setCurrTimeTillNext(System.currentTimeMillis());
            } else {
                session.setTimeTillNextTicks(-1);
            }

            session.setNoSprint(blockManager.isNoSprint(cpLocation));
            parkourPlayer.updateNoSprint(session.isNoSprint());

            parkourPlayer.clearPotionEffects();
            session.setCurrentEffects((List<PotionEffect>) blockManager.getPotionEffects(cpLocation));
            player.addPotionEffects(blockManager.getPotionEffects(cpLocation));

            if (parkourPlayer.getPlayerState() == PlayerState.PLOT) return;
            if (pressurePlate == Material.IRON_PLATE) {
                event.setUseInteractedBlock(Event.Result.DENY);
                
                cpLocation = cpLocation.clone().add(0.5, 0, 0.5);
                boolean isCP = blockManager.isCheckpoint(event.getClickedBlock().getLocation());
                if (isCP) {
                    parkourPlayer.activateCheckPoint(cpLocation, false, true);
                }
                int clickedCP = blockManager.getClickedCPIndex(event.getClickedBlock().getLocation());
                parkourPlayer.checkClickedBlock(event.getClickedBlock().getLocation(), isCP, clickedCP);
                player.getPlayer().playSound(player.getPlayer().getLocation(), Sound.BLOCK_WOOD_BUTTON_CLICK_ON, 1.0F, 1.0F);
            }
        
            if (pressurePlate == Material.GOLD_PLATE) {
                if (parkourPlayer.getPlayerState() != PlayerState.PARKOUR) return;
                parkourPlayer.activateGoal(cpLocation);
                Bukkit.getOnlinePlayers().forEach(p -> player.playSound(player.getLocation(), Sound.ENTITY_ENDERDRAGON_DEATH, 1.0F, 1.0F));
            }
        }

        if (parkourPlayer.getPlayerState() == PlayerState.PRACTICE || parkourPlayer.getPlayerState() == PlayerState.PLOT) {
            event.setUseInteractedBlock(Event.Result.DENY);

            player.addPotionEffects(blockManager.getPotionEffects(cpLocation));
            if (blockManager.hasTP(cpLocation)) {
                cpLocation = blockManager.getTeleportPos(cpLocation).clone();
                player.teleport(cpLocation);
                player.playSound(player.getPlayer().getLocation(), Sound.ENTITY_ENDERMEN_TELEPORT, 1.0F, 1.0F);
            }
        }

    }
    
    private void tutorialPlateInteract(ParkourPlayer parkourPlayer, Location clickedPlate) {
        if (TD2Core.getEndTut().equals(clickedPlate) && parkourPlayer.isTutorial()) {
            Player player = parkourPlayer.getPlayer();
            parkourPlayer.setTutorial(false);
            configManager.getConfigAccessor(PlayerConfig.class).completeTutorial(player.getUniqueId());
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERDRAGON_DEATH, 1.0F, 1.0F);
            player.sendMessage(Message.TUTORIAL_COMPLETED.getSuccessMessage());
            player.teleport(TD2Core.getSpawn());
        }
    }
    
    private void onTrapDoorClick(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
        if (event.getHand().equals(EquipmentSlot.OFF_HAND)) return;
        if (!(event.getItem() == null || event.getItem().getType().equals(Material.AIR))) return;
        if (event.getClickedBlock().getType().equals(Material.IRON_TRAPDOOR)) {
            BlockState state = event.getClickedBlock().getState();
            TrapDoor trapdoor = (TrapDoor) state.getData();
            trapdoor.setOpen(!trapdoor.isOpen());
            state.update();
        } else if (event.getClickedBlock().getType().equals(Material.IRON_DOOR_BLOCK)) {
            BlockState state = event.getClickedBlock().getState();
            Door door = (Door) state.getData();
            door.setOpen(!door.isOpen());
            state.update();
        }
    }

    private void onWoodTrapdoorClick(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
        if (event.getHand().equals(EquipmentSlot.OFF_HAND)) return;
        if (!(event.getItem() == null || event.getItem().getType().equals(Material.AIR))) return;
        if (event.getClickedBlock().getType().equals(Material.TRAP_DOOR)) {
            event.setCancelled(true);
        }
    }

}
