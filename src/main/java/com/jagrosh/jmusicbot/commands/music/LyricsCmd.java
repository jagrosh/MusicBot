/*
 * Copyright 2018 John Grosh <john.a.grosh@gmail.com>.
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
import com.jagrosh.jlyrics.Lyrics;
import com.jagrosh.jlyrics.LyricsClient;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.audio.AudioHandler;
import com.jagrosh.jmusicbot.commands.MusicCommand;
import java.util.concurrent.ExecutionException;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;

/**
 *
 * @author John Grosh (john.a.grosh@gmail.com)
 */
public class LyricsCmd extends MusicCommand
{
    private final LyricsClient client = new LyricsClient();
    
    public LyricsCmd(Bot bot)
    {
        super(bot);
        this.name = "lyrics";
        this.help = "shows the lyrics to the currently-playing song";
        this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
        this.bePlaying = true;
    }

    @Override
    public void doCommand(CommandEvent event)
    {
        String title = ((AudioHandler)event.getGuild().getAudioManager().getSendingHandler()).getPlayer().getPlayingTrack().getInfo().title;
        Lyrics lyrics;
        try
        {
            lyrics = client.getLyrics(title).get();
        }
        catch(InterruptedException | ExecutionException ex)
        {
            lyrics = null;
        }
        
        if(lyrics == null)
        {
            event.replyError("Lyrics for `" + title + "` could not be found!");
            return;
        }
        
        event.reply(new EmbedBuilder().setColor(event.getSelfMember().getColor())
                .setAuthor(lyrics.getAuthor())
                .setTitle(lyrics.getTitle(), lyrics.getURL())
                .setDescription(lyrics.getContent()).build());
    }
}
