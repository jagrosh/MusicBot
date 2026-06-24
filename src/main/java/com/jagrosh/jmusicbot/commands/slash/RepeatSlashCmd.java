package com.jagrosh.jmusicbot.commands.slash;

import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.settings.RepeatMode;
import com.jagrosh.jmusicbot.settings.Settings;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import java.util.List;

public class RepeatSlashCmd extends SlashDJCommand
{
    public RepeatSlashCmd(Bot bot)
    {
        super(bot);
        this.name = "repeat";
        this.help = "sets repeat mode (off, all, single)";
        this.options = List.of(
            new OptionData(OptionType.STRING, "mode", "repeat mode: off, all, or single")
                .addChoice("off", "off")
                .addChoice("all", "all")
                .addChoice("single", "single")
        );
        this.bePlaying = true;
    }

    @Override
    protected void doCommand(SlashCommandEvent event)
    {
        if (!checkDJPermission(event))
        {
            event.reply(getClient().getError() + " You don't have permission to use that!").setEphemeral(true).queue();
            return;
        }
        Settings settings = (Settings) getClient().getSettingsFor(event.getGuild());
        if (event.getOption("mode") == null)
        {
            RepeatMode current = settings.getRepeatMode();
            event.reply(getClient().getSuccess() + " Repeat mode is currently `" + current.getUserFriendlyName() + "`.").queue();
            return;
        }
        String mode = event.getOption("mode").getAsString();
        RepeatMode newMode;
        switch (mode)
        {
            case "all":
                newMode = RepeatMode.ALL;
                break;
            case "single":
                newMode = RepeatMode.SINGLE;
                break;
            default:
                newMode = RepeatMode.OFF;
        }
        settings.setRepeatMode(newMode);
        event.reply(getClient().getSuccess() + " Repeat mode set to `" + newMode.getUserFriendlyName() + "`.").queue();
    }
}
