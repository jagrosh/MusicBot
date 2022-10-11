package com.jagrosh.jmusicbot.commands.admin;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.commands.AdminCommand;
import com.jagrosh.jmusicbot.settings.Settings;

/**
 *
 * @author Omar Sanchez <omarsanchezdev@gmail.com>
 */

public class SetPermissionListSettingsCmd extends AdminCommand
{
    public SetPermissionListSettingsCmd(Bot bot)
    {
        this.name = "setlist";
        this.help = "enable, disable, black/white list for music commands";
        this.arguments = "<listType> <action>";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.guildOnly = true;
    }

    @Override
    protected void execute(CommandEvent event)
    {
        if(event.getArgs().isEmpty())
        {
            event.reply(event.getClient().getError()+" Please include a list (whitelist/blacklist) to enable or disable");
        }
        else if(event.getArgs().split("\\s+").length < 2)
        {
            event.reply(event.getClient().getError()+" Please include a list type (blacklist/whitelist) and an action (enable/disable");
        }
        else
        {
            Settings s = event.getClient().getSettingsFor(event.getGuild());
            String[] args = event.getArgs().split("\\s+");
            String listType = args[0];
            String action = args[1];

            if (!listType.equalsIgnoreCase("blacklist") && !listType.equalsIgnoreCase("whitelist"))
            {
                event.reply(event.getClient().getError()+" Please include a valid list type (blacklist/whitelist)");
            }
            else if (!action.equalsIgnoreCase("enable") && !action.equalsIgnoreCase("disable"))
            {
                event.reply(event.getClient().getError()+" Please include a valid action (enable/disable)");
            }
            else
            {
                if(listType.equalsIgnoreCase("blacklist"))
                {
                    String resp = s.setPermissionListSettings(listType, action);
                    event.reply(resp);
                }
                else if (listType.equalsIgnoreCase("whitelist"))
                {
                    String resp = s.setPermissionListSettings(listType, action);
                    event.reply(resp);
                }
            }
        }
    }
}
