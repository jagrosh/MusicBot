package com.jagrosh.jmusicbot.commands.jankbot;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jmusicbot.Bot;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.Component;

public class JankedexCmd extends Command {
    private Bot bot; 

    public JankedexCmd(Bot bot) {
        this.bot = bot;
        this.name = "jankedex";
        this.help = "View the jankedex. Supply a number for the number, else get a random one.";
        this.aliases = new String[] { "jankdex" };
        this.guildOnly = true;
    }

    public void execute(CommandEvent event) {
        List<String> files = new ArrayList<String>();
        for (final File fileEntry : new File("/home/callum/MusicBot/Jankedex").listFiles()) {
            if (!fileEntry.isDirectory()) {
                files.add(fileEntry.getName());
            }
        }
        String msg_to_send = "";
        String file_to_ret = "";
        int pos;
        if (event.getArgs().trim() != "") {
            if (files.contains(event.getArgs().toLowerCase().trim() + ".png")) {
                pos = files.indexOf(event.getArgs().toLowerCase().trim() + ".png");
                file_to_ret = files.get(pos);
            } else {
                msg_to_send += "Could not find an entry for " + event.getArgs().trim() + ". ";
                Random rv = new Random();
                pos = rv.nextInt(files.size());
                file_to_ret = files.get(pos);
            }
        } else {
            Random rv = new Random();
            pos = rv.nextInt(files.size());
            file_to_ret = files.get(pos);
        }
        msg_to_send += "JANKEDEX ENTRY " + file_to_ret.split("\\.")[0] + ":";
        List<Component> comps = new ArrayList<Component>();
        if (pos - 1 != 0)
            comps.add(Button.secondary("JDX_PREV:" + files.get(pos - 1).split("\\.")[0], "⬅️"));
        if (pos + 1 != files.size() - 1)
            comps.add(Button.secondary("JDX_NEXT:" + files.get(pos + 1).split("\\.")[0], "➡️"));
        event.getEvent().getMessage().reply(msg_to_send).addFile(new File("/home/callum/MusicBot/Jankedex/" + file_to_ret)).setActionRow(comps).queue();
    }

}
