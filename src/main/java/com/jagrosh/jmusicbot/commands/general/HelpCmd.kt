package com.jagrosh.jmusicbot.commands.general

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandClient
import com.jagrosh.jdautilities.command.CommandEvent
import com.jagrosh.jmusicbot.Bot
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.ChannelType
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.User

class HelpCmd(bot: Bot) : Command() {
    lateinit var commandClient: CommandClient

    init {
        name = "help"
        guildOnly = true
    }

    override fun execute(event: CommandEvent) {

        if (event.args.isEmpty()) {
            val Builder = EmbedBuilder()
            var Category: Category? = null
            val message = Category.let { it?.name } ?: "No Category"
            Builder.setTitle("**" + event.selfUser.name + "** Commands:")
            commandClient.commands
                    .filter { it.category?.name == "Music" || it.category?.name == "Fun" || it.category?.name == "DJ" || it.category?.name == "Mod" || it.category?.name == "Admin" }
                    .forEach { command ->
                        if (!command.isHidden && (!command.isOwnerCommand || event!!.isOwner)) {
                            if (Category != command.category) {
                                Category = command.category
                                Builder.appendDescription("\n\n  **").appendDescription(message).appendDescription(":**\n")
                            }
                            Builder.appendDescription("\n`").appendDescription(commandClient.textualPrefix).appendDescription(if (commandClient.prefix == null) " " else "").appendDescription(command.name)
                                    .appendDescription(if (command.arguments == null) "`" else " " + command.arguments + "`")
                                    .appendDescription(" - ").appendDescription(command.help)
                        }
                    }
            val owner: User? = event.jda.getUserById(commandClient.ownerId)
            if (owner != null) {
                Builder.setFooter("For additional help, contact " + owner.asTag + " or join https://discord.gg/Eyetd8J.", "https://cdn.discordapp.com/avatars/297735090014912522/09aa122396c85c55a04a9762054c975b.png")
            }
            event.replyInDm(Builder.build(), { unused: Message? -> if (event!!.isFromType(ChannelType.TEXT)) event!!.reactSuccess() }) { t: Throwable? -> event!!.replyWarning("Help message cannot be sent because you are blocking Direct Messages.") }
        } else {
            if (event.args == "Mod" || event.args == "DJ" || event.args == "Admin" || event.args == "Music") {
                val Builder = EmbedBuilder()
                Builder.setTitle("**" + event.args + "** Commands:")
                commandClient.commands
                        .filter { it.category?.name == event.args }
                        .forEach { command ->
                            if (!command.isHidden && (!command.isOwnerCommand || event!!.isOwner)) {
                                Builder.appendDescription("\n`").appendDescription(commandClient.textualPrefix).appendDescription(if (commandClient.prefix == null) " " else "").appendDescription(command.name)
                                        .appendDescription(if (command.arguments == null) "`" else " " + command.arguments + "`")
                                        .appendDescription(" - ").appendDescription(command.help)
                            }
                        }
                val owner: User? = event.jda.getUserById(commandClient.ownerId)
                if (owner != null) {
                    Builder.setFooter("For additional help, contact " + owner.asTag + " or join https://discord.gg/Eyetd8J.", "https://cdn.discordapp.com/avatars/297735090014912522/09aa122396c85c55a04a9762054c975b.png")
                }
                event.replyInDm(Builder.build(), { unused: Message? -> if (event!!.isFromType(ChannelType.TEXT)) event!!.reactSuccess() }) { t: Throwable? -> event!!.replyWarning("Help message cannot be sent because you are blocking Direct Messages.") }
            } else {
                event.reply(":scream_cat: You must select from the following categories (Case sensitive): **Music, DJ, Mod, Admin**")
            }
        }
    }
}