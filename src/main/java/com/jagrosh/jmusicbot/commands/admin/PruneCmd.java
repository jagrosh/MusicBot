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
package com.jagrosh.jmusicbot.commands.admin;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.commands.AdminCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class PruneCmd extends AdminCommand {
    public static final int QUIET_MILLIS = 5000;
    private long lastExecutionMillis = 0;

    public PruneCmd(Bot bot) {
        this.name = "prune";
        this.help = "deletes messages in bulk";
        this.arguments = "<number of messages>";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.guildOnly = true;
    }

    @Override
    protected void execute(CommandEvent event) {
        long now = System.currentTimeMillis();
        if (now > lastExecutionMillis + QUIET_MILLIS) {
            if (event.getArgs().length() < 1) {
                // Usage
                EmbedBuilder usage = new EmbedBuilder();
                usage.setColor(0xff3923);
                usage.setTitle("Specify amount to delete");
                usage.setDescription("Usage: `siren prune [# of messages]`");
                event.getChannel().sendMessage(usage.build()).queue();
            } else {
                try {
                    List<Message> messages = event.getChannel().getHistory().retrievePast(Integer.parseInt(event.getArgs())).complete();
                    event.getChannel().purgeMessages(messages);

                    // Success
                    EmbedBuilder success = new EmbedBuilder();
                    success.setColor(0x22ff2a);
                    success.setTitle(":smiley_cat: Successfully deleted " + event.getArgs() + " messages.");
                    MessageEmbed messageEmbed = success.build();
                    event.getChannel().sendMessage(messageEmbed).queue(message -> deleteAfterDelay(message));
                    lastExecutionMillis = now;

                } catch (IllegalArgumentException e) {
                    if (e.toString().startsWith("java.lang.IllegalArgumentException: Message retrieval")) {
                        // Too many messages
                        EmbedBuilder error = new EmbedBuilder();
                        error.setColor(0xff3923);
                        error.setTitle(":scream_cat: Too many messages selected");
                        error.setDescription("**You can only delete a max of 100 messages!**");
                        event.getChannel().sendMessage(error.build()).queue();
                    } else {
                        // Messages too old
                        // TODO Add other error
                        EmbedBuilder error = new EmbedBuilder();
                        error.setColor(0xff3923);
                        error.setTitle(":scream_cat: Selected messages are older than 2 weeks");
                        error.setDescription("Messages older than 2 weeks cannot be deleted.");
                        event.getChannel().sendMessage(error.build()).queue();
                    }
                }
            }
        } else {
            MessageBuilder builder = new MessageBuilder();
            EmbedBuilder ebuilder = new EmbedBuilder()
                    .setColor(Color.RED)
                    .setTitle("**Please slow down between commands!**")
                    .setDescription("Please wait ** " + (((QUIET_MILLIS - (now - lastExecutionMillis)) / 1000) + 1) + " ** more seconds.");
            event.getChannel().sendMessage(builder.setEmbed(ebuilder.build()).build()).queue();
        }
    }

    private void deleteAfterDelay(Message message) {
        TimerTask task = new TimerTask() {
            public void run() {
                message.delete().queue();
            }
        };

        new Timer("MessageDeleteTimer").schedule(task, 5000);
    }

}
