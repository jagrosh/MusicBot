package com.jagrosh.jmusicbot.commands.jankbot;

import java.io.File;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class QuitSibeliusListener extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if(event.getMessage().getContentRaw().equals("quit sibelius") || event.getMessage().getContentRaw().equals("<:quit1:737227012435083274><:quit2:737226986191061013>")
           || event.getMessage().getContentRaw().equals("<:quit1:737227012435083274> <:quit2:737226986191061013>")) {
            event.getChannel().sendMessage("Quit sibelius?").setActionRow(Button.primary("QUIT_SIBELIUS", "Quit Sibelius")).queue();
        }
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (event.getComponentId().contains("QUIT_SIBELIUS")) {
            event.getMessage().editMessage(" ").addFile(new File("/home/calluml/MusicBot/images/sib_crashed.png"))
            .setActionRows().queue();
            event.deferEdit().queue();
        }
    }
}
