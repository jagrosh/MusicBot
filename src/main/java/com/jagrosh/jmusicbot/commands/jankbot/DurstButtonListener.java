package com.jagrosh.jmusicbot.commands.jankbot;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.Button;

public class DurstButtonListener extends ListenerAdapter {
    @Override
    public void onButtonClick(ButtonClickEvent event) {
        if (event.getComponentId().contains("DURST")) {
            List<String> files = new ArrayList<String>();
            for (final File fileEntry : new File("/home/callum/MusicBot/durst").listFiles()) {
                if (!fileEntry.isDirectory()) {
                    files.add(fileEntry.getName());
                }
            }

            String file_to_ret = "";
            int pos;
            Random rv = new Random();
            pos = rv.nextInt(files.size());
            file_to_ret = files.get(pos);

            event.getMessage().editMessage(" ").addFile(new File("/home/callum/MusicBot/durst/" + file_to_ret))
                    .setActionRow(Button.primary("DURST" + String.valueOf(pos), "Break Stuff"))
                    .override(true).queue();
            event.deferEdit().queue();
        }
    }
}
