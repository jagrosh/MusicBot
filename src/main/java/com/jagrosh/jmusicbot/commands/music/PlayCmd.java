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
import com.jagrosh.jmusicbot.SpotifyAPI;
import com.jagrosh.jmusicbot.SpotifyAPI.SpotifyPlaylist;
import com.jagrosh.jmusicbot.SpotifyAPI.SpotifyTrack;
import com.jagrosh.jmusicbot.SpotifyAPI.SpotifyUrlData;
import com.jagrosh.jmusicbot.audio.AudioHandler;
import com.jagrosh.jmusicbot.audio.QueuedTrack;
import com.jagrosh.jmusicbot.commands.DJCommand;
import com.jagrosh.jmusicbot.commands.MusicCommand;
import com.jagrosh.jmusicbot.playlist.PlaylistLoader.Playlist;
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
        
    private final Bot bot;

    public PlayCmd(Bot bot)
    {
        super(bot);
        this.bot = bot;
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
                    event.reply(bot.getSuccess(event)+"Resumed **"+handler.getPlayer().getPlayingTrack().getInfo().title+"**.");
                }
                else
                    event.reply(bot.getError(event)+"Only DJs can unpause the player!");
                return;
            }
            StringBuilder builder = new StringBuilder(bot.getWarning(event)+" Play Commands:\n");
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
        SpotifyUrlData spotifyUrl = SpotifyAPI.tryParseUrl(args);
        if (spotifyUrl != null) {
            if (bot.getSpotifyAPI() == null) {
                event.reply(bot.getError(event)+" Spotify not enabled on this bot; contact the owner.");
            } else {
                if (spotifyUrl.type == SpotifyUrlData.Type.TRACK) {
                    try {
                        SpotifyTrack track = bot.getSpotifyAPI().getTrack(spotifyUrl.id);
                        event.reply(bot.getLoading(event) +" Loading... `["+track.name+"]`", 
                            m -> bot.getPlayerManager().loadItemOrdered(event.getGuild(), "ytsearch:"+getYoutubeQueryOfTrack(track), new SpotifyResultHandler(m, event, track)));
                    } catch (Exception e) {
                        LoggerFactory.getLogger("MusicBot").error("Failed to load spotify track from: " + args, e);
                        event.reply(bot.getError(event)+" Failed to load spotify track... `["+spotifyUrl.id+"]`");
                    }
                } else {
                    String noun = (spotifyUrl.type == SpotifyUrlData.Type.PLAYLIST) ? "playlist" : "album";
                    try {
                        SpotifyPlaylist playlist = 
                            (spotifyUrl.type == SpotifyUrlData.Type.PLAYLIST)
                            ? bot.getSpotifyAPI().getPlaylist(spotifyUrl.id)
                            : bot.getSpotifyAPI().getAlbum(spotifyUrl.id);
    
                        if (playlist.tracks.length == 0) {
                            event.reply(bot.getError(event) +" Spotify "+noun+" does not have any tracks... `["+playlist.name+"]`");
                        } else {
                            event.reply(bot.getLoading(event) +" Loading... `["+String.format("%s (%d track%s)", playlist.name, playlist.tracks.length, playlist.tracks.length == 1 ? "" : "s")+"]`", 
                                m -> bot.getPlayerManager().loadItemOrdered(event.getGuild(), "ytsearch:"+getYoutubeQueryOfTrack(playlist.tracks[0]), new SpotifyResultHandler(m, event, playlist)));
                        }
                    } catch (Exception e) {
                        LoggerFactory.getLogger("MusicBot").error("Failed to load spotify "+noun+" from: " + args, e);
                        event.reply(bot.getError(event)+" Failed to load spotify "+noun+"... `["+spotifyUrl.id+"]`");
                    }
                }
            }
        } else {
            event.reply(bot.getLoading(event)+" Loading... `["+args+"]`", m -> bot.getPlayerManager().loadItemOrdered(event.getGuild(), args, new ResultHandler(m,event,false)));
        }
    }
    
    /**
     * A proxy for Message.editMessage requests to help with discord rate limits.
     * 
     * Only queues one editMessage request at a time. Drops redundant
     * requests if mulitple are made while a request is active, which
     * can happen during editMessage rate limiting.
     */
    private class FrequentMessageEditAgent { 
        private final Message m;
        private boolean editInProgress;
        private String queuedNewText;

        private FrequentMessageEditAgent(Message m) {
            this.m = m;
            this.editInProgress = false;
            this.queuedNewText = null;
        }

        private synchronized void handleEditComplete() {
            String queuedNewText = this.queuedNewText;
            if (queuedNewText == null) {
                editInProgress = false;
            } else {
                this.m.editMessage(queuedNewText).queue((_m) -> {
                    this.handleEditComplete();
                });
                this.queuedNewText = null;
            }
        }

        private synchronized void queueEditMessage(String newText) {
            if (newText == null) return;

            if (!editInProgress) {
                this.m.editMessage(newText).queue((_m) -> {
                    this.handleEditComplete();
                });
                this.editInProgress = true;
            } else {
                this.queuedNewText = newText;
            }
        }
    }

    private class ResultHandler implements AudioLoadResultHandler
    {
        protected final Message m;
        protected final CommandEvent event;
        protected final boolean ytsearch;
        protected final FrequentMessageEditAgent frequentEditAgent;
        
        private ResultHandler(Message m, CommandEvent event, boolean ytsearch)
        {
            this(m, event, ytsearch, new FrequentMessageEditAgent(m));
        }

        private ResultHandler(Message m, CommandEvent event, boolean ytsearch, FrequentMessageEditAgent frequentEditAgent)
        {
            this.m = m;
            this.event = event;
            this.ytsearch = ytsearch;
            this.frequentEditAgent = frequentEditAgent;
        }
        
        protected void loadSingle(AudioTrack track, AudioPlaylist playlist)
        {
            if(bot.getConfig().isTooLong(track))
            {
                frequentEditAgent.queueEditMessage(FormatUtil.filter(bot.getWarning(event)+" This track (**"+track.getInfo().title+"**) is longer than the allowed maximum: `"
                        +FormatUtil.formatTime(track.getDuration())+"` > `"+FormatUtil.formatTime(bot.getConfig().getMaxSeconds()*1000)+"`"));
                return;
            }
            AudioHandler handler = (AudioHandler)event.getGuild().getAudioManager().getSendingHandler();
            int pos = handler.addTrack(new QueuedTrack(track, event.getAuthor()))+1;
            String addMsg = FormatUtil.filter(bot.getSuccess(event)+" Added **"+track.getInfo().title
                    +"** (`"+FormatUtil.formatTime(track.getDuration())+"`) "+(pos==0?"to begin playing":" to the queue at position "+pos));
            if(playlist==null || !event.getSelfMember().hasPermission(event.getTextChannel(), Permission.MESSAGE_ADD_REACTION))
                frequentEditAgent.queueEditMessage(addMsg);
            else
            {
                new ButtonMenu.Builder()
                        .setText(addMsg+"\n"+bot.getWarning(event)+" This track has a playlist of **"+playlist.getTracks().size()+"** tracks attached. Select "+LOAD+" to load playlist.")
                        .setChoices(LOAD, CANCEL)
                        .setEventWaiter(bot.getWaiter())
                        .setTimeout(30, TimeUnit.SECONDS)
                        .setAction(re ->
                        {
                            if(re.getName().equals(LOAD))
                                frequentEditAgent.queueEditMessage(addMsg+"\n"+bot.getSuccess(event)+" Loaded **"+loadPlaylist(playlist, track)+"** additional tracks!");
                            else
                                frequentEditAgent.queueEditMessage(addMsg);
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
                if(playlist.getTracks().size() == 0)
                {
                    m.editMessage(FormatUtil.filter(event.getClient().getWarning()+" The playlist "+(playlist.getName()==null ? "" : "(**"+playlist.getName()
                            +"**) ")+" could not be loaded or contained 0 entries")).queue();
                }
                else if(count==0)
                {
                    frequentEditAgent.queueEditMessage(FormatUtil.filter(bot.getWarning(event)+" All entries in this playlist "+(playlist.getName()==null ? "" : "(**"+playlist.getName()
                            +"**) ")+"were longer than the allowed maximum (`"+bot.getConfig().getMaxTime()+"`)"));
                }
                else
                {
                    frequentEditAgent.queueEditMessage(FormatUtil.filter(bot.getSuccess(event)+" Found "
                            +(playlist.getName()==null?"a playlist":"playlist **"+playlist.getName()+"**")+" with `"
                            + playlist.getTracks().size()+"` entries; added to the queue!"
                            + (count<playlist.getTracks().size() ? "\n"+bot.getWarning(event)+" Tracks longer than the allowed maximum (`"
                            + bot.getConfig().getMaxTime()+"`) have been omitted." : "")));
                }
            }
        }

        @Override
        public void noMatches()
        {
            if(ytsearch)
                frequentEditAgent.queueEditMessage(FormatUtil.filter(bot.getWarning(event)+" No results found for `"+event.getArgs()+"`."));
            else
                bot.getPlayerManager().loadItemOrdered(event.getGuild(), "ytsearch:"+event.getArgs(), new ResultHandler(m,event,true));
        }

        @Override
        public void loadFailed(FriendlyException throwable)
        {
            if(throwable.severity==Severity.COMMON)
                frequentEditAgent.queueEditMessage(bot.getError(event)+" Error loading: "+throwable.getMessage());
            else
                frequentEditAgent.queueEditMessage(bot.getError(event)+" Error loading track.");
        }
    }
    
    private static String getYoutubeQueryOfTrack(SpotifyTrack track) {
        return String.format("%s - %s", String.join(", ", track.artists), track.name);
    }

    private class SpotifyResultHandler extends ResultHandler {
        
        private final SpotifyPlaylist playlist;
        private final int iTrack;
        private final String prevMessageText;
        private final int prevErrors;
        private final FrequentMessageEditAgent frequentEditAgent;

        private SpotifyResultHandler(Message m, CommandEvent event, SpotifyTrack track){
            this(m, event, new SpotifyPlaylist(track.name, new SpotifyTrack[] { track }));
        }

        private SpotifyResultHandler(Message m, CommandEvent event, SpotifyPlaylist playlist) {
            this(m, event, playlist, 0, "", 0, new FrequentMessageEditAgent(m));
        }

        private SpotifyResultHandler(Message m, CommandEvent event, SpotifyPlaylist playlist, int iTrack, String prevMessageText, int prevErrors, FrequentMessageEditAgent frequentEditAgent)
        {
            super(m, event, true);
            this.playlist = playlist;
            this.iTrack = iTrack;
            this.prevMessageText = prevMessageText;
            this.prevErrors = prevErrors;
            this.frequentEditAgent = frequentEditAgent;
        }

        protected void loadSingle(AudioTrack track, AudioPlaylist UNUSED)
        {
            if (iTrack == 0) {
                if (track == null) {
                    frequentEditAgent.queueEditMessage(FormatUtil.filter(bot.getError(event)+" Couldn't find youtube link for "+(playlist.tracks.length == 1 ? "track" : "first track")+": "+playlist.tracks[0].name));
                    return;
                }
                if(bot.getConfig().isTooLong(track))
                {
                    frequentEditAgent.queueEditMessage(FormatUtil.filter(bot.getWarning(event)+" This track (**"+track.getInfo().title+"**) is longer than the allowed maximum: `"
                            +FormatUtil.formatTime(track.getDuration())+"` > `"+FormatUtil.formatTime(bot.getConfig().getMaxSeconds()*1000)+"`"));
                    return;
                }
                AudioHandler handler = (AudioHandler)event.getGuild().getAudioManager().getSendingHandler();
                int pos = handler.addTrack(new QueuedTrack(track, event.getAuthor()))+1;
                String addMsg = FormatUtil.filter(bot.getSuccess(event)+" Added **"+track.getInfo().title
                        +"** (`"+FormatUtil.formatTime(track.getDuration())+"`) "+(pos==0?"to begin playing":" to the queue at position "+pos));
                if(playlist.tracks.length == 1 || !event.getSelfMember().hasPermission(event.getTextChannel(), Permission.MESSAGE_ADD_REACTION))
                    frequentEditAgent.queueEditMessage(addMsg);
                else
                {
                    new ButtonMenu.Builder()
                            .setText(addMsg+"\n"+bot.getWarning(event)+" [**"+playlist.name+"**] has **"+playlist.tracks.length+"** tracks attached. Select "+LOAD+" to add the rest to the queue.")
                            .setChoices(LOAD, CANCEL)
                            .setEventWaiter(bot.getWaiter())
                            .setTimeout(30, TimeUnit.SECONDS)
                            .setAction(re ->
                            {
                                if(re.getName().equals(LOAD)) {
                                    String newMessageText = addMsg+"\n"+bot.getSuccess(event)+" Loading **1/"+(playlist.tracks.length - 1)+"** additional tracks...";
                                    frequentEditAgent.queueEditMessage(newMessageText);
                                    bot.getPlayerManager().loadItemOrdered(event.getGuild(), "ytsearch:"+getYoutubeQueryOfTrack(playlist.tracks[1]), new SpotifyResultHandler(m,event,playlist,1,newMessageText,prevErrors, frequentEditAgent));
                                }
                                else
                                    frequentEditAgent.queueEditMessage(addMsg);
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
                    newMessageText = prevMessageText+"\n"+FormatUtil.filter(bot.getError(event)+" Couldn't find youtube link for track " + (iTrack + 1) + " of " + playlist.tracks.length + ": "+playlist.tracks[iTrack].name);
                    newErrors++;
                    frequentEditAgent.queueEditMessage(newMessageText);
                }
                else if(bot.getConfig().isTooLong(track))
                {
                    newMessageText = prevMessageText+"\n"+FormatUtil.filter(bot.getWarning(event)+" This track (**"+track.getInfo().title+"**) is longer than the allowed maximum: `"
                        +FormatUtil.formatTime(track.getDuration())+"` > `"+FormatUtil.formatTime(bot.getConfig().getMaxSeconds()*1000)+"`");
                    newErrors++;
                    frequentEditAgent.queueEditMessage(newMessageText);
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
                if (iTrack < playlist.tracks.length - 1) {
                    // update second line ("loading tracks...")
                    newMessageText = firstLine+"\n"+(bot.getSuccess(event)+" Loading **" + (iTrack + 1) + "/"+(playlist.tracks.length - 1)+"** additional tracks...")+(remainingParts.length == 0 ? "" : "\n"+String.join("\n", remainingParts));
                    frequentEditAgent.queueEditMessage(newMessageText);
                    bot.getPlayerManager().loadItemOrdered(event.getGuild(), "ytsearch:"+getYoutubeQueryOfTrack(playlist.tracks[iTrack + 1]), new SpotifyResultHandler(m,event,playlist,iTrack + 1,newMessageText,newErrors,frequentEditAgent));
                } else {
                    // all done! finalize message
                    // remove the second line ("loading tracks...") but keep everything else
                    newMessageText = String.format("%s\n%s",
                        firstLine + (remainingParts.length == 0 ? "" : "\n"+String.join("\n", remainingParts)),
                        bot.getSuccess(event)+" Loaded **"+(playlist.tracks.length - newErrors - 1)+"** additional tracks!"
                    );
                    frequentEditAgent.queueEditMessage(newMessageText);
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
                loadSingle(single, null);
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
                bot.getPlayerManager().loadItemOrdered(event.getGuild(), "ytsearch:"+getYoutubeQueryOfTrack(playlist.tracks[iTrack]), new SpotifyResultHandler(m,event,playlist,iTrack + 1,prevMessageText,prevErrors,frequentEditAgent));
        }

        @Override
        public void loadFailed(FriendlyException throwable)
        {
            LoggerFactory.getLogger("MusicBot").error("Load failed for: " + playlist.tracks[iTrack].name, throwable);
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
                event.reply(bot.getError(event)+" Please include a playlist name.");
                return;
            }
            Playlist playlist = bot.getPlaylistLoader().getPlaylist(event.getArgs());
            if(playlist==null)
            {
                event.reply(bot.getError(event)+"I could not find `"+event.getArgs()+".txt` in the Playlists folder.");
                return;
            }
            event.getChannel().sendMessage(bot.getLoading(event)+" Loading playlist **"+event.getArgs()+"**... ("+playlist.getItems().size()+" items)").queue(m -> 
            {
                AudioHandler handler = (AudioHandler)event.getGuild().getAudioManager().getSendingHandler();
                playlist.loadTracks(bot.getPlayerManager(), (at)->handler.addTrack(new QueuedTrack(at, event.getAuthor())), () -> {
                    StringBuilder builder = new StringBuilder(playlist.getTracks().isEmpty() 
                            ? bot.getWarning(event)+" No tracks were loaded!" 
                            : bot.getSuccess(event)+" Loaded **"+playlist.getTracks().size()+"** tracks!");
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
