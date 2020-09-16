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
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.Random;

public class HttpCatCmd extends BaseCatCmd {
    Logger log = LoggerFactory.getLogger("HttpCatCmd");

    public HttpCatCmd(Bot bot) {
        this.name = "httpcat";
        this.help = "shows some http kitties";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.guildOnly = false;
    }

    @Override
    protected void execute(CommandEvent event) {
        long now = System.currentTimeMillis();
        String channelId = event.getChannel().getId();
        Long lastExecutionMillis = lastExecutionMillisByChannelMap.getOrDefault(channelId, 0L);
        if (now > lastExecutionMillis + QUIET_MILLIS) {
            MessageBuilder builder = new MessageBuilder();
            EmbedBuilder ebuilder = new EmbedBuilder()
                    .setColor(event.getSelfMember().getColor())
                    .setImage(getKittyUrl())
                    .setDescription("**I found a http status kitty!** :cat:");
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

    @NotNull
    private String getKittyUrl() {
        Integer[] statuses = new Integer[]{100, 101, 200, 201, 202, 204, 206, 206, 300, 301, 302, 304, 305, 307, 401, 402, 403, 404, 405, 406, 408, 409, 410, 411, 412, 413, 414, 415, 416, 417, 418, 421, 422, 423, 424, 425, 426, 429, 431, 444, 451, 500, 501, 502, 503, 504, 506, 507, 508, 509, 510, 511, 599};
        return "https://http.cat/" + statuses[new Random().nextInt(statuses.length)];
    }

}
