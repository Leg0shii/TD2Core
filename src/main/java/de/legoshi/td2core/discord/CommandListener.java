package de.legoshi.td2core.discord;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

@RequiredArgsConstructor
public class CommandListener extends ListenerAdapter {
    
    private final VerifyManager verifyManager;
    
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String channelID = event.getChannel().getId();
        if (!channelID.equals(DiscordManager.verifyChannel)) {
            event.reply("This command can only be used in the verify channel.")
                .setEphemeral(true)
                .queue();
            return;
        }
        if (!event.getName().equals("verify")) return;
    
        OptionMapping optionMapping = event.getOption("verify-id");
        if (optionMapping == null) {
            event.reply("You have to provide the verify ID. Join on TD2.mcpro.io and type /verify to receive the ID.")
                .setEphemeral(true)
                .queue();
            return;
        }
        
        String verifyID = optionMapping.getAsString();
        User author = event.getUser();
        
        String result = verifyManager.checkVerify(author, verifyID);
        event.reply(result)
            .setEphemeral(true)
            .queue();
    }
    
}
