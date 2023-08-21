package de.legoshi.td2core.discord;

import de.legoshi.td2core.TD2Core;
import de.legoshi.td2core.config.ConfigAccessor;
import de.legoshi.td2core.config.DiscordConfig;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.List;

@RequiredArgsConstructor
public class MessageListener extends ListenerAdapter {
    
    private final List<String> authors;
    
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getChannelType() != ChannelType.TEXT) return;
        if (!authors.contains(event.getAuthor().getId())) return;
        
        String message = event.getMessage().getContentRaw();
        String channelID = event.getChannel().getId();
        ConfigAccessor configAccessor = TD2Core.getInstance().config.get(DiscordConfig.fileName);
        
        if (message.equalsIgnoreCase("td2_completion")) {
            DiscordManager.completionTextChannel = channelID;
            configAccessor.getConfig().set("completiontextchannel", channelID);
        }
        if (message.equalsIgnoreCase("td2_checkpoint")) {
            DiscordManager.checkPointTextChannel = channelID;
            configAccessor.getConfig().set("checkpointtextchannel", channelID);
        }
        if (message.equalsIgnoreCase("td2_staff")) {
            DiscordManager.staffTextChannel = channelID;
            configAccessor.getConfig().set("stafftextchannel", channelID);
        }
        if (message.equalsIgnoreCase("td2_leaderboard")) {
            DiscordManager.staffTextChannel = channelID;
            configAccessor.getConfig().set("leaderboardchannel", channelID);
        }
        configAccessor.saveConfig();
    }
    
}
