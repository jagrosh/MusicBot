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
public class SetavatarCmd extends Command {

    public SetavatarCmd()
    {
        this.command = "setavatar";
        this.help = "sets the bot's avatar";
        this.arguments = "<url or NONE>";
        this.level = PermLevel.OWNER;
    }
    
    @Override
    protected void execute(String args, GuildMessageReceivedEvent event, PermLevel caller, ClumpedMusicPlayer player) {
        if(args==null || args.equals(""))
        {
            Sender.sendReply(SpConst.ERROR+"Please specify an avatar URL", event);
            return;
        }
        if(args.equalsIgnoreCase("none"))
        {
            event.getJDA().getAccountManager().setAvatar(null).update();
            event.getChannel().sendMessage(SpConst.SUCCESS+"Successfully removed avatar!");
            return;
        }
        BufferedImage image = OtherUtil.imageFromUrl(args);
        if(image==null)
        {
            Sender.sendReply(SpConst.WARNING+"Invalid or inaccessable image URL", event);
            return;
        }
        try {
            event.getJDA().getAccountManager().setAvatar(AvatarUtil.getAvatar(image)).update();
            event.getChannel().sendMessage(SpConst.SUCCESS+"Successfully changed avatar!");
        }catch(Exception e)
        {
            Sender.sendReply(SpConst.WARNING+"Something went wrong changing the avatar.", event);
        }
    }
    
}
