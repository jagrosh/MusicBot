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
package com.jagrosh.jmusicbot.commands.fun;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.commands.FunCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;

import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

public class RollCmd extends FunCommand {
    public static final int QUIET_MILLIS = 3000;

    protected Map<String, Long> lastExecutionMillisByChannelMap = new LinkedHashMap<>();

    public RollCmd(Bot bot) { //test
        this.name = "roll";
        this.help = "rolls a dice for you";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.guildOnly = false;
        this.category = new Category("Fun");
    }

    @Override
    protected void execute(CommandEvent event) {
        String[] args = event.getArgs().split("\\s");
        String zeroArgs = args[0].replaceAll("\\D+", "");
        if (zeroArgs == "0") {
            MessageBuilder builder = new MessageBuilder();
            EmbedBuilder ebuilder = new EmbedBuilder()
                    .setColor(Color.RED)
                    .setTitle("no")
                    .setDescription("no");
            event.getChannel().sendMessage(builder.setEmbed(ebuilder.build()).build()).queue();
        } else {
            String sideCount = args[0].replaceAll("\\D+", "");
            if (sideCount == "") {
                MessageBuilder builder = new MessageBuilder();
                EmbedBuilder ebuilder = new EmbedBuilder()
                        .setColor(event.getSelfMember().getColor())
                        .setTitle("**:cat: Please react with the dice you want to roll!**")
                        .setDescription(
                                "**:four: Four sided dice\n" +
                                        ":six: Six sided dice\n" +
                                        ":eight: Eight sided dice\n" +
                                        ":keycap_ten: Ten sided dice\n" +
                                        "\uD83C\uDD70 Twelve sided dice\n" +
                                        "\uD83C\uDD71 Twenty sided dice\n" +
                                        "❎ Cancel dice rolling**\n" +
                                        "For a custom amount of sides, type `siren roll <amount of sides>`.");
                event.getChannel().sendMessage(builder.setEmbed(ebuilder.build()).build()).queue(message -> {
                    message.addReaction("4️⃣").queue();
                    message.addReaction("6️⃣").queue();
                    message.addReaction("8️⃣").queue();
                    message.addReaction("\uD83D\uDD1F").queue();
                    message.addReaction("\uD83C\uDD70").queue();
                    message.addReaction("\uD83C\uDD71").queue();
                    message.addReaction("❎").queue();
                });
            } else {
                int sideCountInt = Integer.parseInt(sideCount) + 1;
                Random random = new Random();
                int getRollOutput = 0;
                while (true) {
                    getRollOutput = random.nextInt(sideCountInt);
                    if (getRollOutput != 0) break;
                }
                long now = System.currentTimeMillis();
                String channelId = event.getChannel().getId();
                Long lastExecutionMillis = lastExecutionMillisByChannelMap.getOrDefault(channelId, 0L);
                if (now > lastExecutionMillis + QUIET_MILLIS) {
                    MessageBuilder builder = new MessageBuilder();
                    EmbedBuilder ebuilder = new EmbedBuilder()
                            .setColor(event.getSelfMember().getColor())
                            .setDescription("**:cat: " + event.getMember().getAsMention() + " rolled a `" + getRollOutput + "`!**");
                    event.getChannel().sendMessage(builder.setEmbed(ebuilder.build()).build()).queue();
                    lastExecutionMillisByChannelMap.put(channelId, now);
                } else {
                    MessageBuilder builder = new MessageBuilder();
                    EmbedBuilder ebuilder = new EmbedBuilder()
                            .setColor(Color.RED)
                            .setTitle("**Please slow down between commands!**")
                            .setDescription("Please wait ** " + (((QUIET_MILLIS - (now - lastExecutionMillis)) / 1000) + 1) + " ** more seconds.");
                    event.getChannel().sendMessage(builder.setEmbed(ebuilder.build()).build()).queue();
                }
            }
        }
    }
}