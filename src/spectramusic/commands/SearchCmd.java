/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spectramusic.commands;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import net.dv8tion.jda.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.player.Playlist;
import net.dv8tion.jda.player.source.AudioInfo;
import net.dv8tion.jda.player.source.AudioSource;
import spectramusic.Bot;
import spectramusic.Command;
import spectramusic.Sender;
import spectramusic.SpConst;
import spectramusic.entities.ClumpedMusicPlayer;
import spectramusic.web.YoutubeSearcher;
import spectramusic.web.YoutubeSearcher.YoutubeInfo;

/**
 *
 * @author johna
 */
public class SearchCmd extends Command {
    private final Bot bot;
    private final YoutubeSearcher searcher;
    public SearchCmd(Bot bot, YoutubeSearcher searcher)
    {
        this.bot = bot;
        this.searcher = searcher;
        this.command = "search";
        this.help = "searches YouTube and offers results to be played";
        this.arguments = "<query>";
        this.userMustBeInVC = true;
    }
    
    @Override
    protected void execute(String args, GuildMessageReceivedEvent event, PermLevel caller, ClumpedMusicPlayer player) {
        StringBuilder builder = new StringBuilder(SpConst.SUCCESS+"<@"+event.getAuthor().getId()+"> Results for `"+args+"`:");
        if(searcher==null)
        {
            try 
            {
                String query = "ytsearch4:"+URLEncoder.encode(args, "UTF-8");
                Playlist playlist;
                event.getChannel().sendTyping();
                playlist = Playlist.getPlaylist(query);
                List<AudioSource> list = new ArrayList<>();
                if(playlist.getSources().isEmpty())
                {
                    Sender.sendReply(SpConst.WARNING+"No results found for `"+args+"`", event);
                    return;
                }
                for(int i=0; i<playlist.getSources().size() && i<3; i++)
                {
                    AudioInfo info = playlist.getSources().get(i).getInfo();
                    if(info.getError()==null)
                    {
                        list.add(playlist.getSources().get(i));
                        builder.append("\n**").append(i+1).append(".** `[")
                            .append(info.getDuration().getTimestamp())
                            .append("]` **").append(info.getTitle()).append("**");
                    }
                }
                builder.append("\nType the number of your choice to play, or any invalid choice to cancel");
                Sender.sendReplyNoDelete(builder.toString(), event, m -> bot.addSearch(event, list, null, m) );
            } 
            catch(NullPointerException | UnsupportedEncodingException e)
            {
                Sender.sendReply(SpConst.ERROR+"The given query or result was invalid", event);
            }
        }
        else
        {
            List<YoutubeInfo> list = searcher.getResults(args, 3);
            if(list==null)
            {
                Sender.sendReply(SpConst.ERROR+"The search was unable to be completed", event);
                return;
            }
            if(list.isEmpty())
            {
                Sender.sendReply(SpConst.WARNING+"No results found for `"+args+"`", event);
                return;
            }
            for(int i=0; i<list.size(); i++)
            {
                builder.append("\n**").append(i+1).append(".** `[")
                        .append(list.get(i).duration)
                        .append("]` **").append(list.get(i).title).append("**");
            }
            builder.append("\nType the number of your choice to play, or any invalid choice to cancel");
            Sender.sendReplyNoDelete(builder.toString(), event, m -> bot.addSearch(event, null, list, m) );
        }
    }
    
}
