/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spectramusic.commands;

import java.util.logging.Level;
import java.util.logging.Logger;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.events.message.guild.GuildMessageReceivedEvent;
import spectramusic.Command;
import spectramusic.Sender;
import spectramusic.SpConst;
import spectramusic.entities.ClumpedMusicPlayer;

/**
 *
 * @author johna
 */
public class SkipCmd extends Command {

    public SkipCmd()
    {
        this.command = "skip";
        this.help = "skips the current song in the queue";
        this.level = PermLevel.DJ;
        this.mustBePlaying = true;
    }
    
    @Override
    protected void execute(String args, GuildMessageReceivedEvent event, PermLevel caller, ClumpedMusicPlayer player) {
        if(player.getCurrentTimestamp().getTotalSeconds()<1)
        {
            Sender.sendReply(SpConst.WARNING+"Please wait for the song to start before skipping.", event);
            return;
        }
        User user = event.getJDA().getUserById(player.getCurrentRequestor());
        String title = player.getCurrentAudioSource().getInfo().getTitle();
        String msg = SpConst.SUCCESS+"Skipped **"+title
                +"** (requested by "+(user==null ? "an unknown user" : "**"+user.getUsername()+"**)");
        Sender.sendReply(msg, event);
        player.skipToNext();
    }
    
}
