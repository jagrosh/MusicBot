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
package com.jagrosh.jmusicbot.commands;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException.Severity;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.playlist.Playlist;
import com.jagrosh.jmusicbot.utils.FormatUtil;
import net.dv8tion.jda.core.entities.Message;

/**
 *
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class PlayCmd extends MusicCommand {

    public PlayCmd(Bot bot)
    {
        super(bot);
        this.name = "play";
        this.arguments = "<title|URL|subcommand>";
        this.help = "plays the provided song";
        this.beListening = true;
        this.bePlaying = false;
        this.children = new Command[]{new PlaylistCmd(bot)};
    }

    @Override
    public void doCommand(CommandEvent event) {
        if(event.getArgs().isEmpty())
        {
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
                : event.getArgs();
        event.getChannel().sendMessage("\u231A Loading... `["+args+"]`").queue(m -> {
            bot.getAudioManager().loadItemOrdered(event.getGuild(), args, new ResultHandler(m,event,false));
        });
    }
    
    private class ResultHandler implements AudioLoadResultHandler {
        final Message m;
        final CommandEvent event;
        final boolean ytsearch;
        private ResultHandler(Message m, CommandEvent event, boolean ytsearch)
        {
            this.m = m;
            this.event = event;
            this.ytsearch = ytsearch;
        }
        
        @Override
        public void trackLoaded(AudioTrack track) {
            int pos = bot.queueTrack(event, track)+1;
            m.editMessage(event.getClient().getSuccess()+" Added **"+track.getInfo().title
                    +"** (`"+FormatUtil.formatTime(track.getDuration())+"`) "+(pos==0 ? "to begin playing" 
                        : " to the queue at position "+pos)).queue();
        }

        @Override
        public void playlistLoaded(AudioPlaylist playlist) {
            if(playlist.getTracks().size()==1 || playlist.isSearchResult() || playlist.getSelectedTrack()!=null)
            {
                AudioTrack single = playlist.getSelectedTrack()==null?playlist.getTracks().get(0):playlist.getSelectedTrack();
                int pos = bot.queueTrack(event, single)+1;
                m.editMessage(event.getClient().getSuccess()+" Added **"+single.getInfo().title
                    +"** (`"+FormatUtil.formatTime(single.getDuration())+"`) "+(pos==0 ? "to begin playing" 
                        : " to the queue at position "+pos)).queue();
            }
            else
            {
                m.editMessage(event.getClient().getSuccess()+" Found "
                        +(playlist.getName()==null?"a playlist":"playlist **"+playlist.getName()+"**")+" with `"
                        +playlist.getTracks().size()+"` entries; added to the queue!").queue();
                playlist.getTracks().stream().forEach((track) -> {
                    bot.queueTrack(event, track);
                });
            }
        }

        @Override
        public void noMatches() {
            if(ytsearch)
                m.editMessage(event.getClient().getWarning()+" No results found for `"+event.getArgs()+"`.").queue();
            else
                bot.getAudioManager().loadItemOrdered(event.getGuild(), "ytsearch:"+event.getArgs(), new ResultHandler(m,event,true));
        }

        @Override
        public void loadFailed(FriendlyException throwable) {
            if(throwable.severity==Severity.COMMON)
                m.editMessage(event.getClient().getError()+" Error loading: "+throwable.getMessage()).queue();
            else
                m.editMessage(event.getClient().getError()+" Error loading track.").queue();
        }
    }
    
    public class PlaylistCmd extends MusicCommand {

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
        public void doCommand(CommandEvent event) {
            if(event.getArgs().isEmpty())
            {
                event.reply(event.getClient().getError()+" Please include a playlist name.");
                return;
            }
            Playlist playlist = Playlist.loadPlaylist(event.getArgs());
            if(playlist==null)
            {
                event.reply(event.getClient().getError()+" I could not find `"+event.getArgs()+".txt` in the Playlists folder.");
                return;
            }
            event.getChannel().sendMessage("\u231A Loading playlist **"+event.getArgs()+"**...").queue(m -> {
                playlist.loadTracks(bot.getAudioManager(), () -> {
                    StringBuilder builder = new StringBuilder(playlist.getTracks().isEmpty() 
                            ? event.getClient().getWarning()+" No tracks were loaded!" 
                            : event.getClient().getSuccess()+" Loaded **"+playlist.getTracks().size()+"** tracks!");
                    if(!playlist.getErrors().isEmpty())
                        builder.append("\nThe following tracks failed to load:");
                    playlist.getErrors().forEach(err -> builder.append("\n`[").append(err.getIndex()+1).append("]` **").append(err.getItem()).append("**: ").append(err.getReason()));
                    String str = builder.toString();
                    if(str.length()>2000)
                        str = str.substring(0,1994)+" (...)";
                    m.editMessage(str).queue();
                    playlist.getTracks().forEach(track -> bot.queueTrack(event, track));
                });
            });
        }
    }
}
