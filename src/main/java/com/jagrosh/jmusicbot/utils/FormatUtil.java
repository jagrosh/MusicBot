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
    
    public static boolean NO_PROGRESS_BAR_IN_TOPIC = false;
    
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
    
    public static String formattedAudio(AudioHandler handler, JDA jda)
    {
        if(handler==null)
            return "No music playing\n\u23F9 "+progressBar(-1)+" "+volumeIcon(100);
        else if (!handler.isMusicPlaying())
            return "No music playing\n\u23F9 "+progressBar(-1)+" "+volumeIcon(handler.getPlayer().getVolume());
        else
        {
            String userid = handler.getCurrentTrack().getIdentifier();
            AudioTrack track = handler.getCurrentTrack().getTrack();
            String title = track.getInfo().title;
            if(!NO_PROGRESS_BAR_IN_TOPIC && title.length()>30)
                title = title.substring(0,27)+"...";
            double progress = (double)track.getPosition()/track.getDuration();
            String str = "**"+title+"** ["+(userid==null ? "autoplay" : "<@"+userid+">")+"]";
            String str2 = "\n"+(handler.getPlayer().isPaused()?"\u23F8":"\u25B6")+" "
                    +(NO_PROGRESS_BAR_IN_TOPIC ? "["+formatTime(track.getDuration())+"] " :
                    progressBar(progress)+" ["+formatTime(track.getPosition()) + "/" + formatTime(track.getDuration())+"] ")
                    +volumeIcon(handler.getPlayer().getVolume());
            return str+str2;
        }
    }
    
    public static String embedformattedAudio(AudioHandler ah)
    {
        return (ah.getPlayer().isPaused()?"\u23F8":"\u25B6")+" "+progressBar((double)ah.getCurrentTrack().getTrack().getPosition()/ah.getCurrentTrack().getTrack().getDuration())
                +" `["+formatTime(ah.getCurrentTrack().getTrack().getPosition()) + "/" + formatTime(ah.getCurrentTrack().getTrack().getDuration()) +"]` "
                +volumeIcon(ah.getPlayer().getVolume());
    }
    
    public static String progressBar(double percent)
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
