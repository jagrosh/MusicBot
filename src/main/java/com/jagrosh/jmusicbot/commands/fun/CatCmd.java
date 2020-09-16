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
import com.jagrosh.jmusicbot.utils.OtherUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CatCmd extends BaseCatCmd {
    Logger log = LoggerFactory.getLogger("CatCmd");
    private Path path;

    public CatCmd(Bot bot) {
        this.category = new Category("Fun");

        this.name = "cat";
        this.help = "shows some kitties";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.guildOnly = false;

        createKittyListIfNeeded();

    }

    public void createKittyListIfNeeded() {

        ArrayList<String> kittyUrls = new ArrayList<>();
        kittyUrls.add("https://i.imgur.com/ntoWhyl.jpg");
        kittyUrls.add("https://i.imgur.com/PjlFbn4.jpg");
        kittyUrls.add("https://i.imgur.com/yPLXCc8.jpg");
        kittyUrls.add("https://i.imgur.com/oTRY5Of.jpg");
        kittyUrls.add("https://i.imgur.com/oj9zJyP.jpg");
        kittyUrls.add("https://i.imgur.com/RyAS0CC.jpg");
        kittyUrls.add("https://i.imgur.com/fDgItE6.jpg");
        kittyUrls.add("https://i.imgur.com/bLg792h.jpg");
        kittyUrls.add("https://i.imgur.com/1TqhWEu.jpg");
        kittyUrls.add("https://i.imgur.com/rQsbTtC.jpg");

        // get the path to the kitty config, default kitties.txt
        path = OtherUtil.getPath(System.getProperty("kittyList", "kitties.txt"));
        if (!path.toFile().exists()) {
            try {
                String urlsString = String.join("\n", kittyUrls);

                Files.write(path, urlsString.getBytes());
            } catch (IOException e) {
                startupLog.error("Unable to create kittyList: " + path, e);
            }
        }
        startupLog.info("Loaded kittyList from " + path);

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
                    .setDescription("**I found a kitty!** :cat:");
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
        List<String> urls;
        try {
            urls = Files.readAllLines(path);
        } catch (IOException e) {
            throw new RuntimeException("Unable to load kittyList: " + path, e);
        }
        String url = urls.get(new Random().nextInt(urls.size()));
        log.info("Loading kitty url: " + url);
        return url;
    }

}
