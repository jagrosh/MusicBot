package com.jagrosh.jmusicbot.commands.jankbot;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jmusicbot.Bot;

import net.dv8tion.jda.api.interactions.components.Button;


public class LogoCmd extends Command {
    // private Bot bot; 

    public LogoCmd(Bot bot) {
        // this.bot = bot;
        this.name = "logo";
        this.help = "Get a random logo.";
        this.guildOnly = true;
    }

    public void execute(CommandEvent event) {
        List<String> files = new ArrayList<String>();
        for (final File fileEntry : new File("/home/callum/MusicBot/muselogos").listFiles()) {
            if (!fileEntry.isDirectory()) {
                files.add(fileEntry.getName());
            }
        }

        String file_to_ret = "";
        int pos;
        Random rv = new Random();
        pos = rv.nextInt(files.size());
        file_to_ret = files.get(pos);

        event.getEvent().getMessage().reply(" ").addFile(new File("/home/callum/MusicBot/muselogos/" + file_to_ret)).setActionRow(Button.primary("LOGO_NEW:" + String.valueOf(pos), "New All")).queue();
    }

}
