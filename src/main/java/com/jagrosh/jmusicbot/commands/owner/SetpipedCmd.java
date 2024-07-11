/*
 * Copyright 2017 John Grosh <john.a.grosh@gmail.com>.
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
package com.jagrosh.jmusicbot.commands.owner;

import java.io.IOException;
import java.io.InputStream;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.commands.OwnerCommand;
import com.jagrosh.jmusicbot.utils.OtherUtil;
import net.dv8tion.jda.api.entities.Icon;

/**
 *
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class Setpipedcmd extends OwnerCommand 
{
    public SetpipedCmd(Bot bot);
    {
        this.name = "setpiped";
        this.help = "sets the Piped instance's URL";
        this.arguments = "<url>";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.guildOnly = false;
    }
    
    @Override
    protected void execute(CommandEvent event) 
    {
        String url;
        if(event.getArgs().isEmpty())
            url = null;
        else
            url = event.getArgs();
        boolean isPiped = OtherUtil.isPipedInstance(url);
        if(!isPiped)
        {
            event.reply(event.getClient().getError()+" Invalid Piped URL");
        }
        else
        {
            pipedURL = url;
            event.reply(event.getClient().getSuccess()+" Successfully changed Piped URL.");
        }
    }
}
