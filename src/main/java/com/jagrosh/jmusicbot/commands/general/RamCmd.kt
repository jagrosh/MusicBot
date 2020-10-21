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
package com.jagrosh.jmusicbot.commands.general

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import com.jagrosh.jmusicbot.Bot
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.MessageBuilder
import java.math.BigDecimal
import java.math.RoundingMode

class RamCmd(bot: Bot) : Command() {
    override fun execute(event: CommandEvent) {
        val totalMem = Runtime.getRuntime().totalMemory()
        val usedMem = totalMem - Runtime.getRuntime().freeMemory()
        val usedMemBD: BigDecimal
        val totalMemBD: BigDecimal
        val memPercent: BigDecimal
        usedMemBD = BigDecimal(usedMem)
        totalMemBD = BigDecimal(totalMem)
        memPercent = BigDecimal(100)
        val builder = MessageBuilder()
        val trueRamPercent = usedMemBD.divide(totalMemBD, 4, RoundingMode.HALF_DOWN).multiply(memPercent)
        val ebuilder = EmbedBuilder()
                .setColor(1015169)
                .setTitle("My memory is at **" + trueRamPercent.setScale(2, BigDecimal.ROUND_HALF_UP) + "%**")
        event.channel.sendMessage(builder.setEmbed(ebuilder.build()).build()).queue()
    }

    init {
        name = "ram"
        help = "displays Siren's memory usage"
        arguments = ""
        aliases = bot.config.getAliases(name)
        guildOnly = true
    }
}