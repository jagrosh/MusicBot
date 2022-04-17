package com.jagrosh.jmusicbot.commands.jankbot;

import java.io.File;

import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.Button;

public class QuitSibeliusListener extends ListenerAdapter {
    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if(event.getMessage().getContentRaw().equals("quit sibelius") || event.getMessage().getContentRaw().equals("<:quit1:737227012435083274><:quit2:737226986191061013>")) {
            event.getChannel().sendMessage("Quit sibelius?").setActionRow(Button.primary("QUIT_SIBELIUS", "Quit Sibelius")).queue();
        }
    }

    @Override
    public void onButtonClick(ButtonClickEvent event) {
        if (event.getComponentId().contains("QUIT_SIBELIUS")) {
            event.getMessage().editMessage(" ").addFile(new File("/home/callum/MusicBot/images/sib_crashed.png"))
            .setActionRows().queue();
            event.deferEdit().queue();
        }
    }
}
