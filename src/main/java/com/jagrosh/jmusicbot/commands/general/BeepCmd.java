package com.jagrosh.jmusicbot.commands.general;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class BeepCmd extends Command {

    public BeepCmd() {
        this.name = "beep";
        this.help = "an annoying noise";
        this.guildOnly = false;
    }

    @Override
    protected void execute(CommandEvent event) {
        try {
            Unirest.post("https://shocky.wferr.com/command?user=vctr&target=SplitPixl").body("beep").asString();
            event.reply("Beep sent.");
        } catch (UnirestException e) {
            e.printStackTrace();
        }
    }
}
