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
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

public class BanCmd extends ModCommand {
    Logger log = LoggerFactory.getLogger("BanCmd");

    public BanCmd(Bot bot) {
        this.name = "ban";
        this.help = "bans a user from your guild";
        this.arguments = "<username>";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.botPermissions = new Permission[]{Permission.BAN_MEMBERS};
    }

    @Override
    protected void execute(CommandEvent event) {
        String[] args = event.getArgs().split("\\s");
        String rawUserId = args[0];
        String reason = Arrays.stream(args).skip(1).collect(Collectors.joining(" "));
        if (reason.isEmpty()) {
            reason = "Reason not specified.";
        }

        if (rawUserId.isEmpty()) {
            sendAndQueueEmbed(event, Color.red, ":scream_cat: Please mention a user!", "**Usage:** siren ban <username>");
        } else {
            String userId = rawUserId.replaceAll("\\D+", "");
            User user;
            try {
                user = event.getJDA().getUserById(userId);

            } catch (Exception e) {
                user = null;
                log.info("Unknown User (" + rawUserId + ")", e);
            }
            if (user == null) {
                sendAndQueueEmbed(event, Color.red, null, ":scream_cat: **Failed to ban!**\n\n**Reason:**\nUnknown User");
                log.info("Unknown User (" + rawUserId + ")");
                return;
            }

            if (!event.getMember().canInteract(event.getGuild().getMember(user))) {
                sendAndQueueEmbed(event, Color.red, ":scream_cat: **Failed to ban!**", "**Reason:**\nYou cannot ban " + rawUserId + "!");
                log.info(event.getMember().getEffectiveName() + " cannot ban " + rawUserId);
                return;
            }
            ;

            User finalUser = user;
            String finalReason = reason;
            MessageBuilder builder = new MessageBuilder();
            EmbedBuilder ebuilder = new EmbedBuilder()
                    .setColor(Color.green)
                    .setDescription(":cat: **Successfully banned " + rawUserId + "!\nReason: `" + reason + "`**");
            event.getChannel().sendMessage(builder.setEmbed(ebuilder.build()).build())
                    .queue(message -> {
                        MessageEmbed banMessage = new EmbedBuilder()
                                .setColor(Color.red)
                                .setDescription("You were banned by " + event.getMember().getEffectiveName() + "!\n**Reason:** `" + finalReason + "`")
                                .setTitle(":scream_cat: You have been banned from " + event.getGuild().getName() + "!").build();
                        finalUser.openPrivateChannel()
                                .flatMap(channel -> channel.sendMessage(banMessage))
                                .queue();

                        banAfterDelay(event, finalUser);
                    }, throwable -> {
                    });


        }
    }

    private void sendAndQueueEmbed(CommandEvent event, Color color, String title, String description) {
        MessageBuilder builder = new MessageBuilder();
        EmbedBuilder ebuilder = new EmbedBuilder()
                .setColor(color)
                .setTitle(title)
                .setDescription(description);
        event.getChannel().sendMessage(builder.setEmbed(ebuilder.build()).build()).queue();
    }

    private void banAfterDelay(CommandEvent event, User user) {
        TimerTask task = new TimerTask() {
            public void run() {
                event.getGuild().ban(user.getId(), 0).queue();
            }
        };

        new Timer("BanTimer").schedule(task, 250);
    }
}

