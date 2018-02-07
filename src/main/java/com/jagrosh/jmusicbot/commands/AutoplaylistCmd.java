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
import com.jagrosh.jmusicbot.playlist.Playlist;

/**
 *
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class AutoplaylistCmd extends Command {

    private final Bot bot;
    public AutoplaylistCmd(Bot bot)
    {
        this.bot = bot;
        this.category = bot.OWNER;
        this.ownerCommand = true;
        this.guildOnly = true;
        this.name = "autoplaylist";
        this.arguments = "<name|NONE>";
        this.help = "sets the default playlist for the server";
    }

    @Override
    public void execute(CommandEvent event) {
        if(event.getArgs().isEmpty())
        {
            event.reply(event.getClient().getError()+" Please include a playlist name or NONE");
            return;
        }
        if(event.getArgs().equalsIgnoreCase("none"))
        {
            bot.setDefaultPlaylist(event.getGuild(), null);
            event.reply(event.getClient().getSuccess()+" Cleared the default playlist for **"+event.getGuild().getName()+"**");
            return;
        }
        String pname = event.getArgs().replaceAll("\\s+", "_");
        if(Playlist.loadPlaylist(pname)==null)
        {
            event.reply(event.getClient().getError()+" Could not find `"+pname+".txt`!");
        }
        else
        {
            bot.setDefaultPlaylist(event.getGuild(), pname);
            event.reply(event.getClient().getSuccess()+" The default playlist for **"+event.getGuild().getName()+"** is now `"+pname+"`");
        }
    }
}
