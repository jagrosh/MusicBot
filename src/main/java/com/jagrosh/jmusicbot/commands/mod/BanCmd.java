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
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

public class BanCmd extends ModCommand {
    public BanCmd(Bot bot) {
        this.name = "ban";
        this.help = "bans a user from your guild";
        this.arguments = "<username>";
        this.aliases = bot.getConfig().getAliases(this.name);
    }

    @Override
    protected void execute(CommandEvent event) {
        if (event.getArgs().isEmpty()) {
            MessageBuilder builder = new MessageBuilder();
            EmbedBuilder ebuilder = new EmbedBuilder()
                    .setColor(Color.red)
                    .setTitle(":scream_cat: Please mention a user!")
                    .setDescription("**Usage:** siren ban <username>");
            event.getChannel().sendMessage(builder.setEmbed(ebuilder.build()).build()).queue();


        } else {
            MessageBuilder builder = new MessageBuilder();
            EmbedBuilder ebuilder = new EmbedBuilder()
                    .setColor(Color.green)
                    .setDescription(":cat: **Successfully banned " + event.getArgs() + "!**");
            event.getChannel().sendMessage(builder.setEmbed(ebuilder.build()).build()).queue();

            String userId = event.getArgs().replaceAll("\\D+", "");
            User user = event.getJDA().getUserById(userId);
            MessageEmbed banMessage = new EmbedBuilder()
                    .setColor(Color.red)
                    .setDescription("You were banned by " + event.getMember().getEffectiveName() + "!")
                    .setTitle(":scream_cat: You have been banned permanently from " + event.getGuild().getName() + "!").build();
            user.openPrivateChannel()
                    .flatMap(channel -> channel.sendMessage(banMessage))
                    .queue();

            banAfterDelay(event, user);
        }
    }

    private void banAfterDelay(CommandEvent event, User user) {
        TimerTask task = new TimerTask() {
            public void run() {
                event.getGuild().ban(user.getId(), 0).queue();
            }
        };

        new Timer("BanTimer").schedule(task, 5000);
    }
}

