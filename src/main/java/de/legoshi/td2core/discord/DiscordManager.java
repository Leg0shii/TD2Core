package de.legoshi.td2core.discord;

import de.legoshi.td2core.TD2Core;
import de.legoshi.td2core.cache.GlobalLBStats;
import de.legoshi.td2core.cache.MapLBStats;
import de.legoshi.td2core.config.ConfigAccessor;
import de.legoshi.td2core.config.ConfigManager;
import de.legoshi.td2core.config.DiscordConfig;
import de.legoshi.td2core.discord.progress.ProgressManager;
import de.legoshi.td2core.gui.GlobalLBGUI;
import de.legoshi.td2core.map.ParkourMap;
import de.legoshi.td2core.map.session.ParkourSession;
import de.legoshi.td2core.map.session.SessionManager;
import de.legoshi.td2core.player.PlayerManager;
import de.legoshi.td2core.util.Message;
import de.legoshi.td2core.util.Utils;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.*;

public class DiscordManager {
    
    protected static String completionTextChannel;
    protected static String checkPointTextChannel;
    protected static String staffTextChannel;
    protected static String leaderboardChannel;
    protected static String verifyChannel;
    private static String token;
    
    private final JDA jda;
    private final List<String> authors = new ArrayList<>();
    private final PlayerManager playerManager;
    private final SessionManager sessionManager;
    private final ConfigManager configManager;
    private final VerifyManager verifyManager;
    @Getter private final ProgressManager progressManager;
    @Getter private final RoleManager roleManager;
    private final ConfigAccessor configAccessor;
    private final int page = 0;
    private final int pageVolume = 15;
    
    public DiscordManager(ConfigManager configManager, PlayerManager playerManager, SessionManager sessionManager, VerifyManager verifyManager) {
        this.configManager = configManager;
        this.configAccessor = configManager.getConfigAccessor(DiscordConfig.class);
        this.playerManager = playerManager;
        this.sessionManager = sessionManager;
        this.verifyManager = verifyManager;
        loadConfig();
        
        JDABuilder builder = JDABuilder.createDefault(token);
        builder.setChunkingFilter(ChunkingFilter.ALL);
        builder.disableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE);
        builder.setBulkDeleteSplittingEnabled(false);
        builder.enableIntents(GatewayIntent.MESSAGE_CONTENT);
        builder.enableIntents(GatewayIntent.GUILD_MEMBERS);
        builder.enableIntents(GatewayIntent.GUILD_PRESENCES);
        builder.setActivity(Activity.playing("TD2.mcpro.io"));
        jda = builder.build();
        
        this.progressManager = new ProgressManager(configAccessor, jda);
        this.roleManager = new RoleManager(progressManager);
        this.verifyManager.setRoleManager(roleManager);
        
        registerListener();
    }
    
    public void sendCheckPointMessage(Player player) {
        ParkourMap currentParkourMap = playerManager.get(player).getCurrentParkourMap();
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
        ParkourMap currentParkourMap = playerManager.get(player).getCurrentParkourMap();
        ParkourSession session = sessionManager.get(player, currentParkourMap);
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
    
        ParkourMap currentParkourMap = playerManager.get(player).getCurrentParkourMap();
        ParkourSession session = sessionManager.get(player, currentParkourMap);
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
            HashMap<UUID, GlobalLBStats> sortedMap = new GlobalLBGUI(configManager).sortMap(map, page, pageVolume);
            int count = 1;
            for (UUID a : sortedMap.keySet()) {
                GlobalLBStats b = sortedMap.get(a);
                String playerName = Bukkit.getOfflinePlayer(a).getName();
                String completionPercent = ((int) (10*b.getPercentage()))/10.0 + " %";
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
            progressManager.updateMessage(Collections.singletonList(message), leaderboardChannel);
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
            case VERIFY:
                channelID = verifyChannel;
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
        this.jda.addEventListener(new MessageListener(authors, configAccessor, verifyManager));
        this.jda.addEventListener(new ReadyListener(this));
        this.jda.addEventListener(new CommandListener(verifyManager));
    }
    
    private void loadConfig() {
        FileConfiguration config = configAccessor.getConfig();
        if (config.contains("token")) token = config.getString("token");
        if (config.contains("completiontextchannel")) completionTextChannel = config.getString("completiontextchannel");
        if (config.contains("checkpointtextchannel")) checkPointTextChannel = config.getString("checkpointtextchannel");
        if (config.contains("stafftextchannel")) staffTextChannel = config.getString("stafftextchannel");
        if (config.contains("stafftextchannel")) leaderboardChannel = config.getString("leaderboardchannel");
        if (config.contains("authors")) authors.addAll(config.getConfigurationSection("authors").getKeys(false));
        if (config.contains("verifychannel")) verifyChannel = config.getString("verifychannel");
    }
    
    public void startLeaderboardScheduler() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(TD2Core.getInstance(), this::updateLeaderboard, 20L, 20L * 60 * 10);
    }
    
    public void startProgressScheduler() {
        progressManager.startProgressScheduler();
    }
    
    public void registerCommands(Guild guild) {
        guild.updateCommands().addCommands(
            Commands.slash("verify", "Confirm the verification.")
                .addOption(OptionType.STRING, "verify-id", "The verification ID.", true)
        ).queue();
    }
    
}
