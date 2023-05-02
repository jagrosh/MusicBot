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
package com.jagrosh.jmusicbot.commands.admin;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.utils.FinderUtil;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.commands.AdminCommand;
import com.jagrosh.jmusicbot.settings.Settings;
import com.jagrosh.jmusicbot.utils.FormatUtil;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;

import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class BlacklistUserCmd extends AdminCommand
{
    public BlacklistUserCmd(Bot bot)
    {
        this.name = "forbid";
        this.help = "allow/disallow user from issuing commands";
        this.arguments = "<user>";
        this.aliases = bot.getConfig().getAliases(this.name);
    }
    
    @Override
    protected void execute(CommandEvent event) 
    {
        if(event.getArgs().isEmpty())
        {
            event.replyError("You need to @mention a user!");
            return;
        }
        Settings s = event.getClient().getSettingsFor(event.getGuild());
        if(event.getArgs().equalsIgnoreCase("none"))
        {
            s.clearBlacklistedUsers();
            event.replySuccess("Blacklist cleared; All users can use commands.");
            return;
        }
        String userId = getUserIdFromArgs(event.getArgs());
        if (userId == "") {
            event.replyError("You must @mention a user!");
            return;
        }
        if (s.getBlacklistedUsers().contains(userId))
        {
            s.removeBlacklistedUser(userId);
            event.replySuccess("User "+ event.getArgs() + " can now use commands");
        } else
        {
            s.setBlacklistedUser(userId);
            event.replySuccess("User "+ event.getArgs() + " can no longer use commands");
        }
    }

    private String getUserIdFromArgs(String args) {
        Pattern pattern = Pattern.compile("^<@(\\d{18})>$");
        Matcher matcher = pattern.matcher(args);
        if (!matcher.find()) {
            return "";
        }
        MatchResult match = matcher.toMatchResult();
        String userId = match.group(1);
        return userId;
    }
}
