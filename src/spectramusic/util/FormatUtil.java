
/*
 * Copyright 2016 jagrosh.
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
package spectramusic.util;

import java.util.List;
import net.dv8tion.jda.JDA;
import net.dv8tion.jda.entities.Role;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.entities.VoiceChannel;
import net.dv8tion.jda.player.source.AudioInfo;
import net.dv8tion.jda.player.source.AudioTimestamp;
import spectramusic.SpConst;
import spectramusic.entities.ClumpedMusicPlayer;

/**
 *
 * @author John Grosh (jagrosh)
 */
public class FormatUtil {
    
    public static String formattedAudio(ClumpedMusicPlayer player, JDA jda, boolean inTopic)
    {
        if(player.getCurrentAudioSource()==null)
        {
            return "No music playing\n\u23F9 "+progressBar(-1)+" "+volumeIcon(player.getVolume());
        }
        else
        {
            AudioInfo info = player.getCurrentAudioSource().getInfo();
            AudioTimestamp currentTime = player.getCurrentTimestamp();
            User user = jda.getUserById(player.getCurrentRequestor());
            String title = info.getError()==null ? info.getTitle() : "Error! Source: "+player.getCurrentAudioSource().getSource();
            if(inTopic && title.length()>40)
                title = title.substring(0,37)+"...";
            double progress = info.getError()==null && !info.isLive() ? (double)currentTime.getTotalSeconds() / info.getDuration().getTotalSeconds() : 0;
            String str = "**"+title+"** ~ "+(user==null ? "???" : user.getUsername())+"\n\u25B6 "+progressBar(progress)
                    +" "+(inTopic ? "" : "`")+"["+currentTime.getTimestamp() + "/" 
                    + (info.getError()==null ? info.getDuration().getTimestamp() : "???")+"]"+(inTopic ? "" : "`")+" "
                    +volumeIcon(player.getVolume());
            return str;
        }
    }
    
    private static String progressBar(double percent)
    {
        String str = "";
        for(int i=0; i<10; i++)
            if(i == (int)(percent*10))
                str+="\uD83D\uDD18";
            else
                str+="▬";
        return str;
    }
    
    public static String volumeIcon(double percent)
    {
        if(percent == 0)
            return "\uD83D\uDD07";
        if(percent < .25)
            return "\uD83D\uDD08";
        if(percent < .5)
            return "\uD83D\uDD09";
        return "\uD83D\uDD0A";
    }
    
    public static String listOfTChannels(List<TextChannel> list, String query)
    {
        String out = String.format(SpConst.MULTIPLE_FOUND, "text channels", query);
        for(int i=0; i<6 && i<list.size(); i++)
            out+="\n - "+list.get(i).getName()+" (<#"+list.get(i).getId()+">)";
        if(list.size()>6)
            out+="\n**And "+(list.size()-6)+" more...**";
        return out;
    }
    
    public static String listOfVChannels(List<VoiceChannel> list, String query)
    {
        String out = String.format(SpConst.MULTIPLE_FOUND, "text channels", query);
        for(int i=0; i<6 && i<list.size(); i++)
            out+="\n - "+list.get(i).getName()+" (<#"+list.get(i).getId()+">)";
        if(list.size()>6)
            out+="\n**And "+(list.size()-6)+" more...**";
        return out;
    }
    
    public static String listOfRoles(List<Role> list, String query)
    {
        String out = String.format(SpConst.MULTIPLE_FOUND, "roles", query);
        for(int i=0; i<6 && i<list.size(); i++)
            out+="\n - "+list.get(i).getName()+" (ID:"+list.get(i).getId()+")";
        if(list.size()>6)
            out+="\n**And "+(list.size()-6)+" more...**";
        return out;
}
}
