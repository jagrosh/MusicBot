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

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jmusicbot.Bot;

/**
 *
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class RepeatCmd extends Command {

    private final Bot bot;
    public RepeatCmd(Bot bot)
    {
        this.bot = bot;
        this.name = "repeat";
        this.help = "re-adds music to the queue when finished";
        this.arguments = "[on|off]";
        this.guildOnly = true;
        this.category = bot.DJ;
    }
    
    @Override
    protected void execute(CommandEvent event) {
        boolean value;
        if(event.getArgs().isEmpty())
        {
            value = !bot.getSettings(event.getGuild()).getRepeatMode();
        }
        else if(event.getArgs().equalsIgnoreCase("true") || event.getArgs().equalsIgnoreCase("on"))
        {
            value = true;
        }
        else if(event.getArgs().equalsIgnoreCase("false") || event.getArgs().equalsIgnoreCase("off"))
        {
            value = false;
        }
        else
        {
            event.replyError("Valid options are `on` or `off` (or leave empty to toggle)");
            return;
        }
        bot.setRepeatMode(event.getGuild(), value);
        event.replySuccess("Repeat mode is now `"+(value ? "ON" : "OFF")+"`");
    }
    
}
