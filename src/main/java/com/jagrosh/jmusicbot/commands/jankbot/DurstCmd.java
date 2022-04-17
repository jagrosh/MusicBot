package com.jagrosh.jmusicbot.commands.jankbot;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jmusicbot.Bot;

import net.dv8tion.jda.api.interactions.components.buttons.Button;


public class DurstCmd extends Command {
    //private Bot bot; 

    public DurstCmd(Bot bot) {
        //this.bot = bot;
        this.name = "durst";
        this.help = "Get a random Fred Durst quote.";
        this.aliases = new String[]{"durstdex"};
        this.guildOnly = true;
    }

    public void execute(CommandEvent event) {
        if(event.getArgs().length() == 0){
            List<String> files = new ArrayList<String>();
            for (final File fileEntry : new File("/home/calluml/MusicBot/durst").listFiles()) {
                if (!fileEntry.isDirectory()) {
                    files.add(fileEntry.getName());
                }
            }

            String file_to_ret = "";
            int pos;
            Random rv = new Random();
            pos = rv.nextInt(files.size());
            file_to_ret = files.get(pos);

            event.getEvent().getMessage().reply(" ").addFile(new File("/home/calluml/MusicBot/durst/" + file_to_ret)).setActionRow(Button.primary("DURST" + String.valueOf(pos), "Break Stuff")).queue();
        } else if (event.getArgs().equals("add")) {

        }
    }

}
