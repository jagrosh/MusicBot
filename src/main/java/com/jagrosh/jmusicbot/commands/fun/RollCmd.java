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
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

import java.awt.*;
import java.util.*;
import java.util.stream.Collectors;

public class RollCmd extends FunCommand {
    public static final int QUIET_MILLIS = 3000;
    public static final String UNICODE_FOUR = "4️⃣";
    public static final String UNICODE_SIX = "6️⃣";
    public static final String UNICODE_EIGHT = "8️⃣";
    public static final String UNICODE_TEN = "\uD83D\uDD1F";
    public static final String UNICODE_TWELVE = "\uD83C\uDD70";
    public static final String UNICODE_TWENTY = "\uD83C\uDD71";
    public static final String UNICODE_CANCEL = "❎";

    protected Map<String, Long> lastExecutionMillisByChannelMap = new LinkedHashMap<>();
    static protected Map<String, String> digitToStringMap = new LinkedHashMap<>();
    private Map<String, String> dmGuiMessageIdToUserIdMap = new LinkedHashMap<>();

    private Set<String> guiMessageIds = new LinkedHashSet<>();

    static {
        digitToStringMap.put("0", ":zero:");
        digitToStringMap.put("1", ":one:");
        digitToStringMap.put("2", ":two:");
        digitToStringMap.put("3", ":three:");
        digitToStringMap.put("4", ":four:");
        digitToStringMap.put("5", ":five:");
        digitToStringMap.put("6", ":six:");
        digitToStringMap.put("7", ":seven:");
        digitToStringMap.put("8", ":eight:");
        digitToStringMap.put("9", ":nine:");
    }

    public RollCmd(Bot bot) {
        this.name = "roll";
        this.help = "rolls a dice for you";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.guildOnly = false;
        this.category = new Category("Fun");
    }

    @Override
    protected void execute(CommandEvent event) {
        String[] args = event.getArgs().split("\\s");
        boolean showGui = false;
        boolean useDm = false;
        if (args[0].equals("-s")) {

            useDm = true;
            showGui = true;
        }
        String sideCountString = args[0].replaceAll("\\D+", "");
        if (sideCountString.isEmpty()) {
            showGui = true;
        }
        if (showGui) {
            // Show dice roll GUI
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
                                    "**TIPS:**\n" +
                                    "For a custom amount of sides, type `siren roll <amount of sides>`.\n" +
                                    "To make the dice roll private, type `siren roll -s`.");

            boolean finalUseDm = useDm;

            Message embedMessage = builder.setEmbed(ebuilder.build()).build();
            if (!useDm) {
                event.getChannel().sendMessage(embedMessage).queue(message -> handleQueuedDiceMessage(event.getAuthor(), finalUseDm, message));

            } else {
                event.getAuthor().openPrivateChannel()
                        .flatMap(privateChannel -> privateChannel.sendMessage(embedMessage)).queue(message -> handleQueuedDiceMessage(event.getAuthor(), finalUseDm, message));
                event.getChannel().sendMessage(":cat: Sent dice roll menu in DMs!").queue();
            }


            return;
        }

        int sideCount = Integer.parseInt(sideCountString);

