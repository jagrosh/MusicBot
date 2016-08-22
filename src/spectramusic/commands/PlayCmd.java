/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spectramusic.commands;

import java.util.ArrayList;
import java.util.List;
import net.dv8tion.jda.Permission;
import net.dv8tion.jda.entities.VoiceChannel;
import net.dv8tion.jda.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.player.Playlist;
import net.dv8tion.jda.player.source.AudioInfo;
import net.dv8tion.jda.player.source.AudioSource;
import spectramusic.Command;
import spectramusic.Sender;
import spectramusic.SpConst;
import spectramusic.entities.ClumpedMusicPlayer;
import spectramusic.entities.ClumpedQueue;

/**
 *
 * @author johna
 */
public class PlayCmd extends Command {

    public static final String YT_ID = "[a-zA-Z0-9\\-_]+";
    
    public PlayCmd()
    {
        this.command = "play";
        this.arguments = "<URL>";
        this.help = "plays the song at the specified URL (or youtube video ID)";
        this.userMustBeInVC = true;
    }
    
    @Override
    protected void execute(String args, GuildMessageReceivedEvent event, PermLevel caller, ClumpedMusicPlayer player) {
        if(args.startsWith("<") && args.endsWith(">"))
            args = args.substring(1,args.length()-1);
        args = args.split("\\s+")[0];
        if(args.contains("&list="))
            args = args.split("&list=")[0];
        if(args.equals(""))
        {
            Sender.sendReply(SpConst.ERROR+"Please specify a url", event);
            return;
        }
        if(!event.getGuild().getVoiceStatusOfUser(event.getJDA().getSelfInfo()).inVoiceChannel())
        {
            VoiceChannel target = event.getGuild().getVoiceStatusOfUser(event.getAuthor()).getChannel();
            if(!target.checkPermission(event.getJDA().getSelfInfo(), Permission.VOICE_CONNECT) || !target.checkPermission(event.getJDA().getSelfInfo(), Permission.VOICE_SPEAK))
            {
                Sender.sendReply(SpConst.ERROR+"I must be able to connect and speak in **"+target.getName()+"** to join!", event);
                return;
            }
            event.getGuild().getAudioManager().openAudioConnection(target);
        }
        String url = args;
        Sender.sendReply("\u231A Loading... `["+url+"]`", event, () -> {
            Playlist playlist;
            try {
                playlist = Playlist.getPlaylist(url);
            } catch(NullPointerException e)
            {
                return SpConst.ERROR+"The given link or playlist was invalid";
            }
            List<AudioSource> sources = new ArrayList(playlist.getSources());
            String id = event.getAuthor().getId();
            if (sources.size() > 1)
            {
                final ClumpedMusicPlayer fPlayer = player;
                Thread thread = new Thread()
                {
                    @Override
                    public void run()
                    {
                        int count = 0;
                        for(AudioSource it : sources)
                        {
                            AudioSource source = it;
                            AudioInfo info = source.getInfo();
                            ClumpedQueue<String,AudioSource> queue = fPlayer.getAudioQueue();
                            if (info.getError() == null)
                            {
                                queue.add(id,source);
                                count++;
                                if (fPlayer.isStopped())
                                    fPlayer.play();
                            }
                        }
                        Sender.sendAlert(SpConst.SUCCESS+"Successfully queued "+count+" (out of "+sources.size()+") sources [<@"+id+">]", event);
                    }
                };
                thread.start();
                return SpConst.SUCCESS+"Found a playlist with `"
                        +sources.size()+"` entries.\n\u231A Queueing sources... (this may take some time)";
            }
            else
            {
                AudioSource source = sources.get(0);
                AudioInfo info = source.getInfo();
                if (info.getError() == null)
                {
                    int position = player.getAudioQueue().add(id,source);
                    if(player.isStopped())
                        player.play();
                    return SpConst.SUCCESS+"Added **"+info.getTitle()
                            +"** (`"+(info.isLive() ? "LIVE" : info.getDuration().getTimestamp())+"`) to the queue "+(position==0 ? "and will begin playing" :"at position "+(position+1));
                    
                }
                else
                {
                    return SpConst.ERROR+"There was a problem with the provided source:\n"+info.getError();
                }
            }
        });
        }
    }
