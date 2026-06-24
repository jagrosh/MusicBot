package com.jagrosh.jmusicbot.commands.slash;

import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.audio.AudioHandler;
import com.jagrosh.jmusicbot.audio.QueuedTrack;
import com.jagrosh.jmusicbot.audio.RequestMetadata;
import com.jagrosh.jmusicbot.audio.RequestMetadata.RequestInfo;
import com.jagrosh.jmusicbot.utils.FormatUtil;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import java.util.List;

public class PlaynextSlashCmd extends SlashDJCommand
{
    public PlaynextSlashCmd(Bot bot)
    {
        super(bot);
        this.name = "playnext";
        this.help = "plays a song next in the queue (DJ only)";
        this.options = List.of(
            new OptionData(OptionType.STRING, "query", "song title or URL", true)
        );
        this.beListening = true;
    }

    @Override
    protected void doCommand(SlashCommandEvent event)
    {
        if (!checkDJPermission(event))
        {
            event.reply(getClient().getError() + " You don't have permission to use that!").setEphemeral(true).queue();
            return;
        }
        String query = event.getOption("query").getAsString();
        event.deferReply().queue();
        InteractionHook hook = event.getHook();

        bot.getPlayerManager().loadItemOrdered(event.getGuild(), query, new AudioLoadResultHandler()
        {
            @Override
            public void trackLoaded(AudioTrack track)
            {
                addNext(track, hook, event);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist)
            {
                if (playlist.isSearchResult() || playlist.getTracks().size() == 1)
                {
                    AudioTrack track = playlist.getTracks().isEmpty() ? null : playlist.getTracks().get(0);
                    if (track == null)
                        hook.sendMessage(getClient().getWarning() + " No results found.").queue();
                    else
                        addNext(track, hook, event);
                }
                else
                {
                    hook.sendMessage(getClient().getWarning() + " Playlists can only be added to the end of the queue. Use `/play` instead.").queue();
                }
            }

            @Override
            public void noMatches()
            {
                bot.getPlayerManager().loadItemOrdered(event.getGuild(), "ytsearch:" + query, this);
            }

            @Override
            public void loadFailed(FriendlyException e)
            {
                hook.sendMessage(getClient().getError() + " Failed to load: " + e.getMessage()).queue();
            }
        });
    }

    private void addNext(AudioTrack track, InteractionHook hook, SlashCommandEvent event)
    {
        AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
        handler.addTrackToFront(new QueuedTrack(track, new RequestMetadata(event.getUser(), new RequestInfo(null, track.getInfo().uri))));
        hook.sendMessage(getClient().getSuccess() + " **" + FormatUtil.filter(track.getInfo().title) + "** will play next.").queue();
    }
}
