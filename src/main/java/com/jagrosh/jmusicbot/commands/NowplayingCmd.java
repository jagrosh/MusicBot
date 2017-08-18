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

import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.audio.AudioHandler;
import com.jagrosh.jmusicbot.utils.FormatUtil;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioTrack;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.User;

/**
 *
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class NowplayingCmd extends MusicCommand {

    public NowplayingCmd(Bot bot)
    {
        super(bot);
        this.name = "nowplaying";
        this.help = "shows the song that is currently playing";
        this.aliases = new String[]{"np","current"};
        this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
    }

    @Override
    public void doCommand(CommandEvent event) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(event.getSelfMember().getColor());
        AudioHandler ah = (AudioHandler)event.getGuild().getAudioManager().getSendingHandler();
        
        if(ah==null || !ah.isMusicPlaying())
        {
            eb.setTitle("No music playing");
            eb.setDescription("\u23F9 "+FormatUtil.progressBar(-1)+" "+FormatUtil.volumeIcon(ah==null?100:ah.getPlayer().getVolume()));
            event.reply(eb.build());
            return;
        }
        
        if(ah.getRequester()!=0)
        {
            User u = event.getJDA().getUserById(ah.getRequester());
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
        
        if(ah.getPlayer().getPlayingTrack() instanceof YoutubeAudioTrack)
            eb.setThumbnail("https://img.youtube.com/vi/"+ah.getPlayer().getPlayingTrack().getIdentifier()+"/maxresdefault.jpg");
        
        eb.setDescription(FormatUtil.embedformattedAudio(ah));
        
        event.reply(eb.build());
    }
    
}
