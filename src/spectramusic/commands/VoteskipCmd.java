/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spectramusic.commands;

import java.util.ArrayList;
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
public class VoteskipCmd extends Command {

    public VoteskipCmd()
    {
        this.command = "skip";
        this.aliases = new String[]{"voteskip"};
        this.help = "votes to skip the current song (needs majority vote of listeners)";
        this.mustBePlaying = true;
        this.userMustBeInVC = true;
    }
    
    @Override
    protected void execute(String args, GuildMessageReceivedEvent event, PermLevel caller, ClumpedMusicPlayer player) {
        if(event.getAuthor().getId().equals(player.getCurrentRequestor()))
        {
            if(player.getCurrentTimestamp().getTotalSeconds()<1)
            {
                Sender.sendReply(SpConst.WARNING+"Please wait for the song to start before skipping.", event);
                return;
            }
            else
            {
                Sender.sendReply(SpConst.SUCCESS+"Skipped **"+player.getCurrentAudioSource().getInfo().getTitle()+"** (requested by **"+event.getAuthor().getUsername()+"**)", event);
                player.skipToNext();
                return;
            }
        }
        int listeners = 0;
        int skippers = 0;
        ArrayList<String> checked = new ArrayList<>();
        checked.add(event.getJDA().getSelfInfo().getId());
        for(User user : event.getGuild().getVoiceStatusOfUser(event.getJDA().getSelfInfo()).getChannel().getUsers())
        {
            if(checked.contains(user.getId()) || user.isBot())
                continue;
            if(event.getGuild().getVoiceStatusOfUser(user).inVoiceChannel() && !event.getGuild().getVoiceStatusOfUser(user).isDeaf())
                listeners++;
            if(player.getCurrentSkips().contains(user.getId()))
                skippers++;
            checked.add(user.getId());
        }
        int required = (int)Math.ceil(listeners * .6);
        String msg = "";
        if(player.getCurrentSkips().contains(event.getAuthor().getId()))
            msg+=SpConst.WARNING+"You have already voted to skip this song `[";
        else
        {
            skippers++;
            player.getCurrentSkips().add(event.getAuthor().getId());
            msg+=SpConst.SUCCESS+"You voted to skip the song `[";
        }
        msg+=skippers+" votes, "+required+"/"+listeners+" needed]`";
        if(skippers>=required)
        {
            if(player.getCurrentTimestamp().getTotalSeconds()<1)
            {
                Sender.sendReply(msg+"\n"+SpConst.WARNING+"Please wait for the song to start before skipping.", event);
                return;
            }
            else
            {
                User user = event.getJDA().getUserById(player.getCurrentRequestor());
                String title = player.getCurrentAudioSource().getInfo().getTitle();
                msg+="\n"+SpConst.SUCCESS+"Skipped **"+title
                    +"** (requested by "+(user==null ? "an unknown user" : "**"+user.getUsername()+"**)");
                player.skipToNext();
            }
        }
        Sender.sendReply(msg, event);
    }
    
}
