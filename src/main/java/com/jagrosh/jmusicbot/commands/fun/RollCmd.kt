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
package com.jagrosh.jmusicbot.commands.`fun`

import com.jagrosh.jdautilities.command.CommandEvent
import com.jagrosh.jmusicbot.Bot
import com.jagrosh.jmusicbot.commands.FunCommand
import com.jagrosh.jmusicbot.utils.queueMessageToChannel
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.entities.*
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent
import java.awt.Color
import java.util.*
import java.util.stream.Collectors

class RollCmd(bot: Bot) : FunCommand() {
    protected var lastExecutionMillisByChannelMap: MutableMap<String, Long> = LinkedHashMap()
    private val dmGuiMessageIdToUserIdMap: MutableMap<String, String> = LinkedHashMap()
    private val guiMessageIds: MutableSet<String> = LinkedHashSet()

    companion object {
        const val QUIET_MILLIS = 3000
        const val UNICODE_FOUR = "4️⃣"
        const val UNICODE_SIX = "6️⃣"
        const val UNICODE_EIGHT = "8️⃣"
        const val UNICODE_TEN = "\uD83D\uDD1F"
        const val UNICODE_TWELVE = "\uD83C\uDD70"
        const val UNICODE_TWENTY = "\uD83C\uDD71"
        const val UNICODE_CANCEL = "❎"

        val digitToStringMap = mapOf(
                "0" to ":zero:",
                "1" to ":one:",
                "2" to ":two:",
                "3" to ":three:",
                "4" to ":four:",
                "5" to ":five:",
                "6" to ":six:",
                "7" to ":seven:",
                "8" to ":eight:",
                "9" to ":nine:",
        )

    }

    init {
        name = "roll"
        help = "rolls a dice for you"
        aliases = bot.config.getAliases(name)
        guildOnly = false
        this.category = Category("Fun")
    }

    override fun execute(event: CommandEvent) {
        val args = event.args.split("\\s".toRegex()).toTypedArray()
        var showGui = false
        var useDm = false
        if (args[0] == "-s") {
            useDm = true
            showGui = true
        }
        val sideCountString = args[0].replace("\\D+".toRegex(), "")
        if (sideCountString.isEmpty()) {
            showGui = true
        }
        if (showGui) {
            // Show dice roll GUI
            val builder = MessageBuilder()
            val ebuilder = EmbedBuilder()
                    .setColor(event.selfMember.color)
                    .setTitle("**:cat: Please react with the dice you want to roll!**")
                    .setDescription(
                            """
                        **:four: Four sided dice
                        :six: Six sided dice
                        :eight: Eight sided dice
                        :keycap_ten: Ten sided dice
                        🅰 Twelve sided dice
                        🅱 Twenty sided dice
                        ❎ Cancel dice rolling**
                        **TIPS:**
                        For a custom amount of sides, type `siren roll <amount of sides>`.
                        To make the dice roll private, type `siren roll -s`.
                        """.trimIndent())
            val finalUseDm = useDm
            val embedMessage = builder.setEmbed(ebuilder.build()).build()
            if (!useDm) {
                event.channel.sendMessage(embedMessage).queue { message: Message -> handleQueuedDiceMessage(event.author, finalUseDm, message) }
            } else {
                event.author.openPrivateChannel()
                        .flatMap { privateChannel: PrivateChannel -> privateChannel.sendMessage(embedMessage) }.queue { message: Message -> handleQueuedDiceMessage(event.author, finalUseDm, message) }
                event.queueMessageToChannel(":cat: Sent dice roll menu in DMs!")
            }
            return
        }
        val sideCount = sideCountString.toInt()
        if (sideCount == 0) {
            val builder = MessageBuilder()
            val ebuilder = EmbedBuilder()
                    .setColor(Color.RED)
                    .setImage("https://http.cat/508")
                    .setTitle("Why would you even try that?")
            event.channel.sendMessage(builder.setEmbed(ebuilder.build()).build()).queue()
            return
        }
        rollDie(event.jda, event.channel, event.member, sideCount, null) // TODO: 10/14/2020 allow private custom rolling
    }

    private fun handleQueuedDiceMessage(user: User?, finalUseDm: Boolean, message: Message) {
        if (!finalUseDm) {
            guiMessageIds.add(message.id)
        } else {
            dmGuiMessageIdToUserIdMap[message.id] = user!!.id
        }
        addDiceReactions(message)
    }

