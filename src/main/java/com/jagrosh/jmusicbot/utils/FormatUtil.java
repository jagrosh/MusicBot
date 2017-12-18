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
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioTrack;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
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
    
    public static Message nowPlayingMessage(Guild guild, String successEmoji)
    {
        MessageBuilder mb = new MessageBuilder();
        mb.append(successEmoji+" **Now Playing...**");
        EmbedBuilder eb = new EmbedBuilder();
        AudioHandler ah = (AudioHandler)guild.getAudioManager().getSendingHandler();
        eb.setColor(guild.getSelfMember().getColor());
        if(ah==null || !ah.isMusicPlaying())
        {
            eb.setTitle("No music playing");
            eb.setDescription("\u23F9 "+FormatUtil.progressBar(-1)+" "+FormatUtil.volumeIcon(ah==null?100:ah.getPlayer().getVolume()));
        }
        else
        {
            if(ah.getRequester()!=0)
            {
                User u = guild.getJDA().getUserById(ah.getRequester());
                if(u==null)
                    eb.setAuthor("Unknown (ID:"+ah.getRequester()+")", null, null);
                else
                    eb.setAuthor(u.getName()+"#"+u.getDiscriminator(), null, u.getEffectiveAvatarUrl());
            }

            try {
                eb.setTitle(ah.getPlayer().getPlayingTrack().getInfo().title, ah.getPlayer().getPlayingTrack().getInfo().uri);
            } catch(Exception e) {
                eb.setTitle(ah.getPlayer().getPlayingTrack().getInfo().title);
            }

            if(!AudioHandler.USE_NP_REFRESH && ah.getPlayer().getPlayingTrack() instanceof YoutubeAudioTrack)
                eb.setThumbnail("https://img.youtube.com/vi/"+ah.getPlayer().getPlayingTrack().getIdentifier()+"/mqdefault.jpg");

            eb.setDescription(FormatUtil.embedFormat(ah));
        }
        return mb.setEmbed(eb.build()).build();
    }
    
    public static String topicFormat(AudioHandler handler, JDA jda)
    {
        if(handler==null)
            return "No music playing\n\u23F9 "+progressBar(-1)+" "+volumeIcon(100);
        else if (!handler.isMusicPlaying())
            return "No music playing\n\u23F9 "+progressBar(-1)+" "+volumeIcon(handler.getPlayer().getVolume());
        else
        {
            long userid = handler.getRequester();
            AudioTrack track = handler.getPlayer().getPlayingTrack();
            String title = track.getInfo().title;
            return "**"+title+"** ["+(userid==0 ? "autoplay" : "<@"+userid+">")+"]"
                    + "\n"+(handler.getPlayer().isPaused()?"\u23F8":"\u25B6")+" "
                    +"["+formatTime(track.getDuration())+"] "
                    +volumeIcon(handler.getPlayer().getVolume());
        }
    }
    
    public static String embedFormat(AudioHandler handler)
    {
        if(handler==null)
            return "No music playing\n\u23F9 "+progressBar(-1)+" "+volumeIcon(100);
        else if (!handler.isMusicPlaying())
            return "No music playing\n\u23F9 "+progressBar(-1)+" "+volumeIcon(handler.getPlayer().getVolume());
        else
        {
            AudioTrack track = handler.getPlayer().getPlayingTrack();
            double progress = (double)track.getPosition()/track.getDuration();
            return (handler.getPlayer().isPaused()?"\u23F8":"\u25B6")
                    +" "+progressBar(progress)
                    +" `["+formatTime(track.getPosition()) + "/" + formatTime(track.getDuration()) +"]` "
                    +volumeIcon(handler.getPlayer().getVolume());
        }
    }
        
    public static String progressBar(double percent)
    {
        String str = "";
        for(int i=0; i<12; i++)
            if(i == (int)(percent*12))
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
    
    public static String filter(String input)
    {
        return input.replace("@everyone", "@\u0435veryone").replace("@here", "@h\u0435re").trim();
    }
}
