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
import com.jagrosh.jmusicbot.utils.FormatUtil;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.util.regex.Pattern;


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
        this.arguments = "<HH:MM:SS>|<MM:SS>|<SS>";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.beListening = true;
        this.bePlaying = true;
    }

    @Override
    public void doCommand(CommandEvent event)
    {
        AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
        if (!handler.getPlayer().getPlayingTrack().isSeekable())
        {
            event.replyError("This track is not seekable.");
            return;
        }

        AudioTrack currentTrack = handler.getPlayer().getPlayingTrack();
        if (!DJCommand.checkDJPermission(event))
        {
            event.replyError("You cannot seek **" + currentTrack.getInfo().title + "** because you didn't add it!");
            return;
        }

        String args = event.getArgs();
        long track_duration = handler.getPlayer().getPlayingTrack().getDuration();
        int seek_milliseconds = 0;
        int seconds;
        int minutes = 0;
        int hours = 0;

        if (Pattern.matches("^(\\d\\d):([0-5]\\d):([0-5]\\d)$", args))
        {
            hours = Integer.parseInt(args.substring(0, 2));
            minutes = Integer.parseInt(args.substring(3, 5));
            seconds = Integer.parseInt(args.substring(6));
        }
        else if (Pattern.matches("^([0-5]\\d):([0-5]\\d)$", args))
        {
            minutes = Integer.parseInt(args.substring(0, 2));
            seconds = Integer.parseInt(args.substring(3, 5));
        }
        else if (Pattern.matches("^([0-5]\\d)$", args))
        {
            seconds = Integer.parseInt(args.substring(0, 2));
        }
        else
        {
            event.replyError("Invalid seek!");
            return;
        }

        seek_milliseconds += hours * 3600000 + minutes * 60000 + seconds * 1000;
        if (seek_milliseconds > track_duration)
        {
            event.replyError("Current track (`" + FormatUtil.formatTime(track_duration) + "`) is not that long!");
            return;
        }

        handler.getPlayer().getPlayingTrack().setPosition(seek_milliseconds);
        event.replySuccess("Successfully seeked!");
    }


}