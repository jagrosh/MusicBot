package com.jagrosh.jmusicbot.commands.slash;

import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.audio.AudioHandler;
import com.jagrosh.jmusicbot.audio.QueuedTrack;
import com.jagrosh.jmusicbot.settings.RepeatMode;
import com.jagrosh.jmusicbot.settings.Settings;
import com.jagrosh.jmusicbot.utils.FormatUtil;
import com.jagrosh.jmusicbot.utils.TimeUtil;
import java.util.List;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class QueueSlashCmd extends SlashMusicCommand
{
    public QueueSlashCmd(Bot bot)
    {
        super(bot);
        this.name = "queue";
        this.help = "shows the current queue";
        this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
        this.options = List.of(
            new OptionData(OptionType.INTEGER, "page", "page number to view")
        );
    }

    @Override
    protected void doCommand(SlashCommandEvent event)
    {
        int page = event.getOption("page") != null ? (int) event.getOption("page").getAsLong() : 1;
        AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
        List<QueuedTrack> list = handler.getQueue().getList();
        if (list.isEmpty())
        {
            event.reply(handler.getNoMusicPlaying(event.getJDA())).queue();
            return;
        }

        int itemsPerPage = 10;
        int pages = (int) Math.ceil((double) list.size() / itemsPerPage);
        if (page < 1 || page > pages)
        {
            event.reply(getClient().getError() + " Page must be between 1 and " + pages + ".").queue();
            return;
        }

        long total = 0;
        for (QueuedTrack qt : list)
            total += qt.getTrack().getDuration();

        Settings settings = getClient().getSettingsFor(event.getGuild());
        StringBuilder sb = new StringBuilder();
        if (handler.getPlayer().getPlayingTrack() != null)
            sb.append(handler.getStatusEmoji()).append(" **").append(handler.getPlayer().getPlayingTrack().getInfo().title).append("**\n");
        sb.append(getClient().getSuccess()).append(" Queue | ").append(list.size()).append(" entries | `").append(TimeUtil.formatTime(total)).append("`");

        RepeatMode repeat = settings.getRepeatMode();
        if (repeat.getEmoji() != null)
            sb.append(" | ").append(repeat.getEmoji());

        int start = (page - 1) * itemsPerPage;
        int end = Math.min(start + itemsPerPage, list.size());
        sb.append("\n```\n");
        for (int i = start; i < end; i++)
            sb.append(String.format("%2d. %s\n", i + 1, list.get(i).toString()));
        sb.append("```");
        if (pages > 1)
            sb.append("Page ").append(page).append("/").append(pages);

        event.reply(FormatUtil.filter(sb.toString().trim())).queue();
    }
}
