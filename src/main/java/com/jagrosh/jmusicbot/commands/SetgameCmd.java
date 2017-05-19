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
        this.arguments = "[game]";
        this.ownerCommand = true;
        this.category = bot.OWNER;
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
    
}
