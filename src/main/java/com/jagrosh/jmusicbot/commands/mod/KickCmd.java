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
package com.jagrosh.jmusicbot.commands.mod;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.commands.ModCommand;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;

import java.awt.*;

/**
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class KickCmd extends ModCommand {
    public KickCmd(Bot bot) {
        this.name = "kick";
        this.help = "kicks a user from your guild";
        this.arguments = "<username> [reason]";
        this.aliases = bot.getConfig().getAliases(this.name);
    }

    @Override
    protected void execute(CommandEvent event) {
        if (event.getArgs().isEmpty()) {
            MessageBuilder builder = new MessageBuilder();
            EmbedBuilder ebuilder = new EmbedBuilder()
                    .setColor(Color.red)
                    .setTitle(":scream_cat: Please mention a user!")
                    .setDescription("**Usage:** siren kick <username> [reason]");
            event.getChannel().sendMessage(builder.setEmbed(ebuilder.build()).build()).queue();
        } else {
            try {
                MessageBuilder builder = new MessageBuilder();
                EmbedBuilder ebuilder = new EmbedBuilder()
                        .setColor(Color.green)
                        //.setTitle(":cat: Successfully kicked **" + event.getMember().getNickname() + "**!")
                        .setDescription(":cat: **Successfully kicked " + event.getArgs() + "!**");
                event.getChannel().sendMessage(builder.setEmbed(ebuilder.build()).build()).queue();
            } catch (IllegalArgumentException e) {
            }
        }
    }
}

