package de.legoshi.td2core.discord;

import de.legoshi.td2core.config.ConfigAccessor;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

@RequiredArgsConstructor
public class MessageListener extends ListenerAdapter {
    
    private final List<String> authors;
    private final ConfigAccessor configAccessor;
    private final VerifyManager verifyManager;
    
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getChannelType() != ChannelType.TEXT) return;
    
        String channelID = event.getChannel().getId();
        if (!authors.contains(event.getAuthor().getId())) return;
        
        String message = event.getMessage().getContentRaw();
        FileConfiguration config = configAccessor.getConfig();
        
        if (message.equalsIgnoreCase("td2_completion")) {
            DiscordManager.completionTextChannel = channelID;
            config.set("completiontextchannel", channelID);
        }
        if (message.equalsIgnoreCase("td2_checkpoint")) {
            DiscordManager.checkPointTextChannel = channelID;
            config.set("checkpointtextchannel", channelID);
        }
        if (message.equalsIgnoreCase("td2_staff")) {
            DiscordManager.staffTextChannel = channelID;
            config.set("stafftextchannel", channelID);
        }
        if (message.equalsIgnoreCase("td2_leaderboard")) {
            DiscordManager.staffTextChannel = channelID;
            config.set("leaderboardchannel", channelID);
        }
        if (message.equalsIgnoreCase("td2_verify")) {
            DiscordManager.verifyChannel = channelID;
            config.set("verifychannel", channelID);
        }
        
        configAccessor.saveConfig();
    }
    
}
