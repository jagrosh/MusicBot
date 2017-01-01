/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spectramusic.commands;

import net.dv8tion.jda.events.message.guild.GuildMessageReceivedEvent;
import spectramusic.Bot;
import spectramusic.Command;
import spectramusic.Sender;
import spectramusic.SpConst;
import spectramusic.entities.ClumpedMusicPlayer;

/**
 *
 * @author johna
 */
public class PlayCmd extends Command {

    //public static final String YT_ID = "[a-zA-Z0-9\\-_]+";
    private final Bot bot;
    public PlayCmd(Bot bot)
    {
        this.bot = bot;
        this.command = "play";
        this.arguments = "<URL or song title>";
        this.help = "plays the song at the specified URL (or youtube video ID/search)";
        this.userMustBeInVC = true;
    }
    
    @Override
    protected void execute(String args, GuildMessageReceivedEvent event, PermLevel caller, ClumpedMusicPlayer player) {
        if(args.startsWith("<") && args.endsWith(">"))
            args = args.substring(1,args.length()-1);
        //args = args.split("\\s+")[0];
        if(args.contains("&list="))
            args = args.split("&list=")[0];
        if(args.equals(""))
        {
            Sender.sendReply(SpConst.ERROR+"Please specify a url", event);
            return;
        }
        bot.addToQueue(event, args);
        }
    }
