package com.jagrosh.jmusicbot.commands.jankbot;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.ItemComponent;

public class JankedexButtonListener extends ListenerAdapter {
    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (event.getComponentId().contains("JDX")) {
            List<String> files = new ArrayList<String>();
            for (final File fileEntry : new File("/home/calluml/MusicBot/Jankedex").listFiles()) {
                if (!fileEntry.isDirectory()) {
                    files.add(fileEntry.getName());
                }
            }
            String curr_idx = event.getComponentId().split(":")[1];
            int pos = files.indexOf(curr_idx + ".png");
            if (event.getComponentId().contains("JDX_NEXT")) {
                List<ItemComponent> comps = new ArrayList<ItemComponent>();
                comps.add(Button.secondary("JDX_PREV:" + files.get(pos - 1).split("\\.")[0], "⬅️"));
                if (pos + 1 != files.size() - 1)
                    comps.add(Button.secondary("JDX_NEXT:" + files.get(pos + 1).split("\\.")[0], "➡️"));
                event.getMessage().editMessage("JANKEDEX ENTRY " + files.get(pos + 1).split("\\.")[0] + ":")
                        .addFile(new File("/home/calluml/MusicBot/Jankedex/" + files.get(pos + 1)))
                        .override(true)
                        .setActionRow(comps) // update button id
                        .queue();
                event.deferEdit().queue();
            } else if (event.getComponentId().contains("JDX_PREV")) {
                List<ItemComponent> comps = new ArrayList<ItemComponent>();
                if (pos - 1 != 0)
                    comps.add(Button.secondary("JDX_PREV:" + files.get(pos - 1).split("\\.")[0], "⬅️"));
                comps.add(Button.secondary("JDX_NEXT:" + files.get(pos + 1).split("\\.")[0], "➡️"));
                event.getMessage().editMessage("JANKEDEX ENTRY " + files.get(pos - 1).split("\\.")[0] + ":")
                        .addFile(new File("/home/calluml/MusicBot/Jankedex/" + files.get(pos - 1)))
                        .override(true)
                        .setActionRow(comps) // update button id
                        .queue();
                event.deferEdit().queue();
            }
        }
    }

}
