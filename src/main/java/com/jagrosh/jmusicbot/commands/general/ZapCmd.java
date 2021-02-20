package com.jagrosh.jmusicbot.commands.general;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class ZapCmd extends Command {
    private final Long[] permitted = new Long[]{};

    public ZapCmd() {
        this.name = "zap";
        this.help = "shock the monkey";
        this.guildOnly = false;
        this.cooldown = 15;
        this.cooldownScope = CooldownScope.SHARD;
    }

    @Override
    protected void execute(CommandEvent event) {
        try {
            Unirest.post("https://shocky.wferr.com/command?user=vctr&target=SplitPixl").body("zap").asString();
            System.out.println(event.getGuild().toString() + " " + event.getAuthor().toString()+" zapped");
            event.reply("Zap sent.");
        } catch (UnirestException e) {
            e.printStackTrace();
        }
    }
}
