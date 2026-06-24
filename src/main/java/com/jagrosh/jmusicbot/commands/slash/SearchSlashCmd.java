package com.jagrosh.jmusicbot.commands.slash;

import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.audio.AudioHandler;
import com.jagrosh.jmusicbot.audio.QueuedTrack;
import com.jagrosh.jmusicbot.audio.RequestMetadata;
import com.jagrosh.jmusicbot.audio.RequestMetadata.RequestInfo;
import com.jagrosh.jmusicbot.utils.FormatUtil;
import com.jagrosh.jmusicbot.utils.TimeUtil;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import java.util.List;

public class SearchSlashCmd extends SlashMusicCommand
{
    public SearchSlashCmd(Bot bot)
    {
        super(bot);
        this.name = "search";
        this.help = "searches YouTube for a query";
        this.options = List.of(
            new OptionData(OptionType.STRING, "query", "search query", true)
        );
        this.beListening = true;
    }

    @Override
    protected void doCommand(SlashCommandEvent event)
    {
        String query = event.getOption("query").getAsString();
        event.deferReply().queue();

        bot.getPlayerManager().loadItemOrdered(event.getGuild(), "ytsearch:" + query, new AudioLoadResultHandler()
        {
            @Override
            public void trackLoaded(AudioTrack track)
            {
                addAndReply(track, event);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist)
            {
                if (playlist.getTracks().isEmpty())
                {
                    event.getHook().sendMessage(getClient().getWarning() + " No results found for `" + query + "`.").queue();
                    return;
                }
                StringBuilder sb = new StringBuilder(getClient().getSuccess() + " Top 5 results for `" + query + "`:\n");
                List<AudioTrack> tracks = playlist.getTracks();
                for (int i = 0; i < Math.min(5, tracks.size()); i++)
                {
                    AudioTrack track = tracks.get(i);
                    sb.append("\n`").append(i + 1).append(".` **").append(track.getInfo().title).append("** – `").append(TimeUtil.formatTime(track.getDuration())).append("`");
                }
                sb.append("\n\nUse `/play` directly with the title or URL to play a specific result.");
                event.getHook().sendMessage(FormatUtil.filter(sb.toString())).queue();
            }

            @Override
            public void noMatches()
            {
                event.getHook().sendMessage(getClient().getWarning() + " No results found for `" + query + "`.").queue();
            }

            @Override
            public void loadFailed(FriendlyException e)
            {
                event.getHook().sendMessage(getClient().getError() + " Failed to search: " + e.getMessage()).queue();
            }
        });
    }

    private void addAndReply(AudioTrack track, SlashCommandEvent event)
    {
        if (bot.getConfig().isTooLong(track))
        {
            event.getHook().sendMessage(getClient().getWarning() + " This track (**" + track.getInfo().title + "**) is longer than the allowed maximum.").queue();
            return;
        }
        AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
        int pos = handler.addTrack(new QueuedTrack(track, new RequestMetadata(event.getUser(), new RequestInfo(null, track.getInfo().uri)))) + 1;
        event.getHook().sendMessage(getClient().getSuccess() + " Added **" + track.getInfo().title + "** (`" + TimeUtil.formatTime(track.getDuration()) + "`) " + (pos == 0 ? "to begin playing" : " to the queue at position " + pos)).queue();
    }
}
