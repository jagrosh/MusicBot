/*
 * Copyright 2021 John Grosh <john.a.grosh@gmail.com>.
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
import com.jagrosh.jmusicbot.commands.AdminCommand;
import com.jagrosh.jmusicbot.settings.Settings;

/**
 *
 * @author Brian Kendall
 */
public class FairqueueCmd extends AdminCommand
{
    public FairqueueCmd(Bot bot)
    {
        this.name = "fairqueue";
        this.help = "turns fair queue on or off";
        this.arguments = "[off|on]";
        this.aliases = bot.getConfig().getAliases(this.name);
    }
    
    @Override
    protected void execute(CommandEvent event) 
    {
        String args = event.getArgs();
        boolean value = true;
        Settings settings = event.getClient().getSettingsFor(event.getGuild());
        if(args.isEmpty())
        {
            value = !settings.getUseFairQueue();
        }
        else if(args.equalsIgnoreCase("false") || args.equalsIgnoreCase("off"))
        {
            value = false;
        }
        else if(args.equalsIgnoreCase("true") || args.equalsIgnoreCase("on"))
        {
            value = true;
        }
        else
        {
            event.replyError("Valid options are `off` or `on` (or leave empty to toggle between `off` and `on`)");
            return;
        }
        settings.setUseFairQueue(value);
        event.replySuccess("Fair queue is now `"+(value ? "on" : "off")+"`");
    }
}
