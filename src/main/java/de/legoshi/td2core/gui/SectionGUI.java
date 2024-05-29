package de.legoshi.td2core.gui;

import de.legoshi.td2core.TD2Core;
import de.legoshi.td2core.config.ConfigManager;
import de.legoshi.td2core.map.MapManager;
import de.legoshi.td2core.map.session.SessionManager;
import de.legoshi.td2core.player.PlayerManager;
import de.legoshi.td2core.util.CustomHeads;
import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import org.bukkit.entity.Player;

public class SectionGUI extends GUIPane {
    
    private final MapManager mapManager;
    private final PlayerManager playerManager;
    private final SessionManager sessionManager;
    private final ConfigManager configManager;
    private final String[] guiSetup = {
        "abcdefghi",
        "zzzkzjzzz",
    };
    
    public SectionGUI(MapManager mapManager, PlayerManager playerManager, SessionManager sessionManager, ConfigManager configManager) {
        this.mapManager = mapManager;
        this.playerManager = playerManager;
        this.sessionManager = sessionManager;
        this.configManager = configManager;
    }
    
    public void openGui(Player player, InventoryGui parent) {
        super.openGui(player, parent);
        this.current = new InventoryGui(TD2Core.getInstance(), player, "Section Selection", guiSetup);
        fullCloseOnEsc();
        registerGuiElements();
        this.current.show(this.holder);
    }
    
    @Override
    protected void registerGuiElements() {
        StaticGuiElement section1 = new StaticGuiElement('a', CustomHeads.section1, click -> openMap(1), "§7§lSection 1 §8§l- §b§lSky");
        StaticGuiElement section2 = new StaticGuiElement('b', CustomHeads.section2, click -> openMap(2), "§7§lSection 2 §8§l- §8§lRankup");
        StaticGuiElement section3 = new StaticGuiElement('c', CustomHeads.section3, click -> openMap(3), "§7§lSection 3 §8§l- §e§lHal§b§llw§8§lay");
        StaticGuiElement section4 = new StaticGuiElement('d', CustomHeads.section4,  click -> openMap(4), "§7§lSection 4 §8§l- §c§lSpeed§4§lrun");
        StaticGuiElement section5 = new StaticGuiElement('e', CustomHeads.section5,  click -> openMap(5), "§7§lSection 5 §8§l- §d§lOne§5§ljump");
        StaticGuiElement section6 = new StaticGuiElement('f', CustomHeads.section6, click -> openMap(6), "§7§lSection 6 §8§l- §c§lRed§4§lstone");
        StaticGuiElement section7 = new StaticGuiElement('g', CustomHeads.section7, click -> openMap(7), "§7§lSection 7 §8§l- §b§lSpe§3§led");
        StaticGuiElement section8 = new StaticGuiElement('h', CustomHeads.section8, click -> openMap(8), "§7§lSection 8 §8§l- §e§lJump§6§lboost");
        StaticGuiElement section9 = new StaticGuiElement('i', CustomHeads.section9, click -> openMap(9), "§7§lSection 9 §8§l- §7§lXXL");
        StaticGuiElement section10 = new StaticGuiElement('j', CustomHeads.section10, click -> openMap(10), "§7§lSection 10 §8§l- §a§lSli§2§lme");
        StaticGuiElement lastJump = new StaticGuiElement('k', CustomHeads.section11, click -> openMap(11), "§7§lFinal §8§lJump");
        this.current.addElements(this.returnToParent, section1, section2, section3, section4, section5, section6, section7, section8, section9, section10, lastJump);
    }
    
    protected boolean openMap(int id) {
        new MapGUI(mapManager, playerManager, sessionManager, configManager).openGui(holder.getPlayer(), this.current, id);
        return true;
    }
    
}
