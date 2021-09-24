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
package com.jagrosh.jmusicbot.commands.dj;

import java.time.Duration;

import javax.sound.midi.Track;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.audio.AudioHandler;
import com.jagrosh.jmusicbot.commands.DJCommand;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

/**
 *
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class SeekToCmd extends DJCommand 
{
    public SeekToCmd(Bot bot)
    {
        super(bot);
        this.name = "seekto";
        this.help = "seeks to the specified position in the currently playing song";
        this.arguments = "<position>";
        this.aliases = bot.getConfig().getAliases(this.name);
        //this.bePlaying = true;
    }

    @Override
    public void doCommand(CommandEvent event) 
    {
        String index = "";
        try
        {
            index = event.getArgs();
            if(index.indexOf(':') == -1){
                throw new NumberFormatException("Wrong format. Example: 5:18 (up to hours only).");
            }
            AudioHandler handler = (AudioHandler)event.getGuild().getAudioManager().getSendingHandler();
            String[] tokens = index.split(":");
            long total = 0;
            total += Integer.parseInt(tokens[tokens.length-1]) * 1000;
            total += Integer.parseInt(tokens[tokens.length-2]) * 60000;
            if(tokens.length==3){
                total += Integer.parseInt(tokens[tokens.length-3]) * 3600000;
            }
            
            AudioTrack track = handler.getPlayer().getPlayingTrack();
            long trackDuration = track.getDuration();
            long trackDurationInS = track.getDuration() / 1000;

            if(total<trackDuration || total<0){
                handler.seek(total);
            }else{
                throw new NumberFormatException("Track length: " + String.format("%d:%02d:%02d", trackDurationInS / 3600, (trackDurationInS % 3600) / 60, (trackDurationInS % 60)));
            }
        }
        catch(NumberFormatException e)
        {
            event.reply(event.getClient().getError()+" `"+event.getArgs()+"` is not a valid position! " + e.getMessage());
            return;
        }
    }
}
