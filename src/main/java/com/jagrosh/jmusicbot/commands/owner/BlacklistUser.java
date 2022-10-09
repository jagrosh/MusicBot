package com.jagrosh.jmusicbot.commands.owner;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.commands.OwnerCommand;
import com.jagrosh.jmusicbot.settings.Settings;
import static com.jagrosh.jmusicbot.utils.OtherUtil.isValidUserID;

/**
 *
 * @author Omar Sanchez <omarsanchezdev@gmail.com>
 */

public class BlacklistUser extends OwnerCommand
{
    public BlacklistUser(Bot bot)
    {
        this.name = "blacklist";
        this.help = "blocks or unblocks a user from using commands";
        this.arguments = "<action>|<user>";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.guildOnly = false;
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
            event.reply(event.getClient().getError()+" Please include a action (add/remove) and a user (@ExampleUser");
        }
        else
        {
            Settings s = event.getClient().getSettingsFor(event.getGuild());
            String[] args = event.getArgs().split("\\s+");
            String action = args[0];
            String user = args[1];

            if (!action.equalsIgnoreCase("add") && !action.equalsIgnoreCase("remove"))
            {
                event.reply(event.getClient().getError()+" Please include a valid action (add/remove)");
            }
            else if (!isValidUserID(user))
            {
                event.reply(event.getClient().getError()+" Please include a valid user (@ExampleUser)");
            }
            else
            {
                s.setBlacklistedUsers(args[0], args[1]);
                String response = String.format("%s %s blacklist 📃", user, action.equalsIgnoreCase("add") ? "added to" : "removed from");
                event.reply(response);
            }
        }
    }
}
