/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spectramusic.commands;

import java.awt.image.BufferedImage;
import net.dv8tion.jda.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.utils.AvatarUtil;
import spectramusic.Command;
import spectramusic.Sender;
import spectramusic.SpConst;
import spectramusic.entities.ClumpedMusicPlayer;
import spectramusic.util.OtherUtil;

/**
 *
 * @author johna
 */
public class SetnameCmd extends Command {

    public SetnameCmd()
    {
        this.command = "setname";
        this.help = "sets the bot's username";
        this.arguments = "<username>";
        this.level = PermLevel.OWNER;
    }
    
    @Override
    protected void execute(String args, GuildMessageReceivedEvent event, PermLevel caller, ClumpedMusicPlayer player) {
        if(args==null || args.length()<2 || args.length()>32)
        {
            Sender.sendReply(SpConst.ERROR+"Please specify an username (2 to 32 characters)", event);
            return;
        }
        try {
            event.getJDA().getAccountManager().setUsername(args).update();
            event.getChannel().sendMessage(SpConst.SUCCESS+"Successfully changed avatar!");
        } catch(Exception e)
        {
            Sender.sendReply(SpConst.ERROR+"Invalid username.", event);
        }
    }
    
}
