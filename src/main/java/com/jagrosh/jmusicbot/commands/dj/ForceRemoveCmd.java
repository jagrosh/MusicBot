/*
 * Copyright 2019 John Grosh <john.a.grosh@gmail.com>.
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
import com.jagrosh.jmusicbot.audio.AudioHandler;
import com.jagrosh.jmusicbot.commands.DJCommand;
import com.jagrosh.jmusicbot.utils.FormatUtil;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;

import java.util.regex.Matcher;

/**
 *
 * @author Michaili K.
 */
public class ForceRemoveCmd extends DJCommand
{
    public ForceRemoveCmd(Bot bot)
    {
        super(bot);
        this.name = "forceremove";
        this.help = "removes all entries by a user from the queue";
        this.arguments = "<user>";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.beListening = false;
        this.bePlaying = true;
        this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
    }

    @Override
    public void doCommand(CommandEvent event)
    {
        if (event.getArgs().isEmpty())
        {
            event.replyError("You need to mention a user!");
            return;
        }

        AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
        if (handler.getQueue().isEmpty())
        {
            event.replyError("There is nothing in the queue!");
            return;
        }


        User target = findUser(event.getArgs());

        if (target == null)
        {
            event.replyError("Unable to find the user!");
            return;
        }

        removeAllEntries(target, event);

    }

    private User findUser(String query)
    {
        Matcher userMention = FinderUtil.USER_MENTION.matcher(query);
        Matcher fullRefMatch = FinderUtil.FULL_USER_REF.matcher(query);
        Matcher discordIdMatch = FinderUtil.DISCORD_ID.matcher(query);
        if(userMention.matches() || discordIdMatch.matches())
        {
            String stringId;
            if (userMention.matches()) 
            {
                stringId = query.replaceAll("[^0-9]", "");
            }
            else 
            {
                stringId = query;
            }
            long userId;
            try 
            {
                userId = Long.parseLong(stringId);
            } 
            catch (NumberFormatException e) 
            {
                return null;
            }
            return bot.getJDA().retrieveUserById(userId).complete();
        }
        else if(fullRefMatch.matches())
        {
            String username = fullRefMatch.group(1).toLowerCase() + "#" + fullRefMatch.group(2);
        
            return bot.getJDA().getUserByTag(username);
        }
        return null;
    }

    private void removeAllEntries(User target, CommandEvent event)
    {
        int count = ((AudioHandler) event.getGuild().getAudioManager().getSendingHandler()).getQueue().removeAll(target.getIdLong());
        if (count == 0)
        {
            event.replyWarning("**"+target.getName()+"** doesn't have any songs in the queue!");
        }
        else
        {
            event.replySuccess("Successfully removed `"+count+"` entries from "+FormatUtil.formatUsername(target)+".");
        }
    }
}
