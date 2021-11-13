/*
 * Copyright 2021 John Grosh <john.a.grosh@gmail.com>.
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
package com.jagrosh.jmusicbot.commands.dj;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.commands.DJCommand;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.exceptions.PermissionException;
import net.dv8tion.jda.api.managers.AudioManager;

import java.util.Objects;
import java.util.Optional;

/**
 *
 * @author UnrealValentin
 */
public class JoinCmd extends DJCommand
{

    public JoinCmd(Bot bot)
    {
        super(bot);
        this.name = "join";
        this.help = "makes the bot join a voice channel";
        this.arguments = "[channel]";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.bePlaying = false;
        this.beListening = false;
    }

    @Override
    public void doCommand(CommandEvent event)
    {

        AudioManager manager = event.getGuild().getAudioManager();
        VoiceChannel vc;

        if(event.getArgs().isEmpty()) {

            vc = Objects.requireNonNull(event.getMember().getVoiceState()).getChannel();

        } else {

            // I made the code simple cause I don't think someone will have multiple channels with the same name...
            // I just hope we don't need a menu like in search here!

            Optional<VoiceChannel> maybe = event.getGuild().getVoiceChannels().stream()
                    .filter(voiceChannel -> voiceChannel.getName().equalsIgnoreCase(event.getArgs())).findFirst();

            if(maybe.isPresent()) {
                vc = maybe.get();
            } else {
                event.reply(":x: Channel not found! Check your spelling!");
                return;
            }

        }

        if (vc == null) {
            event.reply(":x: You need to be in a channel to use this or provide a Voice Channel for the bot to join!");
        }

        try {
            manager.openAudioConnection(vc);
        } catch (PermissionException permissionException) {
            event.reply(event.getClient().getError()+" I am unable to connect to **"+vc.getName()+"**! I don't have the permissions!");
            return;
        }

        event.reply("Joined " + vc.getName());

    }
}
