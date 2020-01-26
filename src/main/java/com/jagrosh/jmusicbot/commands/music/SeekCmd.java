/*
 * Copyright 2020 John Grosh <john.a.grosh@gmail.com>.
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

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.audio.AudioHandler;
import com.jagrosh.jmusicbot.commands.DJCommand;
import com.jagrosh.jmusicbot.commands.MusicCommand;
import com.jagrosh.jmusicbot.utils.TimeUtil;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;


/**
 * @author Whew., Inc.
 */
public class SeekCmd extends MusicCommand
{
    public SeekCmd(Bot bot)
    {
        super(bot);
        this.name = "seek";
        this.help = "seeks the current song";
        this.arguments = "[+ | -] <HH:MM:SS | MM:SS | SS>";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.beListening = true;
        this.bePlaying = true;
    }

    @Override
    public void doCommand(CommandEvent event)
    {
        AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
        AudioTrack playingTrack = handler.getPlayer().getPlayingTrack();
        if (!playingTrack.isSeekable())
        {
            event.replyError("This track is not seekable.");
            return;
        }


        if (!DJCommand.checkDJPermission(event) && playingTrack.getUserData(Long.class) != event.getAuthor().getIdLong())
        {
            event.replyError("You cannot seek **" + playingTrack.getInfo().title + "** because you didn't add it!");
            return;
        }

        String args = event.getArgs();
        Boolean seek_relative = null; // seek forward or backward
        char charRelative = args.charAt(0);
        if (args.charAt(0) == '+' || args.charAt(0) == '-')
        {
            args = args.substring(1);
            seek_relative = charRelative == '+';

        }
        Long seek_milliseconds = TimeUtil.parseTime(args);
        if (seek_milliseconds == null)
        {
            event.replyError("Invalid seek! Expected format:" + arguments);
            return;
        }

        long track_duration = playingTrack.getDuration();
        long currentPosition = playingTrack.getPosition();
        if (seek_relative != null)
        {
            if ((seek_relative && (seek_milliseconds > (track_duration - currentPosition))) || (!seek_relative && (seek_milliseconds > currentPosition)))
            {
                event.replyError("You can't seek past the length of the track! (" + TimeUtil.formatTime(currentPosition) + "/" + TimeUtil.formatTime(track_duration) + ")");
                return;
            }
            playingTrack.setPosition(seek_relative ? currentPosition + seek_milliseconds : currentPosition - seek_milliseconds);
        }

        else if (seek_milliseconds > track_duration)
        {
            event.replyError("Current track (`" + TimeUtil.formatTime(track_duration) + "`) is not that long!");
            return;
        }
        else playingTrack.setPosition(seek_milliseconds);

        event.replySuccess("Successfully seeked to `" + TimeUtil.formatTime(playingTrack.getPosition()) + "/" + TimeUtil.formatTime(playingTrack.getDuration()) + "`!");
    }
}
