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
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

public class KickCmd extends ModCommand {
    Logger log = LoggerFactory.getLogger("KickCmd");

    public KickCmd(Bot bot) {
        this.name = "kick";
        this.help = "kicks a user from your guild";
        this.arguments = "<username>";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.botPermissions = new Permission[]{Permission.KICK_MEMBERS};
    }

    @Override
    protected void execute(CommandEvent event) {
        if (event.getArgs().isEmpty()) {
            MessageBuilder builder = new MessageBuilder();
            EmbedBuilder ebuilder = new EmbedBuilder()
                    .setColor(Color.red)
                    .setTitle(":scream_cat: Please mention a user!")
                    .setDescription("**Usage:** siren kick <username>");
            event.getChannel().sendMessage(builder.setEmbed(ebuilder.build()).build()).queue();


        } else {
            String userId = event.getArgs().replaceAll("\\D+", "");
            User user;
            try {
                user = event.getJDA().getUserById(userId);

            } catch (Exception e) {
                user = null;
                log.info("Unknown User (" + event.getArgs() + ")", e);
            }
            if (user == null) {
                MessageBuilder builder = new MessageBuilder();
                EmbedBuilder ebuilder = new EmbedBuilder()
                        .setColor(Color.red)
                        .setDescription(":scream_cat: **Failed to kick!**\n\n**Reason:**\nUnknown User");
                event.getChannel().sendMessage(builder.setEmbed(ebuilder.build()).build()).queue();
                log.info("Unknown User (" + event.getArgs() + ")");
                return;
            }

            if (!event.getMember().canInteract(event.getGuild().getMember(user))) {
                MessageBuilder builder = new MessageBuilder();
                EmbedBuilder ebuilder = new EmbedBuilder()
                        .setColor(Color.red)
                        .setTitle(":scream_cat: **Failed to kick!**")
                        .setDescription("**Reason:**\nYou cannot kick " + event.getArgs() + "!");
                event.getChannel().sendMessage(builder.setEmbed(ebuilder.build()).build()).queue();
                log.info(event.getMember().getEffectiveName() + " cannot kick " + event.getArgs());
                return;
            }
            ;

            MessageBuilder builder = new MessageBuilder();
            EmbedBuilder ebuilder = new EmbedBuilder()
                    .setColor(Color.green)
                    .setDescription(":cat: **Successfully kicked " + event.getArgs() + "!**");
            User finalUser = user;
            event.getChannel().sendMessage(builder.setEmbed(ebuilder.build()).build())
                    .queue(message -> {
                        MessageEmbed kickMessage = new EmbedBuilder()
                                .setColor(Color.red)
                                .setDescription("You were kicked by " + event.getMember().getEffectiveName() + "!")
                                .setTitle(":scream_cat: You have been kicked from " + event.getGuild().getName() + "!").build();
                        finalUser.openPrivateChannel()
                                .flatMap(channel -> channel.sendMessage(kickMessage))
                                .queue();

                        kickAfterDelay(event, finalUser);
                    }, throwable -> {
                    });


        }
    }

    private void kickAfterDelay(CommandEvent event, User user) {
        TimerTask task = new TimerTask() {
            public void run() {
                event.getGuild().kick(user.getId()).queue();
            }
        };

        new Timer("KickTimer").schedule(task, 5000);
    }
}

