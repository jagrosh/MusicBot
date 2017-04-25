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
package com.jagrosh.jmusicbot.utils;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import java.util.List;
import com.jagrosh.jmusicbot.audio.AudioHandler;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.VoiceChannel;

/**
 *
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class FormatUtil {
    
    public static String formatTime(long duration)
    {
        if(duration == Long.MAX_VALUE)
            return "LIVE";
        long seconds = Math.round(duration/1000.0);
        long hours = seconds/(60*60);
        seconds %= 60*60;
        long minutes = seconds/60;
        seconds %= 60;
        return (hours>0 ? hours+":" : "") + (minutes<10 ? "0"+minutes : minutes) + ":" + (seconds<10 ? "0"+seconds : seconds);
    }
    
    public static String formattedAudio(AudioHandler handler, JDA jda, boolean inTopic)
    {
        if(handler==null)
            return "No music playing\n\u23F9 "+progressBar(-1)+" "+volumeIcon(100);
        else if (handler.getCurrentTrack()==null)
            return "No music playing\n\u23F9 "+progressBar(-1)+" "+volumeIcon(handler.getPlayer().getVolume());
        else
        {
            String userid = handler.getCurrentTrack().getIdentifier();
            User user = jda.getUserById(userid);
            AudioTrack track = handler.getCurrentTrack().getTrack();
            String title = track.getInfo().title;
            if(inTopic && title.length()>30)
                title = title.substring(0,27)+"...";
            double progress = (double)track.getPosition()/track.getDuration();
            String str = "**"+title+"** ["+(user==null||inTopic ? (userid==null ? "autoplay" : "<@"+userid+">") : user.getName())+"]\n\u25B6 "+progressBar(progress)
                    +" "+(inTopic ? "" : "`")+"["+formatTime(track.getPosition()) + "/" + formatTime(track.getDuration())
                    +"]"+(inTopic ? "" : "`")+" " +volumeIcon(handler.getPlayer().getVolume())
                    +(inTopic ? "" : "\n**<"+track.getInfo().uri+">**");
            return str;
        }
    }
    
    private static String progressBar(double percent)
    {
        String str = "";
        for(int i=0; i<8; i++)
            if(i == (int)(percent*8))
                str+="\uD83D\uDD18";
            else
                str+="▬";
        return str;
    }
    
    public static String volumeIcon(int volume)
    {
        if(volume == 0)
            return "\uD83D\uDD07";
        if(volume < 30)
            return "\uD83D\uDD08";
        if(volume < 70)
            return "\uD83D\uDD09";
        return "\uD83D\uDD0A";
    }
    
    public static String listOfTChannels(List<TextChannel> list, String query)
    {
        String out = " Multiple text channels found matching \""+query+"\":";
        for(int i=0; i<6 && i<list.size(); i++)
            out+="\n - "+list.get(i).getName()+" (<#"+list.get(i).getId()+">)";
        if(list.size()>6)
            out+="\n**And "+(list.size()-6)+" more...**";
        return out;
    }
    public static String listOfVChannels(List<VoiceChannel> list, String query)
    {
        String out = " Multiple voice channels found matching \""+query+"\":";
        for(int i=0; i<6 && i<list.size(); i++)
            out+="\n - "+list.get(i).getName()+" (ID:"+list.get(i).getId()+")";
        if(list.size()>6)
            out+="\n**And "+(list.size()-6)+" more...**";
        return out;
    }
    public static String listOfRoles(List<Role> list, String query)
    {
        String out = " Multiple text channels found matching \""+query+"\":";
        for(int i=0; i<6 && i<list.size(); i++)
            out+="\n - "+list.get(i).getName()+" (ID:"+list.get(i).getId()+")";
        if(list.size()>6)
            out+="\n**And "+(list.size()-6)+" more...**";
        return out;
    }
}
