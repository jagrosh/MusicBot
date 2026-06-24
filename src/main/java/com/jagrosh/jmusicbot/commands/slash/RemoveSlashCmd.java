package com.jagrosh.jmusicbot.commands.slash;

import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.audio.AudioHandler;
import com.jagrosh.jmusicbot.audio.QueuedTrack;
import com.jagrosh.jmusicbot.utils.FormatUtil;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import java.util.List;

public class RemoveSlashCmd extends SlashMusicCommand
{
    public RemoveSlashCmd(Bot bot)
    {
        super(bot);
        this.name = "remove";
        this.help = "removes a song from the queue by position";
        this.options = List.of(
            new OptionData(OptionType.INTEGER, "position", "position in the queue to remove", true)
        );
        this.beListening = true;
        this.bePlaying = true;
    }

    @Override
    protected void doCommand(SlashCommandEvent event)
    {
        int pos = (int) event.getOption("position").getAsLong() - 1;
        AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
        if (pos < 0 || pos >= handler.getQueue().size())
        {
            event.reply(getClient().getError() + " Position must be between 1 and " + handler.getQueue().size() + "!").queue();
            return;
        }
        QueuedTrack track = handler.getQueue().remove(pos);
        event.reply(getClient().getSuccess() + " Removed **" + FormatUtil.filter(track.getTrack().getInfo().title) + "** from the queue.").queue();
    }
}