        if (sideCount == 0) {
            MessageBuilder builder = new MessageBuilder();
            EmbedBuilder ebuilder = new EmbedBuilder()
                    .setColor(Color.RED)
                    .setImage("https://http.cat/508")
                    .setTitle("Why would you even try that?");
            event.getChannel().sendMessage(builder.setEmbed(ebuilder.build()).build()).queue();
            return;
        }
        rollDie(event.getJDA(), event.getChannel(), event.getMember(), sideCount, null); // TODO: 10/14/2020 allow private custom rolling
    }


    private void handleQueuedDiceMessage(User user, boolean finalUseDm, Message message) {
        if (!finalUseDm) {
            guiMessageIds.add(message.getId());
        } else {
            dmGuiMessageIdToUserIdMap.put(message.getId(), user.getId());
        }
        addDiceReactions(message);
    }

    private void addDiceReactions(net.dv8tion.jda.api.entities.Message message) {
        message.addReaction(UNICODE_FOUR).queue();
        message.addReaction(UNICODE_SIX).queue();
        message.addReaction(UNICODE_EIGHT).queue();
        message.addReaction(UNICODE_TEN).queue();
        message.addReaction(UNICODE_TWELVE).queue();
        message.addReaction(UNICODE_TWENTY).queue();
        message.addReaction(UNICODE_CANCEL).queue();
    }

    private void rollDie(JDA jda, MessageChannel channel, Member member, int sideCount, String dmUserId) { // TODO: 10/14/2020 Make only command operator able to control dice rolling
        int rollOutput = new Random().nextInt(sideCount) + 1;

        String rollOutputMessage = Arrays.stream(String.valueOf(rollOutput).split("")).map(s -> digitToStringMap.get(s)).collect(Collectors.joining());
        long now = System.currentTimeMillis();
        Long lastExecutionMillis = lastExecutionMillisByChannelMap.getOrDefault(channel.getId(), 0L);
//        if (now > lastExecutionMillis + QUIET_MILLIS) {

        if (dmUserId == null) {
            MessageBuilder builder = new MessageBuilder();
            EmbedBuilder ebuilder = new EmbedBuilder()
                    .setColor(Color.GREEN)
                    .setDescription(member.getAsMention() + " rolled a D" + sideCount + "! **OUTPUT:**");
            channel.sendMessage(builder.setEmbed(ebuilder.build()).build()).queue();
            channel.sendMessage(rollOutputMessage).queue(message -> handleQueuedDiceMessage(member.getUser(), false, message));
        } else {
            User user = jda.getUserById(dmUserId);
            user.openPrivateChannel()
                    .flatMap(privateChannel -> privateChannel.sendMessage(rollOutputMessage)).queue(message -> handleQueuedDiceMessage(user, true, message));
        }


        lastExecutionMillisByChannelMap.put(channel.getId(), now);
//        } else {
//            MessageBuilder builder = new MessageBuilder();
//            EmbedBuilder ebuilder = new EmbedBuilder()
//                    .setColor(Color.RED)
//                    .setTitle("**Please slow down between commands!**")
//                    .setDescription("Please wait ** " + (((QUIET_MILLIS - (now - lastExecutionMillis)) / 1000) + 1) + " ** more seconds.");
//            event.getChannel().sendMessage(builder.setEmbed(ebuilder.build()).build()).queue();
//        }
    }

    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        if (guiMessageIds.contains(event.getMessageId()) || dmGuiMessageIdToUserIdMap.containsKey(event.getMessageId())) {
            if (!event.getUser().equals(event.getJDA().getSelfUser()))
                switch (event.getReactionEmote().getName()) {
                    case UNICODE_FOUR:
                        rollDie(event.getJDA(), event.getChannel(), event.getMember(), 4, dmGuiMessageIdToUserIdMap.get(event.getMessageId()));
                        event.getReaction().removeReaction(event.getUser()).queue();
                        break;
                    case UNICODE_SIX:
                        rollDie(event.getJDA(), event.getChannel(), event.getMember(), 6, dmGuiMessageIdToUserIdMap.get(event.getMessageId()));
                        event.getReaction().removeReaction(event.getUser()).queue();
                        break;
                    case UNICODE_EIGHT:
                        rollDie(event.getJDA(), event.getChannel(), event.getMember(), 8, dmGuiMessageIdToUserIdMap.get(event.getMessageId()));
                        event.getReaction().removeReaction(event.getUser()).queue();
                        break;
                    case UNICODE_TEN:
                        rollDie(event.getJDA(), event.getChannel(), event.getMember(), 10, dmGuiMessageIdToUserIdMap.get(event.getMessageId()));
                        event.getReaction().removeReaction(event.getUser()).queue();
                        break;
                    case UNICODE_TWELVE:
                        rollDie(event.getJDA(), event.getChannel(), event.getMember(), 12, dmGuiMessageIdToUserIdMap.get(event.getMessageId()));
                        event.getReaction().removeReaction(event.getUser()).queue();
                        break;
                    case UNICODE_TWENTY:
                        rollDie(event.getJDA(), event.getChannel(), event.getMember(), 20, dmGuiMessageIdToUserIdMap.get(event.getMessageId()));
                        event.getReaction().removeReaction(event.getUser()).queue();
                        break;
                    case UNICODE_CANCEL: // TODO: 10/14/2020
                        event.getChannel().deleteMessageById(event.getMessageId()).queue();
                        event.getReaction().removeReaction(event.getUser()).queue();
                        break;
                }
        }
    }
}