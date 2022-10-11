package com.jagrosh.jmusicbot.commands.admin;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.utils.FinderUtil;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.commands.AdminCommand;
import com.jagrosh.jmusicbot.settings.Settings;
import com.jagrosh.jmusicbot.utils.FormatUtil;
import net.dv8tion.jda.api.entities.Role;
import java.util.List;
import static com.jagrosh.jmusicbot.utils.OtherUtil.checkUserOrRole;
import static com.jagrosh.jmusicbot.utils.OtherUtil.parseUserID;

/**
 *
 * @author Omar Sanchez <omarsanchezdev@gmail.com>
 */

public class SetUsageListUserCmd extends AdminCommand
{
    public SetUsageListUserCmd(Bot bot)
    {
        this.name = "setusage";
        this.help = "blocks or unblocks a user/role from using commands";
        this.arguments = "<action> <user|role>";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.guildOnly = true;
    }

    @Override
    protected void execute(CommandEvent event)
    {
        if(event.getArgs().isEmpty())
        {
            event.replyError("Please include a user or role");
        }
        else if(event.getArgs().split("\\s+").length < 2)
        {
            event.reply(event.getClient().getError()+" Please include an action (add/remove) and a user or role (@ExampleUser/@admins)");
        }
        else
        {
            Settings s = event.getClient().getSettingsFor(event.getGuild());
            String[] args = event.getArgs().split("\\s+");
            String action = args[0];
            String userOrRole = args[1];

            if (!action.equalsIgnoreCase("add") && !action.equalsIgnoreCase("remove"))
            {
                event.reply(event.getClient().getError()+" Please include a valid action (add/remove)");
            }
            else if (userOrRole == null)
            {
                event.reply(event.getClient().getError()+" Please include a valid user or role (@ExampleUser)");
            }
            else
            {
                String argType =  checkUserOrRole(args[1]);

                if(argType.equalsIgnoreCase("user"))
                {
                    String user_id = parseUserID(userOrRole);
                    s.setUsageList(action, user_id);
                    String response = String.format("%s %s usage list 📃", userOrRole, action.equalsIgnoreCase("add") ? "added to" : "removed from");
                    event.reply(response);
                }
                else if (argType.equalsIgnoreCase("role"))
                {
                    List<Role> list = FinderUtil.findRoles(userOrRole, event.getGuild());
                    if(list.isEmpty())
                        event.reply(event.getClient().getWarning()+" No Roles found matching \""+userOrRole+"\"");
                    else if (list.size()>1)
                        event.reply(event.getClient().getWarning()+ FormatUtil.listOfRoles(list, event.getArgs()));
                    else
                    {
                        Role role = list.get(0);
                        long roleID = role.getIdLong();
                        s.setUsageList(action, String.valueOf(roleID));
                        String response = String.format("%s %s usage list 📃", role.getName(), action.equalsIgnoreCase("add") ? "added to" : "removed from");
                        event.reply(response);
                    }
                }

            }
        }
    }
}
