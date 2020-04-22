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
import com.jagrosh.jmusicbot.commands.DJCommand;
import com.jagrosh.jmusicbot.commands.music.QueueCmd;
import com.jagrosh.jmusicbot.settings.RepeatMode;
import com.jagrosh.jmusicbot.settings.Settings;

/**
 *
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class RepeatCmd extends DJCommand
{
    public RepeatCmd(Bot bot)
    {
        super(bot);
        this.name = "repeat";
        this.help = "replays the current song or re-adds music to the queue when finished";
        this.arguments = "[off|single|all]";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.guildOnly = true;
    }
    
    // override musiccommand's execute because we don't actually care where this is used
    @Override
    protected void execute(CommandEvent event) 
    {
        RepeatMode value;
        Settings settings = event.getClient().getSettingsFor(event.getGuild());
        if(event.getArgs().isEmpty())
        {
            event.reply(QueueCmd.REPEAT+" Current repeat setting is `"+settings.getRepeatMode()+"`.");
            return;
        }
        else if(event.getArgs().equalsIgnoreCase(RepeatMode.off.toString()))
        {
            value = RepeatMode.off;
        }
        else if(event.getArgs().equalsIgnoreCase(RepeatMode.single.toString()))
        {
            value = RepeatMode.single;
        }
        else if(event.getArgs().equalsIgnoreCase(RepeatMode.all.toString()))
        {
            value = RepeatMode.all;
        }
        else
        {
            event.replyError("Valid options are `off`, `single`, or `all`.");
            return;
        }
        settings.setRepeatMode(value);
        event.replySuccess("Repeat mode is now `"+value.toString()+"`.");
    }

    @Override
    public void doCommand(CommandEvent event) { /* Intentionally Empty */ }
}
