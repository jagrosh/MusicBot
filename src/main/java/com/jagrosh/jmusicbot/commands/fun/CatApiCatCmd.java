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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.commands.FunCommand;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class CatApiCatCmd extends FunCommand {
    public static final int QUIET_MILLIS = 3000;
    Logger log = LoggerFactory.getLogger("CatCmd");
    Logger startupLog = LoggerFactory.getLogger("Startup");
    private Path path;
    private long lastExecutionMillis = 0;

    public CatApiCatCmd(Bot bot) {
        this.name = "unknowncat";
        this.help = "shows some unknown kitties";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.guildOnly = false;

    }


    @Override
    protected void execute(CommandEvent event) {
        long now = System.currentTimeMillis();
        if (now > lastExecutionMillis + QUIET_MILLIS) {
            MessageBuilder builder = new MessageBuilder();
            EmbedBuilder ebuilder = new EmbedBuilder()
                    .setColor(event.getSelfMember().getColor())
                    .setImage(getKittyUrl())
                    .setDescription("**I found a kitty!** :cat:");
            event.getChannel().sendMessage(builder.setEmbed(ebuilder.build()).build()).queue();
            lastExecutionMillis = now;
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
        HttpClient httpclient = new DefaultHttpClient();
        try {
            HttpGet httpget = new HttpGet("https://api.thecatapi.com/v1/images/search/");

            System.out.println("executing request " + httpget.getURI());

            // Create a response handler
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            // Body contains your json stirng
            String responseBody = null;
            try {
                responseBody = httpclient.execute(httpget, responseHandler);
            } catch (IOException e) {
                log.warn("Unable to fetch cat.", e);
                return "https://http.cat/500";
            }
            try {
                return (String) ((Map) new ObjectMapper().readValue(responseBody, List.class).get(0)).get("url");
            } catch (JsonProcessingException e) {
                log.warn("Unable to read cat response.", e);
                return "https://http.cat/400";
            }

        } finally {
            // When HttpClient instance is no longer needed,
            // shut down the connection manager to ensure
            // immediate deallocation of all system resources
            httpclient.getConnectionManager().shutdown();
        }
    }

}
