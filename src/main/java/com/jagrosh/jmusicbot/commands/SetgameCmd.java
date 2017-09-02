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
package com.jagrosh.jmusicbot.commands;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.jagrosh.jmusicbot.Bot;
import net.dv8tion.jda.core.entities.Game;

/**
 *
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class SetgameCmd extends Command {

    public SetgameCmd(Bot bot)
    {
        this.name = "setgame";
        this.help = "sets the game the bot is playing";
        this.arguments = "[game] OR stream <username> <game>";
        this.ownerCommand = true;
        this.category = bot.OWNER;
        this.children = new Command[]{new SetstreamCmd(bot)};
    }
    
    @Override
    protected void execute(CommandEvent event) {
        try {
            event.getJDA().getPresence().setGame(event.getArgs().isEmpty() ? null : Game.of(event.getArgs()));
            event.reply(event.getClient().getSuccess()+" **"+event.getSelfUser().getName()
                    +"** is "+(event.getArgs().isEmpty() ? "no longer playing anything." : "now playing `"+event.getArgs()+"`"));
        } catch(Exception e) {
            event.reply(event.getClient().getError()+" The game could not be set!");
        }
    }
    
    private class SetstreamCmd extends Command {
        
        private SetstreamCmd(Bot bot)
        {
            this.name = "stream";
            this.aliases = new String[]{"twitch","streaming"};
            this.help = "sets the game the bot is playing to a stream";
            this.arguments = "<username> <game>";
            this.ownerCommand = true;
            this.category = bot.OWNER;
        }

        @Override
        protected void execute(CommandEvent event) {
            String[] parts = event.getArgs().split("\\s+", 2);
            if(parts.length<2)
            {
                event.replyError("Please include a twitch username and the name of the game to 'stream'");
                return;
            }
            try {
                event.getJDA().getPresence().setGame(Game.of(parts[1], "https://twitch.tv/"+parts[0]));
                event.replySuccess("**"+event.getSelfUser().getName()
                        +"** is now streaming `"+parts[1]+"`");
            } catch(Exception e) {
                event.reply(event.getClient().getError()+" The game could not be set!");
            }
        }
    }
}
