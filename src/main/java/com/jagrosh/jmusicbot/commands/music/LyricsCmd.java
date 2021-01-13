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
import com.jagrosh.jlyrics.LyricsClient;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.audio.AudioHandler;
import com.jagrosh.jmusicbot.commands.MusicCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;

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
        this.arguments = "[song name]";
        this.help = "shows the lyrics to the currently-playing song";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
        this.guildOnly = false;
        this.bePlaying = false;
    }

    @Override
    public void doCommand(CommandEvent event)
    {
        event.getChannel().sendTyping().queue();
        String title;
        if(event.getArgs().isEmpty())
        {
            if(event.getChannelType().isGuild())
                try {
                    title = ((AudioHandler) event.getGuild().getAudioManager().getSendingHandler()).getPlayer().getPlayingTrack().getInfo().title;
                } catch (NullPointerException e) {
                    event.replyError("Please enter the song name!");
                    return;
                }
            else {
                event.replyError("Please enter the song name!");
                return;
            }
        }
        else
            title = event.getArgs();
        client.getLyrics(title).thenAccept(lyrics ->
        {
            if(lyrics == null)
            {
                event.replyError("Lyrics for `" + title + "` could not be found!" + (event.getArgs().isEmpty() ? " Try entering the song name manually (`lyrics [song name]`)" : ""));
                return;
            }
            String content = lyrics.getContent().trim();
            if (content.length() > 15000) {
                event.replyWarning("Lyrics for `" + title + "` found but likely not correct: " + lyrics.getURL());
                return;
            }
            if(event.getChannelType().isGuild()) {
                EmbedBuilder eb = new EmbedBuilder()
                        .setAuthor(lyrics.getAuthor())
                        .setColor(event.getSelfMember().getColor())
                        .setTitle(lyrics.getTitle(), lyrics.getURL());
                if (content.length() > 2048) {
                    while (content.length() > 2048) {
                        int index = content.lastIndexOf("\n\n", 2048);
                        if (index == -1)
                            index = content.lastIndexOf("\n", 2048);
                        if (index == -1)
                            index = content.lastIndexOf(" ", 2048);
                        if (index == -1)
                            index = 2048;
                        event.reply(eb.setDescription(content.substring(0, index).trim()).build());
                        content = content.substring(index).trim();
                        eb.setAuthor(null).setTitle(null, null);
                    }
                    event.reply(eb.setFooter("Powered by " + lyrics.getSource()).setDescription(content.trim()).build());
                } else
                    event.reply(eb.setFooter("Powered by " + lyrics.getSource()).setDescription(content).build());
            }
            else {
                if (content.length() > 2000) {
                    while (content.length() > 2000) {
                        int index = content.lastIndexOf("\n\n", 2000);
                        if (index == -1)
                            index = content.lastIndexOf("\n", 2000);
                        if (index == -1)
                            index = content.lastIndexOf(" ", 2000);
                        if (index == -1)
                            index = 2000;
                        event.reply(content.substring(0, index).trim());
                        content = content.substring(index).trim();
                    }
                    event.reply(content.trim());
                } else
                    event.reply(content);
            }
        });
    }
}