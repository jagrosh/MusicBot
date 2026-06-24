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
import java.util.List;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class PlaySlashCmd extends SlashMusicCommand
{
    public PlaySlashCmd(Bot bot)
    {
        super(bot);
        this.name = "play";
        this.help = "plays the provided song or URL";
        this.options = List.of(
            new OptionData(OptionType.STRING, "query", "song title or URL", true)
        );
        this.beListening = true;
        this.bePlaying = false;
    }

    @Override
    protected void doCommand(SlashCommandEvent event)
    {
        String query = event.getOption("query").getAsString();
        event.deferReply().queue();
        InteractionHook hook = event.getHook();

        bot.getPlayerManager().loadItemOrdered(event.getGuild(), query, new AudioLoadResultHandler()
        {
            @Override
            public void trackLoaded(AudioTrack track)
            {
                loadSingle(track, null, hook, event);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist)
            {
                if (playlist.getTracks().size() == 1 || playlist.isSearchResult())
                {
                    AudioTrack single = playlist.getSelectedTrack() == null ? playlist.getTracks().get(0) : playlist.getSelectedTrack();
                    loadSingle(single, null, hook, event);
                }
                else if (playlist.getSelectedTrack() != null)
                {
                    loadSingle(playlist.getSelectedTrack(), playlist, hook, event);
                }
                else
                {
                    int count = loadPlaylist(playlist, null, hook, event);
                    if (count == 0)
                        hook.sendMessage(getClient().getWarning() + " All entries in this playlist were longer than the allowed maximum (`" + bot.getConfig().getMaxTime() + "`)").queue();
                    else
                        hook.sendMessage(getClient().getSuccess() + " Found " + (playlist.getName() == null ? "a playlist" : "playlist **" + playlist.getName() + "**") + " with `" + playlist.getTracks().size() + "` entries; added to the queue!" + (count < playlist.getTracks().size() ? "\n" + getClient().getWarning() + " Tracks longer than the allowed maximum (`" + bot.getConfig().getMaxTime() + "`) have been omitted." : "")).queue();
                }
            }

            @Override
            public void noMatches()
            {
                bot.getPlayerManager().loadItemOrdered(event.getGuild(), "ytsearch:" + query, new AudioLoadResultHandler()
                {
                    @Override
                    public void trackLoaded(AudioTrack track) { loadSingle(track, null, hook, event); }

                    @Override
                    public void playlistLoaded(AudioPlaylist playlist)
                    {
                        if (playlist.getTracks().isEmpty())
                        {
                            hook.sendMessage(getClient().getWarning() + " No results found for `" + query + "`.").queue();
                            return;
                        }
                        AudioTrack single = playlist.getTracks().get(0);
                        loadSingle(single, null, hook, event);
                    }

                    @Override
                    public void noMatches()
                    {
                        hook.sendMessage(getClient().getWarning() + " No results found for `" + query + "`.").queue();
                    }

                    @Override
                    public void loadFailed(FriendlyException e)
                    {
                        hook.sendMessage(getClient().getError() + " Failed to load track: " + e.getMessage()).queue();
                    }
                });
            }

            @Override
            public void loadFailed(FriendlyException e)
            {
                hook.sendMessage(getClient().getError() + " Failed to load: " + e.getMessage()).queue();
            }
        });
    }

    private void loadSingle(AudioTrack track, AudioPlaylist playlist, InteractionHook hook, SlashCommandEvent event)
    {
        if (bot.getConfig().isTooLong(track))
        {
            hook.sendMessage(getClient().getWarning() + " This track (**" + track.getInfo().title + "**) is longer than the allowed maximum (`" + TimeUtil.formatTime(track.getDuration()) + "` > `" + bot.getConfig().getMaxTime() + "`)").queue();
            return;
        }
        AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
        int pos = handler.addTrack(new QueuedTrack(track, new RequestMetadata(event.getUser(), new RequestInfo(null, track.getInfo().uri)))) + 1;
        String msg = getClient().getSuccess() + " Added **" + track.getInfo().title + "** (`" + TimeUtil.formatTime(track.getDuration()) + "`) " + (pos == 0 ? "to begin playing" : " to the queue at position " + pos);
        if (playlist == null || playlist.getTracks().size() <= 1)
            hook.sendMessage(FormatUtil.filter(msg)).queue();
        else
            hook.sendMessage(FormatUtil.filter(msg + "\n" + getClient().getWarning() + " This track has a playlist of **" + playlist.getTracks().size() + "** tracks attached.")).queue();
    }

    private int loadPlaylist(AudioPlaylist playlist, AudioTrack exclude, InteractionHook hook, SlashCommandEvent event)
    {
        int[] count = {0};
        playlist.getTracks().stream().forEach(track ->
        {
            if (!bot.getConfig().isTooLong(track) && !track.equals(exclude))
            {
                AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
                handler.addTrack(new QueuedTrack(track, new RequestMetadata(event.getUser(), new RequestInfo(null, track.getInfo().uri))));
                count[0]++;
            }
        });
        return count[0];
    }
}
