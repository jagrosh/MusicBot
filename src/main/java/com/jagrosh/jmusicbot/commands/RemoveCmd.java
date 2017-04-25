/*
 * Copyright 2016 John Grosh <john.a.grosh@gmail.com>.
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
package com.jagrosh.jmusicbot.commands;

import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.audio.AudioHandler;
import com.jagrosh.jmusicbot.audio.QueuedTrack;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.utils.PermissionUtil;

/**
 *
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class RemoveCmd extends MusicCommand {

    public RemoveCmd(Bot bot)
    {
        super(bot);
        this.name = "remove";
        this.help = "removes a song from the queue";
        this.arguments = "<position|ALL>";
        this.aliases = new String[]{"delete"};
        this.beListening = true;
        this.bePlaying = true;
    }

    @Override
    public void doCommand(CommandEvent event) {
        AudioHandler handler = (AudioHandler)event.getGuild().getAudioManager().getSendingHandler();
        if(handler.getQueue().isEmpty())
        {
            event.reply(event.getClient().getError()+" There is nothing in the queue!");
            return;
        }
        if(event.getArgs().equalsIgnoreCase("all"))
        {
            int count = handler.getQueue().removeAll(event.getAuthor().getId());
            if(count==0)
                event.reply(event.getClient().getWarning()+" You don't have any songs in the queue!");
            else
                event.reply(event.getClient().getSuccess()+" Successfully removed your "+count+" entries.");
            return;
        }
        int pos;
        try {
            pos = Integer.parseInt(event.getArgs());
        } catch(NumberFormatException e) {
            pos = 0;
        }
        if(pos<1 || pos>handler.getQueue().size())
        {
            event.reply(event.getClient().getError()+" Position must be a valid integer between 1 and "+handler.getQueue().size()+"!");
            return;
        }
        boolean isDJ = PermissionUtil.checkPermission(event.getGuild(), event.getMember(), Permission.MANAGE_SERVER);
        if(!isDJ)
            isDJ = event.getMember().getRoles().contains(event.getGuild().getRoleById(bot.getSettings(event.getGuild()).getRoleId()));
        QueuedTrack qt = handler.getQueue().get(pos-1);
        if(qt.getIdentifier().equals(event.getAuthor().getId()))
        {
            handler.getQueue().remove(pos-1);
            event.reply(event.getClient().getSuccess()+" Removed **"+qt.getTrack().getInfo().title+"** from the queue");
        }
        else if(isDJ)
        {
            handler.getQueue().remove(pos-1);
            User u = event.getJDA().getUserById(qt.getIdentifier());
            event.reply(event.getClient().getSuccess()+" Removed **"+qt.getTrack().getInfo().title
                    +"** from the queue (requested by "+(u==null ? "someone" : "**"+u.getName()+"**")+")");
        }
        else
        {
            event.reply(event.getClient().getError()+" You cannot remove **"+qt.getTrack().getInfo().title+"** because you didn't add it!");
        }
    }
    
}
