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
import com.jagrosh.jdautilities.commons.utils.FinderUtil;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.commands.DJCommand;
import com.jagrosh.jmusicbot.utils.FormatUtil;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.exceptions.PermissionException;
import net.dv8tion.jda.api.managers.AudioManager;

import java.util.List;

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

        if(event.getArgs().isEmpty())
        {
            GuildVoiceState state = event.getMember().getVoiceState();
            if(state == null)
                vc = null;
            else
                vc = state.getChannel();
        }
        else
        {
            List<VoiceChannel> list = FinderUtil.findVoiceChannels(event.getArgs(), event.getGuild());
            if(list.isEmpty())
            {
                event.replyWarning("No Voice Channels found matching \""+event.getArgs()+"\"");
                return;
            }
            if(list.size()>1)
            {
                event.replyWarning(FormatUtil.listOfVChannels(list, event.getArgs()));
                return;
            }
            vc = list.get(0);
        }

        if(vc == null)
        {
            event.replyError("You need to be in a channel to use this or provide a Voice Channel for the bot to join!");
            return;
        }

        try
        {
            manager.openAudioConnection(vc);
        }
        catch (PermissionException permissionException)
        {
            event.replyError("I am unable to connect to **"+vc.getName()+"**!");
            return;
        }

        event.replySuccess("Joined "+vc.getName());
    }
}
