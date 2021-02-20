/*
 * Copyright 2018 John Grosh <john.a.grosh@gmail.com>.
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
import com.jagrosh.jmusicbot.audio.AudioHandler;
import com.jagrosh.jmusicbot.commands.DJCommand;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;

/**
 *
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class DisconnectAllCmd extends DJCommand
{
    public DisconnectAllCmd(Bot bot)
    {
        super(bot);
        this.name = "dcall";
        this.help = "disconnect all bots from your voice channel";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.bePlaying = false;
    }

    @Override
    public void doCommand(CommandEvent event) 
    {
        GuildVoiceState state = event.getMember().getVoiceState();
        if (state == null || !state.inVoiceChannel()) {
            event.replyError("You need to be in a voice channel.");
            return;
        }
        long channel = state.getChannel().getIdLong();
        event.getGuild().loadMembers().onSuccess(members -> {
            for (Member mem : members) {
                if (mem.getUser().isBot() && mem.getVoiceState() != null && mem.getVoiceState().getChannel().getIdLong() == channel) {
                    event.getGuild().moveVoiceMember(mem, null).queue(e -> {System.out.println(e);}, e -> e.printStackTrace());
                }
            }
        });
        event.replySuccess("Disconnected the bots.");
    }
}
