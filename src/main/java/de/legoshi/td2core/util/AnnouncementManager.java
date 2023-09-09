package de.legoshi.td2core.util;

import de.legoshi.td2core.TD2Core;
import de.legoshi.td2core.config.AnnouncementConfig;
import de.legoshi.td2core.config.ConfigManager;
import de.legoshi.td2core.discord.VerifyManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class AnnouncementManager {
    
    private final VerifyManager verifyManager;
    private final FileConfiguration fileConfiguration;
    private final List<String> initialMessages;
    private final List<String> messagesToAnnounce;
    
    public AnnouncementManager(VerifyManager verifyManager, ConfigManager configManager) {
        this.verifyManager = verifyManager;
        this.fileConfiguration = configManager.getConfig(AnnouncementConfig.class);
        
        initialMessages = new ArrayList<>();
        loadFileConfiguration();
        
        messagesToAnnounce = new ArrayList<>(initialMessages);
    }
    
    public void startAnnouncementScheduler() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(TD2Core.getInstance(), () -> {
            if (messagesToAnnounce.isEmpty()) {
                restoreMessagesToAnnounce();
            }
            int messageIndex = (int) (Math.random() * messagesToAnnounce.size());
            String messageToAnnounce = messagesToAnnounce.remove(messageIndex);
            
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (verifyManager.isVerified(player) && messageToAnnounce.contains("discord")) continue;
                player.sendMessage(messageToAnnounce);
            }
        }, 20L*60*30, 20L*60*30);
    }
    
    private void restoreMessagesToAnnounce() {
        messagesToAnnounce.addAll(initialMessages);
    }
    
    private void loadFileConfiguration() {
        fileConfiguration.getKeys(false).forEach(key -> {
            String message = fileConfiguration.getString(key);
            if (message != null) {
                initialMessages.add(message);
            }
        });
    }
    
}
