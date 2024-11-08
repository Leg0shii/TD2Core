package de.legoshi.td2core.gui;

import de.legoshi.td2core.TD2Core;
import de.legoshi.td2core.config.ConfigManager;
import de.legoshi.td2core.map.ParkourMap;
import de.legoshi.td2core.map.MapManager;
import de.legoshi.td2core.map.session.SessionManager;
import de.legoshi.td2core.player.ParkourPlayer;
import de.legoshi.td2core.map.session.ParkourSession;
import de.legoshi.td2core.player.PlayerManager;
import de.legoshi.td2core.player.PlayerState;
import de.legoshi.td2core.util.CustomHeads;
import de.legoshi.td2core.util.Message;
import de.legoshi.td2core.util.Utils;
import de.themoep.inventorygui.*;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class MapGUI extends GUIPane {
    
    private final MapManager mapManager;
    private final PlayerManager playerManager;
    private final SessionManager sessionManager;
    private final ConfigManager configManager;
    
    private final String[] guiSetup = {
        "ggggggggg",
        "gaaaaaaag",
        "ggggggggq"
    };
    
    private int id;
    
    public MapGUI(MapManager mapManager, PlayerManager playerManager, SessionManager sessionManager, ConfigManager configManager) {
        this.mapManager = mapManager;
        this.playerManager = playerManager;
        this.sessionManager = sessionManager;
        this.configManager = configManager;
    }
    
    public void openGui(Player player, InventoryGui parent, int id) {
        super.openGui(player, parent);
        this.id = id;
        this.current = new InventoryGui(TD2Core.getInstance(), player, "Map Selection", guiSetup);
        registerGuiElements();
        fullCloseOnEsc();
        this.current.show(this.holder);
        this.current.addElement(this.returnToParent);
    }
    
    @Override
    protected void registerGuiElements() {
        List<ParkourMap> parkourMaps = mapManager.getAll("section" + id);
        GuiElementGroup group = new GuiElementGroup('a');
    
        List<CompletableFuture<DynamicGuiElement>> futures = new ArrayList<>();
    
        for (ParkourMap parkourMap : parkourMaps) {
            ParkourPlayer parkourPlayer = playerManager.get(holder.getPlayer());
        
            CompletableFuture<DynamicGuiElement> future = mapManager.hasPassed(parkourPlayer.getPlayer(), parkourMap).thenApply(passed -> {
                ItemStack item = CustomHeads.create(parkourMap.getHead());
                return retrieveItem(item, parkourPlayer, parkourMap, passed);
            });
            
            futures.add(future);
        }
        
        this.current = new InventoryGui(TD2Core.getInstance(), current.getOwner(), "Map Selection", getGuiSetup(futures.size()));
        
        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allOf.thenRun(() -> {
            futures.forEach(future -> {
                try {
                    DynamicGuiElement element = future.get();
                    group.addElement(element);
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            });
            this.current.draw();
        });
    
        this.current.addElement(group);
    }
    
    private DynamicGuiElement retrieveItem(ItemStack item, ParkourPlayer player, ParkourMap map, boolean passed) {
        playerManager.loadIndividualStats(player.getPlayer(), map).join();
        return new DynamicGuiElement(
            'a',
            () -> new StaticGuiElement(
                'a',
                item,
                click -> {
                    if (click.getType().isRightClick()) {
                        new MapLBGUI(configManager).openGui(player.getPlayer(), this.current, map);
                        return true;
                    }
                    if (map.mapName.equals("Final Jump")) {
                        if (player.getPercentage() < 99) {
                            player.getPlayer().sendMessage(Message.NOT_WORTHY.getMessage());
                            this.current.close();
                            return true;
                        }
                    }
                    if (map.mapName.equals("LD TD1")) {
                        player.getPlayer().sendMessage(Message.COMING_SOON.getMessage());
                        return true;
                    }
                    if (player.getPlayerState() == PlayerState.STAFF) {
                        player.getPlayer().sendMessage(Message.NOT_STAFF_MODE.getMessage());
                        return true;
                    }
                    player.mapJoin(map);
                    player.getPlayer().playSound(player.getPlayer().getLocation(), Sound.BLOCK_PORTAL_TRAVEL, 1.0F, 1.0F);
                    this.current.close();
                    return true;
                },
                getMapString(map, sessionManager.get(player.getPlayer(), map), passed)
            )
        );
    }
    
    private String[] getMapString(ParkourMap map, ParkourSession session, boolean passed) {
        String passedString = passed ? "§2§l✔" : "§c§l✘";
        return new String[]{
            map.getDisplayName(),
            " ",
            "   §eBuilt in: §7" + map.getBuildTime(),
            " ",
            "§6§l----Global Stats----",
            "§6➤ §eDifficulty: §7" + Utils.difficultyString(map.getEstimatedDifficulty()),
            "§6➤ §eTotal CPs: §7" + map.getCpCount(),
            "§6➤ §eGain: §7" + (double) map.getWeight() + " %",
            "§6➤ §eFastest Player: §7" + map.getFastestPlayer(),
            "§6➤ §eFastest Time: §7" + Utils.secondsToTime((int) (map.getFastestTime() / 1000)),
            "§6➤ §eClear Rate: §7" + ((int) (10.0 * map.getClearRate() * 100.0))/10 + " %",
            "§6➤ §eTotal Plays: §7" + map.getTotalPlays(),
            " ",
            "§6§l-----Your Stats-----",
            "§6➤ §eTotal Fails: §7" + session.getTotalFails(),
            "§6➤ §eTotal Playtime: §7" + Utils.secondsToTime((int) (session.getTotalPlayTime() / 1000)),
            "§6➤ §ePassed: §7" + passedString,
            " ",
            "§8          (Left Click to Join)",
            "§8 (Right Click to show Leaderboard)"
        };
    }
    
    private String[] getGuiSetup(int num) {
        switch (num) {
            case 1:
                return new String[]{
                    "ggggggggg",
                    "ggggagggg",
                    "ggggggggq"
                };
            case 2:
                return new String[]{
                    "ggggggggg",
                    "ggagggagg",
                    "ggggggggq"
                };
            case 3:
                return new String[]{
                        "ggggggggg",
                        "ggagagagg",
                        "ggggggggq"
                };
            case 4:
                return new String[]{
                    "ggggggggg",
                    "gagagagag",
                    "ggggggggq"
                };
            case 5:
                return new String[]{
                    "ggggggggg",
                    "gaagagaag",
                    "ggggggggq"
                };
            default:
                return guiSetup;
        }
    }
    
}
