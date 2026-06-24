package com.jagrosh.jmusicbot.commands.slash;

import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.settings.Settings;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public abstract class SlashDJCommand extends SlashMusicCommand
{
    public SlashDJCommand(Bot bot)
    {
        super(bot);
        this.category = new Category("DJ");
    }

    public boolean checkDJPermission(SlashCommandEvent event)
    {
        if (event.getUser().getIdLong() == bot.getConfig().getOwnerId())
            return true;
        if (event.getGuild() == null)
            return true;
        if (event.getMember().hasPermission(Permission.MANAGE_SERVER))
            return true;
        Settings settings = (Settings) getClient().getSettingsFor(event.getGuild());
        Role dj = settings.getRole(event.getGuild());
        return dj != null && (event.getMember().getRoles().contains(dj) || dj.getIdLong() == event.getGuild().getIdLong());
    }
}