    private fun addDiceReactions(message: Message) {
        message.addReaction(UNICODE_FOUR).queue()
        message.addReaction(UNICODE_SIX).queue()
        message.addReaction(UNICODE_EIGHT).queue()
        message.addReaction(UNICODE_TEN).queue()
        message.addReaction(UNICODE_TWELVE).queue()
        message.addReaction(UNICODE_TWENTY).queue()
        message.addReaction(UNICODE_CANCEL).queue()
    }

    private fun rollDie(jda: JDA, channel: MessageChannel, member: Member?, sideCount: Int, dmUserId: String?) { // TODO: 10/14/2020 Make only command operator able to control dice rolling
        val rollOutput = Random().nextInt(sideCount) + 1
        val rollOutputMessage = Arrays.stream(rollOutput.toString().split("".toRegex()).toTypedArray()).map { s: String -> digitToStringMap[s] }.collect(Collectors.joining())
        val now = System.currentTimeMillis()
        val lastExecutionMillis = lastExecutionMillisByChannelMap.getOrDefault(channel.id, 0L)
        //        if (now > lastExecutionMillis + QUIET_MILLIS) {
        if (dmUserId == null) {
            val builder = MessageBuilder()
            val ebuilder = EmbedBuilder()
                    .setColor(Color.GREEN)
                    .setDescription(member!!.asMention + " rolled a D" + sideCount + "! **OUTPUT:**")
            channel.sendMessage(builder.setEmbed(ebuilder.build()).build()).queue()
            channel.sendMessage(rollOutputMessage).queue { message: Message -> handleQueuedDiceMessage(member.user, false, message) }
        } else {
            val user = jda.getUserById(dmUserId)
            user!!.openPrivateChannel()
                    .flatMap { privateChannel: PrivateChannel -> privateChannel.sendMessage(rollOutputMessage) }.queue { message: Message -> handleQueuedDiceMessage(user, true, message) }
        }
        lastExecutionMillisByChannelMap[channel.id] = now
        //        } else {
//            MessageBuilder builder = new MessageBuilder();
//            EmbedBuilder ebuilder = new EmbedBuilder()
//                    .setColor(Color.RED)
//                    .setTitle("**Please slow down between commands!**")
//                    .setDescription("Please wait ** " + (((QUIET_MILLIS - (now - lastExecutionMillis)) / 1000) + 1) + " ** more seconds.");
//            event.getChannel().sendMessage(builder.setEmbed(ebuilder.build()).build()).queue();
//        }
    }

    fun onMessageReactionAdd(event: MessageReactionAddEvent) {
        if (guiMessageIds.contains(event.messageId) || dmGuiMessageIdToUserIdMap.containsKey(event.messageId)) {
            if (event.user != event.jda.selfUser) when (event.reactionEmote.name) {
                UNICODE_FOUR -> {
                    rollDie(event.jda, event.channel, event.member, 4, dmGuiMessageIdToUserIdMap[event.messageId])
                    event.reaction.removeReaction(event.user!!).queue()
                }
                UNICODE_SIX -> {
                    rollDie(event.jda, event.channel, event.member, 6, dmGuiMessageIdToUserIdMap[event.messageId])
                    event.reaction.removeReaction(event.user!!).queue()
                }
                UNICODE_EIGHT -> {
                    rollDie(event.jda, event.channel, event.member, 8, dmGuiMessageIdToUserIdMap[event.messageId])
                    event.reaction.removeReaction(event.user!!).queue()
                }
                UNICODE_TEN -> {
                    rollDie(event.jda, event.channel, event.member, 10, dmGuiMessageIdToUserIdMap[event.messageId])
                    event.reaction.removeReaction(event.user!!).queue()
                }
                UNICODE_TWELVE -> {
                    rollDie(event.jda, event.channel, event.member, 12, dmGuiMessageIdToUserIdMap[event.messageId])
                    event.reaction.removeReaction(event.user!!).queue()
                }
                UNICODE_TWENTY -> {
                    rollDie(event.jda, event.channel, event.member, 20, dmGuiMessageIdToUserIdMap[event.messageId])
                    event.reaction.removeReaction(event.user!!).queue()
                }
                UNICODE_CANCEL -> event.channel.getHistoryBefore(event.messageId, 1).queue { messageHistory: MessageHistory ->
                    for (message in messageHistory.retrievedHistory) {
                        event.channel.deleteMessageById(message.id).queue()
                    }
                    event.channel.deleteMessageById(event.messageId).queue()
                }
            }
        }
    }
}