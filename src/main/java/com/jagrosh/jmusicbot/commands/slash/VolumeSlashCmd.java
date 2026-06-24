package com.jagrosh.jmusicbot.commands.slash;

import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.audio.AudioHandler;
import com.jagrosh.jmusicbot.settings.Settings;
import com.jagrosh.jmusicbot.utils.FormatUtil;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import java.util.List;

public class VolumeSlashCmd extends SlashDJCommand
{
    public VolumeSlashCmd(Bot bot)
    {
        super(bot);
        this.name = "volume";
        this.help = "sets or shows the volume";
        this.options = List.of(
            new OptionData(OptionType.INTEGER, "level", "volume level (0-150)")
        );
    }

    @Override
    protected void doCommand(SlashCommandEvent event)
    {
        if (!checkDJPermission(event))
        {
            event.reply(getClient().getError() + " You don't have permission to use that!").setEphemeral(true).queue();
            return;
        }
        AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
        Settings settings = getClient().getSettingsFor(event.getGuild());
        int volume = handler.getPlayer().getVolume();

        if (event.getOption("level") == null)
        {
            event.reply(FormatUtil.volumeIcon(volume) + " Current volume is `" + volume + "`").queue();
        }
        else
        {
            int nvolume = (int) event.getOption("level").getAsLong();
            if (nvolume < 0 || nvolume > 150)
            {
                event.reply(getClient().getError() + " Volume must be a valid integer between 0 and 150!").queue();
                return;
            }
            handler.getPlayer().setVolume(nvolume);
            settings.setVolume(nvolume);
            event.reply(FormatUtil.volumeIcon(nvolume) + " Volume changed from `" + volume + "` to `" + nvolume + "`").queue();
        }
    }
}
