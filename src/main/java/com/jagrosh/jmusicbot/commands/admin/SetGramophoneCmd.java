package com.jagrosh.jmusicbot.commands.admin;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.commands.MusicCommand;

import net.dv8tion.jda.api.entities.Role;

public class SetGramophoneCmd extends MusicCommand {
    //private Bot bot;
    public SetGramophoneCmd(Bot bot) {
        super(bot);
        this.name = "gramophone";
        this.help = "enable gramophone mode (mod only)";
    }
    
    @Override
    public void doCommand(CommandEvent event) {
        boolean is_mod = false;
        List<Role> user_roles = event.getMember().getRoles();
        for (Role r : user_roles) {
            if (r.getIdLong() == 736622853797052519L) {
                is_mod = true;
                break;
            }
        }
        if(!is_mod) return;
        String args = event.getArgs().toLowerCase().trim();
        switch(args){
            case "on":
                this.bot.setDJMode(true);
                this.bot.setGramophoneMode(true);
                event.reply("Gramophone Mode On. Auto-off in 2 hours and 10 minutes.");
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if(bot.getDJMode()){
                            bot.setDJMode(false);
                            bot.setGramophoneMode(false);
                            event.reply("DJ & Gramophone Mode have been automatically switched OFF.");
                        }
                    }
                }, (130L * 60000L)); // 300 is the delay in millis
                
                break;
            case "off":
                this.bot.setDJMode(false);
                this.bot.setGramophoneMode(false);
                event.reply("Gramophone Mode Off.");
                break;
            case "":
                event.reply("Gramophone Mode is currently " + (this.bot.getGramophoneMode() ? "ON" : "OFF"));
                break;
            default:
                event.reply("Didn't understand. j!gramophone <on|off>");
        }
    }

}
