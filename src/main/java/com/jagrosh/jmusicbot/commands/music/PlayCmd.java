/*
 * Copyright 2016 John Grosh <john.a.grosh@gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jagrosh.jmusicbot.commands.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException.Severity;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import org.slf4j.LoggerFactory;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.menu.ButtonMenu;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.audio.AudioHandler;
import com.jagrosh.jmusicbot.audio.QueuedTrack;
import com.jagrosh.jmusicbot.commands.DJCommand;
import com.jagrosh.jmusicbot.commands.MusicCommand;
import com.jagrosh.jmusicbot.playlist.PlaylistLoader.Playlist;
import com.jagrosh.jmusicbot.playlist.SpotifyAPI.SpotifyPlaylistQuery;
import com.jagrosh.jmusicbot.utils.FormatUtil;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.exceptions.PermissionException;

/**
 *
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class PlayCmd extends MusicCommand
{
    private final static String LOAD = "\uD83D\uDCE5"; // 📥
    private final static String CANCEL = "\uD83D\uDEAB"; // 🚫
    
    private final String loadingEmoji;
    
    public PlayCmd(Bot bot)
    {
        super(bot);
        this.loadingEmoji = bot.getConfig().getLoading();
        this.name = "play";
        this.arguments = "<title|URL|subcommand>";
        this.help = "plays the provided song";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.beListening = true;
        this.bePlaying = false;
        this.children = new Command[]{new PlaylistCmd(bot)};
    }

    @Override
    public void doCommand(CommandEvent event) 
    {
        if(event.getArgs().isEmpty() && event.getMessage().getAttachments().isEmpty())
        {
            AudioHandler handler = (AudioHandler)event.getGuild().getAudioManager().getSendingHandler();
            if(handler.getPlayer().getPlayingTrack()!=null && handler.getPlayer().isPaused())
            {
                if(DJCommand.checkDJPermission(event))
                {
                    handler.getPlayer().setPaused(false);
                    event.replySuccess("Resumed **"+handler.getPlayer().getPlayingTrack().getInfo().title+"**.");
                }
                else
                    event.replyError("Only DJs can unpause the player!");
                return;
            }
            StringBuilder builder = new StringBuilder(event.getClient().getWarning()+" Play Commands:\n");
            builder.append("\n`").append(event.getClient().getPrefix()).append(name).append(" <song title>` - plays the first result from Youtube");
            builder.append("\n`").append(event.getClient().getPrefix()).append(name).append(" <URL>` - plays the provided song, playlist, or stream");
            for(Command cmd: children)
                builder.append("\n`").append(event.getClient().getPrefix()).append(name).append(" ").append(cmd.getName()).append(" ").append(cmd.getArguments()).append("` - ").append(cmd.getHelp());
            event.reply(builder.toString());
            return;
        }

        String args = event.getArgs().startsWith("<") && event.getArgs().endsWith(">") 
            ? event.getArgs().substring(1,event.getArgs().length()-1) 
            : event.getArgs().isEmpty() ? event.getMessage().getAttachments().get(0).getUrl() : event.getArgs();

        LoggerFactory.getLogger("MusicBot").info("Playing: " + args);
        String spotifyPlaylistId = bot.getSpotifyAPI().tryGetPlaylistIdFromUrl(args);
        if (spotifyPlaylistId != null) {
            try {
                SpotifyPlaylistQuery query = bot.getSpotifyAPI().getPlaylistTracksSearchQueries(spotifyPlaylistId);
                if (query.trackQueries.length == 0) {
                    event.reply(event.getClient().getError() +" Spotify playlist does not have any tracks... `["+query.name+"]`");
                } else {
                    event.reply(bot.getConfig().getLoading() +" Loading... `["+query.name+"]`", 
                        m -> bot.getPlayerManager().loadItemOrdered(event.getGuild(), "ytsearch:"+query.trackQueries[0], new SpotifyResultHandler(m, event, true, query)));
                }
            } catch (Exception e) {
                LoggerFactory.getLogger("MusicBot").error("Failed to load spotify playlist from: " + args, e);
                event.reply(event.getClient().getError()+" Failed to load spotify playlist... `["+spotifyPlaylistId+"]`");
                return;
            }
        } else {
            event.reply(loadingEmoji+" Loading... `["+args+"]`", m -> bot.getPlayerManager().loadItemOrdered(event.getGuild(), args, new ResultHandler(m,event,false)));
        }
    }
    
    private class ResultHandler implements AudioLoadResultHandler
    {
        protected final Message m;
        protected final CommandEvent event;
        protected final boolean ytsearch;
        
        private ResultHandler(Message m, CommandEvent event, boolean ytsearch)
        {
            this.m = m;
            this.event = event;
            this.ytsearch = ytsearch;
        }
        
        protected void loadSingle(AudioTrack track, AudioPlaylist playlist)
        {
            if(bot.getConfig().isTooLong(track))
            {
                m.editMessage(FormatUtil.filter(event.getClient().getWarning()+" This track (**"+track.getInfo().title+"**) is longer than the allowed maximum: `"
                        +FormatUtil.formatTime(track.getDuration())+"` > `"+FormatUtil.formatTime(bot.getConfig().getMaxSeconds()*1000)+"`")).queue();
                return;
            }
            AudioHandler handler = (AudioHandler)event.getGuild().getAudioManager().getSendingHandler();
            int pos = handler.addTrack(new QueuedTrack(track, event.getAuthor()))+1;
            String addMsg = FormatUtil.filter(event.getClient().getSuccess()+" Added **"+track.getInfo().title
                    +"** (`"+FormatUtil.formatTime(track.getDuration())+"`) "+(pos==0?"to begin playing":" to the queue at position "+pos));
            if(playlist==null || !event.getSelfMember().hasPermission(event.getTextChannel(), Permission.MESSAGE_ADD_REACTION))
                m.editMessage(addMsg).queue();
            else
            {
                new ButtonMenu.Builder()
                        .setText(addMsg+"\n"+event.getClient().getWarning()+" This track has a playlist of **"+playlist.getTracks().size()+"** tracks attached. Select "+LOAD+" to load playlist.")
                        .setChoices(LOAD, CANCEL)
                        .setEventWaiter(bot.getWaiter())
                        .setTimeout(30, TimeUnit.SECONDS)
                        .setAction(re ->
                        {
                            if(re.getName().equals(LOAD))
                                m.editMessage(addMsg+"\n"+event.getClient().getSuccess()+" Loaded **"+loadPlaylist(playlist, track)+"** additional tracks!").queue();
                            else
                                m.editMessage(addMsg).queue();
                        }).setFinalAction(m ->
                        {
                            try{ m.clearReactions().queue(); }catch(PermissionException ignore) {}
                        }).build().display(m);
            }
        }
        
        protected int loadPlaylist(AudioPlaylist playlist, AudioTrack exclude)
        {
            int[] count = {0};
            playlist.getTracks().stream().forEach((track) -> {
                if(!bot.getConfig().isTooLong(track) && !track.equals(exclude))
                {
                    AudioHandler handler = (AudioHandler)event.getGuild().getAudioManager().getSendingHandler();
                    handler.addTrack(new QueuedTrack(track, event.getAuthor()));
                    count[0]++;
                }
            });
            return count[0];
        }
        
        @Override
        public void trackLoaded(AudioTrack track)
        {
            loadSingle(track, null);
        }

        @Override
        public void playlistLoaded(AudioPlaylist playlist)
        {
            if(playlist.getTracks().size()==1 || playlist.isSearchResult())
            {
                AudioTrack single = playlist.getSelectedTrack()==null ? playlist.getTracks().get(0) : playlist.getSelectedTrack();
                loadSingle(single, null);
            }
            else if (playlist.getSelectedTrack()!=null)
            {
                AudioTrack single = playlist.getSelectedTrack();
                loadSingle(single, playlist);
            }
            else
            {
                int count = loadPlaylist(playlist, null);
                if(count==0)
                {
                    m.editMessage(FormatUtil.filter(event.getClient().getWarning()+" All entries in this playlist "+(playlist.getName()==null ? "" : "(**"+playlist.getName()
                            +"**) ")+"were longer than the allowed maximum (`"+bot.getConfig().getMaxTime()+"`)")).queue();
                }
                else
                {
                    m.editMessage(FormatUtil.filter(event.getClient().getSuccess()+" Found "
                            +(playlist.getName()==null?"a playlist":"playlist **"+playlist.getName()+"**")+" with `"
                            + playlist.getTracks().size()+"` entries; added to the queue!"
                            + (count<playlist.getTracks().size() ? "\n"+event.getClient().getWarning()+" Tracks longer than the allowed maximum (`"
                            + bot.getConfig().getMaxTime()+"`) have been omitted." : ""))).queue();
                }
            }
        }

        @Override
        public void noMatches()
        {
            if(ytsearch)
                m.editMessage(FormatUtil.filter(event.getClient().getWarning()+" No results found for `"+event.getArgs()+"`.")).queue();
            else
                bot.getPlayerManager().loadItemOrdered(event.getGuild(), "ytsearch:"+event.getArgs(), new ResultHandler(m,event,true));
        }

        @Override
        public void loadFailed(FriendlyException throwable)
        {
            if(throwable.severity==Severity.COMMON)
                m.editMessage(event.getClient().getError()+" Error loading: "+throwable.getMessage()).queue();
            else
                m.editMessage(event.getClient().getError()+" Error loading track.").queue();
        }
    }
    
    private class SpotifyResultHandler extends ResultHandler {
        
        private final SpotifyPlaylistQuery query;
        private final int iQuery;
        private final String prevMessageText;
        private final int prevErrors;

        private SpotifyResultHandler(Message m, CommandEvent event, boolean ytsearch, SpotifyPlaylistQuery query, int iQuery, String prevMessageText, int prevErrors)
        {
            super(m, event, ytsearch);
            this.query = query;
            this.iQuery = iQuery;
            this.prevMessageText = prevMessageText;
            this.prevErrors = prevErrors;
        }

        private SpotifyResultHandler(Message m, CommandEvent event, boolean ytsearch, SpotifyPlaylistQuery query) {
            this(m, event, ytsearch, query, 0, "", 0);
        }

        protected void loadSingle(AudioTrack track, AudioPlaylist playlist)
        {
            if (iQuery == 0) {
                if (track == null) {
                    m.editMessage(FormatUtil.filter(event.getClient().getError()+" Couldn't find first track: "+query.trackQueries[0])).queue();
                    return;
                }
                if(bot.getConfig().isTooLong(track))
                {
                    m.editMessage(FormatUtil.filter(event.getClient().getWarning()+" This track (**"+track.getInfo().title+"**) is longer than the allowed maximum: `"
                            +FormatUtil.formatTime(track.getDuration())+"` > `"+FormatUtil.formatTime(bot.getConfig().getMaxSeconds()*1000)+"`")).queue();
                    return;
                }
                AudioHandler handler = (AudioHandler)event.getGuild().getAudioManager().getSendingHandler();
                int pos = handler.addTrack(new QueuedTrack(track, event.getAuthor()))+1;
                String addMsg = FormatUtil.filter(event.getClient().getSuccess()+" Added **"+track.getInfo().title
                        +"** (`"+FormatUtil.formatTime(track.getDuration())+"`) "+(pos==0?"to begin playing":" to the queue at position "+pos));
                if(query.trackQueries.length == 1 || !event.getSelfMember().hasPermission(event.getTextChannel(), Permission.MESSAGE_ADD_REACTION))
                    m.editMessage(addMsg).queue();
                else
                {
                    new ButtonMenu.Builder()
                            .setText(addMsg+"\n"+event.getClient().getWarning()+" This playlist has **"+query.trackQueries.length+"** tracks attached. Select "+LOAD+" to load playlist.")
                            .setChoices(LOAD, CANCEL)
                            .setEventWaiter(bot.getWaiter())
                            .setTimeout(30, TimeUnit.SECONDS)
                            .setAction(re ->
                            {
                                if(re.getName().equals(LOAD)) {
                                    String newMessageText = addMsg+"\n"+event.getClient().getSuccess()+" Loading **1/"+(query.trackQueries.length - 1)+"** additional tracks...";
                                    m.editMessage(newMessageText).queue();
                                    bot.getPlayerManager().loadItemOrdered(event.getGuild(), "ytsearch:"+query.trackQueries[1], new SpotifyResultHandler(m,event,true,query,1,newMessageText,prevErrors));
                                }
                                else
                                    m.editMessage(addMsg).queue();
                            }).setFinalAction(m ->
                            {
                                try{ m.clearReactions().queue(); }catch(PermissionException ignore) {}
                            }).build().display(m);
                }
            } else {
                // this is an additional track being loaded
                String newMessageText = prevMessageText;
                int newErrors = prevErrors;
                if (track == null)
                {
                    newMessageText = prevMessageText+"\n"+FormatUtil.filter(event.getClient().getError()+" Couldn't find track " + (iQuery + 1) + " of " + query.trackQueries.length + ": "+query.trackQueries[iQuery]);
                    newErrors++;
                    m.editMessage(newMessageText).queue();
                }
                else if(bot.getConfig().isTooLong(track))
                {
                    newMessageText = prevMessageText+"\n"+FormatUtil.filter(event.getClient().getWarning()+" This track (**"+track.getInfo().title+"**) is longer than the allowed maximum: `"
                        +FormatUtil.formatTime(track.getDuration())+"` > `"+FormatUtil.formatTime(bot.getConfig().getMaxSeconds()*1000)+"`");
                    newErrors++;
                    m.editMessage(newMessageText).queue();
                } else {
                    AudioHandler handler = (AudioHandler)event.getGuild().getAudioManager().getSendingHandler();
                    handler.addTrack(new QueuedTrack(track, event.getAuthor()));
                }
                
                String[] messageParts = newMessageText.split("\n");
                String firstLine = messageParts[0];
                // second line will get updated
                String[] remainingParts = new String[messageParts.length - 2];
                for (int i = 0; i < messageParts.length - 2; i++) {
                    remainingParts[i] = messageParts[2 + i];
                }
                if (iQuery < query.trackQueries.length - 1) {
                    // update second line ("loading tracks...")
                    newMessageText = firstLine+"\n"+(event.getClient().getSuccess()+" Loading **" + (iQuery + 1) + "/"+(query.trackQueries.length - 1)+"** additional tracks...")+(remainingParts.length == 0 ? "" : "\n"+String.join("\n", remainingParts));
                    m.editMessage(newMessageText).queue();
                    bot.getPlayerManager().loadItemOrdered(event.getGuild(), "ytsearch:"+query.trackQueries[iQuery + 1], new SpotifyResultHandler(m,event,true,query,iQuery + 1,newMessageText,newErrors));
                } else {
                    // all done! finalize message
                    // remove the second line ("loading tracks...") but keep everything else
                    newMessageText = String.format("%s\n%s",
                        firstLine + (remainingParts.length == 0 ? "" : "\n"+String.join("\n", remainingParts)),
                        event.getClient().getSuccess()+" Loaded **"+(query.trackQueries.length - newErrors - 1)+"** additional tracks!"
                    );
                    m.editMessage(newMessageText).queue();
                }
            }
        }

        @Override
        public void playlistLoaded(AudioPlaylist playlist)
        {
            // This means a youtube playlist was loaded for one of the tracks.
            // Just load the first track of the playlist.

            if(playlist.getTracks().size()>=1 || playlist.isSearchResult())
            {
                AudioTrack single = playlist.getSelectedTrack()==null ? playlist.getTracks().get(0) : playlist.getSelectedTrack();
                loadSingle(single, null);
            }
            else if (playlist.getSelectedTrack()!=null)
            {
                AudioTrack single = playlist.getSelectedTrack();
                loadSingle(single, playlist);
            }
            else
            {
                // failure
                loadSingle(null, null);
            }
        }

        @Override
        public void noMatches()
        {
            if(ytsearch)
                loadSingle(null, null);
            else
                bot.getPlayerManager().loadItemOrdered(event.getGuild(), "ytsearch:"+query.trackQueries[iQuery], new SpotifyResultHandler(m,event,true,query,iQuery + 1,prevMessageText,prevErrors));
        }

        @Override
        public void loadFailed(FriendlyException throwable)
        {
            LoggerFactory.getLogger("MusicBot").error("Load failed for: " + query.trackQueries[iQuery], throwable);
            loadSingle(null, null);
        }
    }

    public class PlaylistCmd extends MusicCommand
    {
        public PlaylistCmd(Bot bot)
        {
            super(bot);
            this.name = "playlist";
            this.aliases = new String[]{"pl"};
            this.arguments = "<name>";
            this.help = "plays the provided playlist";
            this.beListening = true;
            this.bePlaying = false;
        }

        @Override
        public void doCommand(CommandEvent event) 
        {
            if(event.getArgs().isEmpty())
            {
                event.reply(event.getClient().getError()+" Please include a playlist name.");
                return;
            }
            Playlist playlist = bot.getPlaylistLoader().getPlaylist(event.getArgs());
            if(playlist==null)
            {
                event.replyError("I could not find `"+event.getArgs()+".txt` in the Playlists folder.");
                return;
            }
            event.getChannel().sendMessage(loadingEmoji+" Loading playlist **"+event.getArgs()+"**... ("+playlist.getItems().size()+" items)").queue(m -> 
            {
                AudioHandler handler = (AudioHandler)event.getGuild().getAudioManager().getSendingHandler();
                playlist.loadTracks(bot.getPlayerManager(), (at)->handler.addTrack(new QueuedTrack(at, event.getAuthor())), () -> {
                    StringBuilder builder = new StringBuilder(playlist.getTracks().isEmpty() 
                            ? event.getClient().getWarning()+" No tracks were loaded!" 
                            : event.getClient().getSuccess()+" Loaded **"+playlist.getTracks().size()+"** tracks!");
                    if(!playlist.getErrors().isEmpty())
                        builder.append("\nThe following tracks failed to load:");
                    playlist.getErrors().forEach(err -> builder.append("\n`[").append(err.getIndex()+1).append("]` **").append(err.getItem()).append("**: ").append(err.getReason()));
                    String str = builder.toString();
                    if(str.length()>2000)
                        str = str.substring(0,1994)+" (...)";
                    m.editMessage(FormatUtil.filter(str)).queue();
                });
            });
        }
    }
}
