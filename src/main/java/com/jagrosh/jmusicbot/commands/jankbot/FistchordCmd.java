package com.jagrosh.jmusicbot.commands.jankbot;

import java.time.DayOfWeek;
import java.time.LocalDateTime;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jmusicbot.Bot;    

public class FistchordCmd extends Command {
    private boolean is_fistchord = false;
    private Bot bot;

    public FistchordCmd(Bot bot) {
        this.bot = bot;
        this.name = "fistchord";
        this.help = "Is it fistchord yet?";
        this.guildOnly = true;
        this.aliases = new String[] {"fistcord"};
    }

    public void execute(CommandEvent event) {
        LocalDateTime now = LocalDateTime.now();
        if(now.getDayOfWeek().equals(DayOfWeek.FRIDAY)){
            if(now.getHour() > 19) {
                if(this.bot.getJDA().getVoiceChannelById(638309927005323287L).getMembers().size() > 3 || this.is_fistchord){
                    event.reply("Yes!");
                    this.is_fistchord = true;
                } else {
                    event.reply("Not yet. Join <#638309927005323287> to get the party going!");
                }
            } else if(now.getHour() > 15){
                event.reply("Soon.");
            } else {
                event.reply("Not yet, but it is today (JankBot time).");
            }
        } else if (now.getDayOfWeek().equals(DayOfWeek.SATURDAY) && now.getHour() < 6) {
            if(this.bot.getJDA().getVoiceChannelById(638309927005323287L).getMembers().size() > 3) {
                event.reply("Yes!");
            } else {
                event.reply("Fistchord is over. <:Tantasad:822211935184879656>");
                this.is_fistchord = false;
            }
        } else {
            this.is_fistchord = false;
            event.reply("No. <:Tantasad:822211935184879656>");
        }
    }

    public void setIsFistchord(boolean in){
        this.is_fistchord = in;
    }

    public boolean getIsFistchord(){
        return this.is_fistchord;
    }
}
