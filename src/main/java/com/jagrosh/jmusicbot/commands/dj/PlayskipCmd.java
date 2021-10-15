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
package com.jagrosh.jmusicbot.commands.dj;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.audio.AudioHandler;
import com.jagrosh.jmusicbot.commands.DJCommand;

/**
 *
 * @author Gobidev (Adrian Groh) (adrian.groh@t-online.de)
 */
public class PlayskipCmd extends DJCommand
{
    private final String loadingEmoji;

    public PlayskipCmd(Bot bot)
    {
        super(bot);
        this.loadingEmoji = bot.getConfig().getLoading();
        this.name = "playskip";
        this.arguments = "<title|URL>";
        this.help = "plays a single song next and skips to it";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.beListening = true;
        this.bePlaying = false;
    }

    @Override
    public void doCommand(CommandEvent event)
    {
        // Add song to front of queue
        PlaynextCmd playnextCmd = new PlaynextCmd(bot);
        playnextCmd.doCommand(event);

        // Skip current song if music is playing
        AudioHandler handler = (AudioHandler)event.getGuild().getAudioManager().getSendingHandler();
        assert handler != null;
        if (handler.isMusicPlaying(event.getJDA())) {
            ForceskipCmd forceskipCmd = new ForceskipCmd(bot);
            forceskipCmd.doCommand(event);
        }
    }
}
