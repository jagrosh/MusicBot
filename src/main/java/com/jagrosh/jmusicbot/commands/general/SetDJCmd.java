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
package com.jagrosh.jmusicbot.commands.general;

import java.util.List;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.settings.RepeatMode;
import com.jagrosh.jmusicbot.commands.DJCommand;
import com.jagrosh.jmusicbot.commands.MusicCommand;
import com.jagrosh.jmusicbot.settings.Settings;
import com.jagrosh.jmusicbot.utils.FormatUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;

/**
 *
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class SetDJCmd extends MusicCommand
{
    
    public SetDJCmd(Bot bot)
    {
        super(bot);
        this.name = "dj";
        //this.help = "skips the current song";
        //this.aliases = bot.getConfig().getAliases(this.name);
        //this.bePlaying = true;
    }
    
    @Override
    public void doCommand(CommandEvent event) 
    {
        boolean is_mod = false;
        List<Role> user_roles = event.getMember().getRoles();

        for (Role r : user_roles) {
            if (r.getIdLong() == 736622853797052519L)
                is_mod = true;
                break;
        }
        if(!is_mod) return;
        String args = event.getArgs().toLowerCase().trim();
        switch(args){
            case "on":
                this.bot.setDJMode(true);
                event.reply("DJ Mode On.");
                break;
            case "off":
                this.bot.setDJMode(false);
                event.reply("DJ Mode Off.");
                break;
            default:
                event.reply("Didn't understand. j!dj <on|off>");
        }
    }

    

    
}
