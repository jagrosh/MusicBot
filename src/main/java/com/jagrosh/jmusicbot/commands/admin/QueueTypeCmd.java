/*
 * Copyright 2022 John Grosh <john.a.grosh@gmail.com>.
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
package com.jagrosh.jmusicbot.commands.admin;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.audio.AudioHandler;
import com.jagrosh.jmusicbot.commands.AdminCommand;
import com.jagrosh.jmusicbot.settings.QueueType;
import com.jagrosh.jmusicbot.settings.Settings;

/**
 *
 * @author Wolfgang Schwendtbauer
 */
public class QueueTypeCmd extends AdminCommand
{
    public QueueTypeCmd(Bot bot)
    {
        super();
        this.name = "queuetype";
        this.help = "changes the queue type";
        this.arguments = "[" + String.join("|", QueueType.getNames()) + "]";
        this.aliases = bot.getConfig().getAliases(this.name);
    }

    @Override
    protected void execute(CommandEvent event)
    {
        String args = event.getArgs();
        QueueType value;
        Settings settings = event.getClient().getSettingsFor(event.getGuild());

        if (args.isEmpty())
        {
            QueueType currentType = settings.getQueueType();
            event.reply(currentType.getEmoji() + " Current queue type is: `" + currentType.getUserFriendlyName() + "`.");
            return;
        }

        try
        {
            value = QueueType.valueOf(args.toUpperCase());
        }
        catch (IllegalArgumentException e)
        {
            event.replyError("Invalid queue type. Valid types are: [" + String.join("|", QueueType.getNames()) + "]");
            return;
        }

        if (settings.getQueueType() != value)
        {
            settings.setQueueType(value);

            AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
            if (handler != null)
                handler.setQueueType(value);
        }

        event.reply(value.getEmoji() + " Queue type was set to `" + value.getUserFriendlyName() + "`.");
    }
}
