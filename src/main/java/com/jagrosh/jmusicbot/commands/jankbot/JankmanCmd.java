package com.jagrosh.jmusicbot.commands.jankbot;

import java.io.File;


import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jmusicbot.Bot;



public class JankmanCmd extends Command {
        private Bot bot; 
    
        public JankmanCmd(Bot bot) {
            this.bot = bot;
            this.name = "jankman";
            this.help = "Get a jankman.";
            this.guildOnly = true;
        }
    
        public void execute(CommandEvent event) {
            event.getEvent().getMessage().reply(" ").addFile(new File("/home/callum/MusicBot/images/jman.png")).queue();
        }
    
    }