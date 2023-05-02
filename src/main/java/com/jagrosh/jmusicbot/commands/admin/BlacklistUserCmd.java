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
import com.jagrosh.jdautilities.menu.OrderedMenu;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.commands.AdminCommand;
import com.jagrosh.jmusicbot.settings.Settings;
import com.jagrosh.jmusicbot.utils.FormatUtil;
import com.sun.org.apache.xpath.internal.operations.String;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;

import java.text.Format;
import java.util.List;
import java.util.concurrent.TimeUnit;
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
        this.name = "blacklist";
        this.help = "allow/disallow user from issuing commands";
        this.arguments = "<user>";
        this.aliases = bot.getConfig().getAliases(this.name);
    }
    
    @Override
    protected void execute(CommandEvent event) 
    {
        if(event.getArgs().isEmpty())
        {
            event.replyError("You need to mention a user!");
            return;
        }
        Settings s = event.getClient().getSettingsFor(event.getGuild());
        if(event.getArgs().equalsIgnoreCase("none"))
        {
            s.clearBlacklistedUsers();
            event.replySuccess("Blacklist cleared; All users can use commands.");
            return;
        }

        User target;
        List<Member> found = FinderUtil.findMembers(event.getArgs(), event.getGuild());
        if(found.isEmpty())
        {
            event.replyError("Unable to find the user!");
            return;
        }
        else if(found.size()>1)
        {
            StringBuilder builder = new StringBuilder();
            for(int i=0; i<found.size() && i<4; i++)
            {
                Member member = found.get(i);
                builder.append("\n**"+member.getUser().getName()+"**#"+member.getUser().getDiscriminator());
            }
            event.replyWarning("Found multiple users: " + builder.toString());
            return;
        }
        else
        {
            target = found.get(0).getUser();
        }
        handleBlacklistUser(target, event);
    }

    private void handleBlacklistUser(User target, CommandEvent event) {
        Settings s = event.getClient().getSettingsFor(event.getGuild());
        if (s.getBlacklistedUsers().contains(target.getId()))
        {
            s.removeBlacklistedUser(target.getId());
            event.replySuccess("User "+ event.getArgs() + " can now use commands");
        } else
        {
            s.setBlacklistedUser(target.getId());
            event.replySuccess("User "+ event.getArgs() + " can no longer use commands");
        }
    }
}
