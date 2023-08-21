package de.legoshi.td2core.discord;

import de.legoshi.td2core.TD2Core;
import de.legoshi.td2core.cache.GlobalLBCache;
import de.legoshi.td2core.cache.GlobalLBStats;
import de.legoshi.td2core.cache.MapLBStats;
import de.legoshi.td2core.config.ConfigAccessor;
import de.legoshi.td2core.config.DiscordConfig;
import de.legoshi.td2core.gui.GlobalLBGUI;
import de.legoshi.td2core.map.MapManager;
import de.legoshi.td2core.map.ParkourMap;
import de.legoshi.td2core.map.session.ParkourSession;
import de.legoshi.td2core.map.session.SessionManager;
import de.legoshi.td2core.player.PlayerManager;
import de.legoshi.td2core.util.Message;
import de.legoshi.td2core.util.Utils;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class DiscordManager {
    
    protected static String completionTextChannel;
    protected static String checkPointTextChannel;
    protected static String staffTextChannel;
    protected static String leaderboardChannel;
    private static String token;
    
    private final JDA jda;
    private final List<String> authors = new ArrayList<>();
    private int page = 0;
    private int pageVolume = 15;
    
    public DiscordManager() {
        loadConfig();
        
        JDABuilder builder = JDABuilder.createDefault(token);
        builder.disableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE);
        builder.setBulkDeleteSplittingEnabled(false);
        builder.enableIntents(GatewayIntent.MESSAGE_CONTENT);
        builder.setActivity(Activity.playing("TD2.mcpro.io"));
        jda = builder.build();
        
        registerListener();
        startScheduler();
    }
    
    public void sendCheckPointMessage(Player player) {
        ParkourMap currentParkourMap = PlayerManager.get(player).getCurrentParkourMap();
        MapLBStats mapLBStats = TD2Core.getInstance().mapLBCache.getCache().get(currentParkourMap).get(player.getUniqueId());
        String checkpointActivationMessage = Message.CHECKPOINT_ACTIVATED.getMessage(
            player.getName(),
            String.valueOf(mapLBStats.getCurrentCPCount()),
            currentParkourMap.mapName
        );
        sendMessage(checkpointActivationMessage, ChannelType.CHECKPOINT);
        sendStaffMessage(player);
    }
    
    public void sendCompletionMessage(Player player) {
        ParkourMap currentParkourMap = PlayerManager.get(player).getCurrentParkourMap();
        ParkourSession session = SessionManager.get(player, currentParkourMap);
        String completionActivationMessage = Message.COMPLETION_ACTIVATED.getMessage(
            player.getName(),
            currentParkourMap.mapName,
            Utils.secondsToTime((int) session.getPlayTime()/1000),
            String.valueOf(session.getFails())
        );
        sendMessage(completionActivationMessage, ChannelType.COMPLETION);
        sendStaffMessage(player);
    }
    
    private void sendStaffMessage(Player player) {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = sdf.format(date);
    
        ParkourMap currentParkourMap = PlayerManager.get(player).getCurrentParkourMap();
        ParkourSession session = SessionManager.get(player, currentParkourMap);
        Location sessionLocation = session.getLastCheckpointLocation();
        if (sessionLocation == null) sessionLocation = player.getLocation();
    
        String logMessage = Message.LOG_ACTIVATED.getMessage(
            formattedDate,
            player.getName(),
            sessionLocation.getX()+"", sessionLocation.getY()+"", sessionLocation.getZ()+"",
            currentParkourMap.mapName,
            String.valueOf(session.getFails())
        );
        
        sendMessage(logMessage, ChannelType.STAFF);
    }
    
    private void updateLeaderboard() {
        TD2Core.getInstance().globalLBCache.getLeaderboardData().thenApply(map -> {
            String leaderboardMessage = "🏆 Completion **LEADERBOARD** (updates every 10 mins)\n\n";
            HashMap<UUID, GlobalLBStats> sortedMap = new GlobalLBGUI().sortMap(map, page, pageVolume);
            int count = 1;
            for (UUID a : sortedMap.keySet()) {
                GlobalLBStats b = sortedMap.get(a);
                String playerName = Bukkit.getOfflinePlayer(a).getName();
                String completionPercent = b.getPercentage() + " %";
                String countString = count + ".";
                
                if (count == 1) {
                    countString = "🥇";
                    playerName = "**" + playerName + "**";
                } else if (count == 2) {
                    countString = "🥈";
                    playerName = "**" + playerName + "**";
                } else if (count == 3) {
                    countString = "🥉";
                    playerName = "**" + playerName + "**";
                }
                
                String spaces = Utils.repeatSpace((16 - playerName.length())*1.5);
                leaderboardMessage += Message.LEADERBOARD_ENTRY.getMessage(
                    countString,
                    playerName + spaces,
                    completionPercent
                ).replace("_", "\\_");
                count++;
            }
            return leaderboardMessage;
        }).thenApply(message -> {
            TextChannel textChannel = jda.getTextChannelById(leaderboardChannel);
            if (textChannel == null) return null;
            List<net.dv8tion.jda.api.entities.Message> messages = textChannel.getHistory().retrievePast(1).complete();
    
            if (!messages.isEmpty()) {
                User author = messages.get(0).getAuthor();
                if (jda.getSelfUser().equals(author)) {
                    textChannel.editMessageById(textChannel.getLatestMessageId(), message).queue();
                } else {
                    textChannel.sendMessage(message).queue();
                }
            }
            
            return null;
        });
    }
    
    private void sendMessage(String message, ChannelType channelType) {
        String channelID;
        switch (channelType) {
            case COMPLETION:
                channelID = completionTextChannel;
                break;
            case CHECKPOINT:
                channelID = checkPointTextChannel;
                break;
            case STAFF:
                channelID = staffTextChannel;
                break;
            case LEADERBOARD:
                channelID = leaderboardChannel;
                break;
            default:
                return;
        }
        TextChannel textChannel = jda.getTextChannelById(channelID);
        if (textChannel != null) {
            textChannel.sendMessage(message).queue();
        }
    }
    
    private void registerListener() {
        this.jda.addEventListener(new MessageListener(authors));
    }
    
    private void loadConfig() {
        ConfigAccessor configAccessor = TD2Core.getInstance().config.get(DiscordConfig.fileName);
        if (configAccessor.getConfig().contains("token")) {
            token = configAccessor.getConfig().getString("token");
        }
        if (configAccessor.getConfig().contains("completiontextchannel")) {
            completionTextChannel = configAccessor.getConfig().getString("completiontextchannel");
        }
        if (configAccessor.getConfig().contains("checkpointtextchannel")) {
            checkPointTextChannel = configAccessor.getConfig().getString("checkpointtextchannel");
        }
        if (configAccessor.getConfig().contains("stafftextchannel")) {
            staffTextChannel = configAccessor.getConfig().getString("stafftextchannel");
        }
        if (configAccessor.getConfig().contains("stafftextchannel")) {
            leaderboardChannel = configAccessor.getConfig().getString("leaderboardchannel");
        }
        if (configAccessor.getConfig().contains("authors")) {
            authors.addAll(configAccessor.getConfig().getConfigurationSection("authors").getKeys(false));
        }
    }
    
    private void startScheduler() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(TD2Core.getInstance(), this::updateLeaderboard, 20L, 20L * 60 * 10);
    }
    
}
