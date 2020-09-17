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
package com.jagrosh.jmusicbot.commands.general;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.commands.AdminCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;

import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class RamCmd extends AdminCommand {
    public RamCmd(Bot bot) {
        this.name = "ram";
        this.help = "displays Siren's memory usage";
        this.arguments = "<message>";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.guildOnly = true;
    }

    @Override
    protected void execute(CommandEvent event) {
        long totalMem = Runtime.getRuntime().totalMemory();
        long usedMem = (totalMem - Runtime.getRuntime().freeMemory());
        BigDecimal usedMemBD, totalMemBD, memPercent;

        usedMemBD = new BigDecimal(usedMem);
        totalMemBD = new BigDecimal(totalMem);
        memPercent = new BigDecimal(100);
        MessageBuilder builder = new MessageBuilder();
        BigDecimal trueRamPercent = usedMemBD.divide(totalMemBD, 4, RoundingMode.HALF_DOWN).multiply(memPercent);
        EmbedBuilder ebuilder = new EmbedBuilder()
                .setColor(Color.cyan)
                .setTitle("My memory is at **" + trueRamPercent + "%**");
        event.getChannel().sendMessage(builder.setEmbed(ebuilder.build()).build()).queue();
    }
}
