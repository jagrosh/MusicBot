package com.jagrosh.jmusicbot.commands.admin;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.commands.AdminCommand;
import com.jagrosh.jmusicbot.settings.Settings;
import static com.jagrosh.jmusicbot.utils.OtherUtil.parseUserID;

/**
 *
 * @author Omar Sanchez <omarsanchezdev@gmail.com>
 */

public class WhitelistUserCmd extends AdminCommand
{
    public WhitelistUserCmd(Bot bot)
    {
        this.name = "whitelist";
        this.help = "allows or disallows a user from using commands";
        this.arguments = "<action> <user>";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.guildOnly = true;
    }

    @Override
    protected void execute(CommandEvent event)
    {
        if(event.getArgs().isEmpty())
        {
            event.reply(event.getClient().getError()+" Please include a user");
        }
        else if(event.getArgs().split("\\s+").length < 2)
        {
            event.reply(event.getClient().getError()+" Please include a action (add/remove) and a user (@ExampleUser)");
        }
        else
        {
            Settings s = event.getClient().getSettingsFor(event.getGuild());
            String[] args = event.getArgs().split("\\s+");
            String action = args[0];
            String user = args[1];
            String user_id = parseUserID(args[1]);

            if (!action.equalsIgnoreCase("add") && !action.equalsIgnoreCase("remove"))
            {
                event.reply(event.getClient().getError()+" Please include a valid action (add/remove)");
            }
            else if (user == null)
            {
                event.reply(event.getClient().getError()+" Please include a valid user (@ExampleUser)");
            }
            else
            {
                s.setWhitelistUsers(action, user_id);
                String response = String.format("%s %s whitelist 📃", user, action.equalsIgnoreCase("add") ? "added to" : "removed from");
                event.reply(response);
            }
        }
    }
}
