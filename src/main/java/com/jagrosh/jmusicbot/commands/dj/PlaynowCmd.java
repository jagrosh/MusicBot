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
import com.jagrosh.jmusicbot.commands.PlayCommand;

/**
 *
 * @author Daniel Liljeberg (liljebergxyz@gmail.com)
 */
public class PlaynowCmd extends PlayCommand
{
    public PlaynowCmd(Bot bot, String loadingEmoji)
    {
        super(bot, loadingEmoji);
        this.name = "playnow";
        this.help = "plays a single song now";
    }
    
    @Override
    public void doCommand(CommandEvent event)
    {
        if(event.getArgs().isEmpty() && event.getMessage().getAttachments().isEmpty())
        {
            event.replyWarning("Please include a song title or URL!");
            return;
        }
        AudioHandler handler = (AudioHandler)event.getGuild().getAudioManager().getSendingHandler();
        event.reply(event.getClient().getSuccess()+" Skipped **"+handler.getPlayer().getPlayingTrack().getInfo().title+"**");
        String args = getLoadingArgs(event);
        event.reply(loadingEmoji+" Loading... `["+args+"]`", m -> bot.getPlayerManager().loadItemOrdered(event.getGuild(), args, new PlayResultHandler(m,event,false)));
    }
}
