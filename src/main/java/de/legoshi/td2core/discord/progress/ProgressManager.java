package de.legoshi.td2core.discord.progress;

import de.legoshi.td2core.TD2Core;
import de.legoshi.td2core.config.ConfigAccessor;
import de.legoshi.td2core.discord.progress.overall.OverallChannel;
import de.legoshi.td2core.discord.progress.overall.OverallProgress;
import de.legoshi.td2core.discord.progress.section1.BlackStretch;
import de.legoshi.td2core.discord.progress.section1.GreenStretch;
import de.legoshi.td2core.discord.progress.section1.InitialTD2;
import de.legoshi.td2core.discord.progress.section1.InitialTD2Rework;
import de.legoshi.td2core.discord.progress.section10.Slime;
import de.legoshi.td2core.discord.progress.section2.Cubes;
import de.legoshi.td2core.discord.progress.section3.Cave;
import de.legoshi.td2core.discord.progress.section3.FinalStretch;
import de.legoshi.td2core.discord.progress.section3.Lava;
import de.legoshi.td2core.discord.progress.section3.Overworld;
import de.legoshi.td2core.discord.progress.section4.SRNoSprint;
import de.legoshi.td2core.discord.progress.section4.SRPolish;
import de.legoshi.td2core.discord.progress.section4.SRSpeed;
import de.legoshi.td2core.discord.progress.section4.SRStandard;
import de.legoshi.td2core.discord.progress.section5.OneJump;
import de.legoshi.td2core.discord.progress.section6.*;
import de.legoshi.td2core.discord.progress.section7.*;
import de.legoshi.td2core.discord.progress.section8.*;
import de.legoshi.td2core.discord.progress.section9.TD1Buffed;
import de.legoshi.td2core.discord.progress.section9.TD1LD;
import de.legoshi.td2core.discord.progress.section9.TD1XXL;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;

public class ProgressManager {

    private final FileConfiguration config;
    private final JDA jda;
    private final List<ProgressChannel> progressChannels = new ArrayList<>();
    
    public ProgressManager(ConfigAccessor configAccessor, JDA jda) {
        this.config = configAccessor.getConfig();
        this.jda = jda;
    }
    
    public void loadProgressChannels(Guild guild) {
        OverallChannel section0 = new OverallChannel(config.getString("section0"), guild);
        section0.getProgressMaps().add(new OverallProgress(section0));
        
        ProgressChannel section1 = new ProgressChannel(config.getString("section1"), guild);
        section1.getProgressMaps().add(new InitialTD2());
        section1.getProgressMaps().add(new GreenStretch());
        section1.getProgressMaps().add(new BlackStretch());
        section1.getProgressMaps().add(new InitialTD2Rework());
        
        ProgressChannel section2 = new ProgressChannel(config.getString("section2"), guild);
        section2.getProgressMaps().add(new Cubes());
        
        ProgressChannel section3 = new ProgressChannel(config.getString("section3"), guild);
        section3.getProgressMaps().add(new Overworld(section3));
        section3.getProgressMaps().add(new Cave(section3));
        section3.getProgressMaps().add(new Lava(section3));
        section3.getProgressMaps().add(new FinalStretch());

        ProgressChannel section4 = new ProgressChannel(config.getString("section4"), guild);
        section4.getProgressMaps().add(new SRStandard());
        section4.getProgressMaps().add(new SRPolish());
        section4.getProgressMaps().add(new SRSpeed());
        section4.getProgressMaps().add(new SRNoSprint());
        
        ProgressChannel section5 = new ProgressChannel(config.getString("section5"), guild);
        section5.getProgressMaps().add(new OneJump(section5));
        
        ProgressChannel section6 = new ProgressChannel(config.getString("section6"), guild);
        section6.getProgressMaps().add(new Library());
        section6.getProgressMaps().add(new City());
        section6.getProgressMaps().add(new Arcade());
        section6.getProgressMaps().add(new Castle());
        section6.getProgressMaps().add(new Space());
        section6.getProgressMaps().add(new RedstoneHallway());
        section6.getProgressMaps().add(new RedstoneSky());
        
        ProgressChannel section7 = new ProgressChannel(config.getString("section7"), guild);
        section7.getProgressMaps().add(new Speed0());
        section7.getProgressMaps().add(new Speed1());
        section7.getProgressMaps().add(new Speed2());
        section7.getProgressMaps().add(new Speed3());
        section7.getProgressMaps().add(new Speed4());
        section7.getProgressMaps().add(new Speed5());

        ProgressChannel section8 = new ProgressChannel(config.getString("section8"), guild);
        section8.getProgressMaps().add(new Jump1());
        section8.getProgressMaps().add(new Jump2());
        section8.getProgressMaps().add(new Jump3());
        section8.getProgressMaps().add(new Jump4());
        section8.getProgressMaps().add(new Jump5());
        
        ProgressChannel section9 = new ProgressChannel(config.getString("section9"), guild);
        section9.getProgressMaps().add(new TD1Buffed());
        section9.getProgressMaps().add(new TD1LD());
        section9.getProgressMaps().add(new TD1XXL(section9));
        
        ProgressChannel section10 = new ProgressChannel(config.getString("section10"), guild);
        section10.getProgressMaps().add(new Slime(section10));
        
        progressChannels.addAll(Arrays.asList(section0, section1, section2, section3, section4, section5, section6,
                section7, section8, section9, section10));
    }
    
    public void startProgressScheduler() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(TD2Core.getInstance(), () -> {
            int waitingIncrement = 0;
            for (ProgressChannel channel : progressChannels) {
                List<String> progressStrings = channel.getFullProgress();
                
                waitingIncrement = waitingIncrement + 120 * progressStrings.size();
                Bukkit.getScheduler().runTaskLaterAsynchronously(TD2Core.getInstance(), () -> {
                    updateMessage(progressStrings, channel.getChannelID());
                }, waitingIncrement);
            }
        }, 20L * 15L, 20L * 60L * 10L);
    }

    public void updateMessage(List<String> messageList, String channelID) {
        TextChannel textChannel = jda.getTextChannelById(channelID);
        if (textChannel == null) return;

        int volume = messageList.size();
        List<net.dv8tion.jda.api.entities.Message> messages = textChannel.getHistory().retrievePast(volume).complete();
        Collections.reverse(messages);

        for (int i = 0; i < volume; i++) {
            int finalI = i;
            Bukkit.getScheduler().runTaskLaterAsynchronously(TD2Core.getInstance(), () -> {
                try {
                    if (messages.size() > finalI) {
                        net.dv8tion.jda.api.entities.Message m = messages.get(finalI);
                        if (!m.getContentRaw().equals(messageList.get(finalI))) {
                            m.editMessage(messageList.get(finalI)).queue();
                        }
                    } else {
                        textChannel.sendMessage(messageList.get(finalI)).queue();
                    }
                } catch (Exception e) {
                    Bukkit.getLogger().info("Couldn't update discord progress data");
                    e.printStackTrace();
                }
            }, 120L * finalI);
        }
    }
    
    public List<ProgressMap> getAllProgressMaps() {
        List<ProgressMap> maps = new ArrayList<>();
        for (ProgressChannel channel : progressChannels) {
            maps.addAll(channel.getProgressMaps());
        }
        return maps;
    }
    
}
