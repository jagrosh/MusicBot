package com.jagrosh.jmusicbot.commands.music;

import java.io.IOException;
import java.util.List;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.audio.AudioHandler;
import com.jagrosh.jmusicbot.commands.MusicCommand;
import com.jagrosh.jmusicbot.playlist.PlaylistLoader.Playlist;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

public class SaveCmd extends MusicCommand {

    
    public SaveCmd(Bot bot)
    {
        super(bot);
        this.name = "save";
        this.help = "Save currently playing track";
        this.aliases = bot.getConfig().getAliases(this.name);
    }
    

    @Override
    public void doCommand(CommandEvent event) 
    {
        User owner = bot.getJDA().retrieveUserById(bot.getConfig().getOwnerId()).complete();
        AudioHandler handler = (AudioHandler)event.getGuild().getAudioManager().getSendingHandler();
        Message m = handler.getNowPlaying(event.getJDA());
        if(m==null)
        {
            event.reply(event.getClient().getError()+" No Song currently playing.");
        }
        else
        {
            try {
                String url = m.getEmbeds().get(0).getUrl();
                Playlist playlist = bot.getPlaylistLoader().getPlaylist("saved");
                if(playlist==null){
                    
                        bot.getPlaylistLoader().createPlaylist("saved");
                        bot.getPlaylistLoader().writePlaylist("saved", url);
                    
                    event.reply(event.getClient().getSuccess()+" Initially created \"saved\" playlist.");
                    event.reply(event.getClient().getSuccess()+" Saved track!");
                }
                else{
                    List<String> tracks = playlist.getItems();
                    if(tracks.contains(url)){
                        event.reply(event.getClient().getWarning()+" Song already saved!");
                    }else{
                        bot.getPlaylistLoader().writePlaylist("saved", url);
                        event.reply(event.getClient().getSuccess()+" Saved track!");
                    }                    
                }
            } catch (Exception e) {
                owner.openPrivateChannel().queue(pc -> pc.sendMessage(event.getClient().getError()+" I was unable to create the playlist: " + e).queue());
            }
        }
    }
}