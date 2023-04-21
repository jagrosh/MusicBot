/*
 * Copyright 2019 John Grosh <john.a.grosh@gmail.com>.
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
 * @author Joshua L.
 */
public class ShuffleAllCmd extends DJCommand
{
    public ShuffleAllCmd(Bot bot)
    {
        super(bot);
        this.name = "shuffleall";
        this.help = "shuffles all songs in the queue";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.bePlaying = true;
    }

    @Override
    public void doCommand(CommandEvent event) 
    {
        AudioHandler handler = (AudioHandler)event.getGuild().getAudioManager().getSendingHandler();
        int s = handler.getQueue().shuffle(0, true);
        switch (s) 
        {
            case 0:
                event.replyError("There isn't any music in the queue to shuffle!");
                break;
            case 1:
                event.replyWarning("There is only one song in the queue!");
                break;
            default:
                event.replySuccess("You successfully shuffled "+s+" entries.");
                break;
        }
    }
}
