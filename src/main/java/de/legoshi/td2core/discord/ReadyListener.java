package de.legoshi.td2core.discord;

import com.sun.corba.se.impl.resolver.SplitLocalResolverImpl;
import de.legoshi.td2core.discord.progress.ProgressManager;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Timer;
import java.util.TimerTask;

@RequiredArgsConstructor
public class ReadyListener extends ListenerAdapter {
    
    private final DiscordManager discordManager;
    
    @Override
    public void onReady(ReadyEvent event) {
        JDA jda = event.getJDA();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    jda.awaitReady();
                    Guild guild = jda.getGuildById("1140558503635845151");
                    if (guild != null) {
                        discordManager.getProgressManager().loadProgressChannels(guild);
                        discordManager.getRoleManager().setGuild(guild);
                        discordManager.registerCommands(guild);
                    } else {
                        System.err.println("Guild not found.");
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }, 5000);
    }

}
