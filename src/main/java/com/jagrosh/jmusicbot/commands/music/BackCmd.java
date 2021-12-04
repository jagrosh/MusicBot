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
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.audio.AudioHandler;
import com.jagrosh.jmusicbot.commands.MusicCommand;
import com.jagrosh.jmusicbot.utils.FormatUtil;
import net.dv8tion.jda.api.entities.Message;

/**
 *
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class BackCmd extends MusicCommand
{   
	/**
	 * Loading emoji
	 */
    private final String loadingEmoji;
    
	/**
	 * Constructor for back command
	 * 
	 * @param bot input bot
	 */
    public BackCmd(final Bot bot)
    {
        super(bot);
        this.loadingEmoji = bot.getConfig().getLoading();
        this.name = "back";
        this.help = "plays previous song or adds it to the back of queue";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.beListening = true;
        this.bePlaying = false;
    }

    /**
     * Perform the back command
     * 
     * @param event Command event
     */
    @Override
    public void doCommand(final CommandEvent event) 
    {
        event.reply(loadingEmoji+" Loading... ", m -> bot.getPlayerManager().loadItemOrdered(event.getGuild(), "", new ResultHandler(m,event, false)));
    }

    /**
    * Handles results
    */
    private class ResultHandler implements AudioLoadResultHandler
    {
    	/**
    	 * Message to be replied
    	 */
        private final Message message;
        /**
         * Event being handled
         */
        private final CommandEvent event;
        /**
         * Youtube search
         */
        private final boolean ytsearch;
        
        /**
         * Constructor for result handler
         * 
         * @param message Message
         * @param event Event
         * @param ytsearch Youtube search
         */
        private ResultHandler(final Message message, final CommandEvent event, final boolean ytsearch)
        {
            this.message = message;
            this.event = event;
            this.ytsearch = ytsearch;
        }
        
        /**
         * Load previous track
         */
        private void loadSingle()
        {
            final AudioHandler handler = (AudioHandler)event.getGuild().getAudioManager().getSendingHandler();
            final AudioTrack previousTrack = handler.backTrack();
            final String addMsg = FormatUtil.filter(event.getClient().getSuccess()+" Added **"+previousTrack.getInfo().title
                    +"** (`"+FormatUtil.formatTime(previousTrack.getDuration())+"`) "+" back to the end of the queue");
            message.editMessage(addMsg).queue();
        }

        /**
         * Track loaded
         * 
         * @param track
         */
        @Override
        public void trackLoaded(final AudioTrack track)
        {
            loadSingle();
        }

        /**
         * Playlist loaded
         * 
         * @param playlist
         */
        @Override
        public void playlistLoaded(final AudioPlaylist playlist)
        {
            loadSingle();
        }

        /**
         * No match but still load previous track
         */
        @Override
        public void noMatches()
        {
            loadSingle();
        }

        /**
         * Load failed
         * 
         * @param throwable
         */
        @Override
        public void loadFailed(final FriendlyException throwable)
        {
            if(throwable.severity==FriendlyException.Severity.COMMON) {
                message.editMessage(event.getClient().getError()+" Error loading: "+throwable.getMessage()).queue();
            }
            else {
                message.editMessage(event.getClient().getError()+" Error loading track.").queue();
            }
        }
    }

}