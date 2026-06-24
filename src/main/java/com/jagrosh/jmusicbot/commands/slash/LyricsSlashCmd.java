package com.jagrosh.jmusicbot.commands.slash;

import com.jagrosh.jlyrics.LyricsClient;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.audio.AudioHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import java.util.List;

public class LyricsSlashCmd extends SlashMusicCommand
{
    private final LyricsClient client = new LyricsClient();

    public LyricsSlashCmd(Bot bot)
    {
        super(bot);
        this.name = "lyrics";
        this.help = "shows the lyrics of the current or specified song";
        this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
        this.options = List.of(
            new OptionData(OptionType.STRING, "song", "song title to search for lyrics")
        );
    }

    @Override
    protected void doCommand(SlashCommandEvent event)
    {
        String title;
        if (event.getOption("song") != null)
        {
            title = event.getOption("song").getAsString();
        }
        else
        {
            AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
            if (!handler.isMusicPlaying(event.getJDA()))
            {
                event.reply(getClient().getError() + " There must be music playing to use that!").queue();
                return;
            }
            title = handler.getPlayer().getPlayingTrack().getInfo().title;
        }

        event.deferReply().queue();
        client.getLyrics(title).thenAccept(lyrics ->
        {
            if (lyrics == null)
            {
                event.getHook().sendMessage(getClient().getWarning() + " Lyrics for `" + title + "` could not be found!").queue();
                return;
            }
            EmbedBuilder eb = new EmbedBuilder()
                .setAuthor(lyrics.getAuthor())
                .setColor(event.getMember().getColor())
                .setTitle(lyrics.getTitle(), lyrics.getURL());
            if (lyrics.getContent().length() > 15000)
            {
                event.getHook().sendMessage(getClient().getWarning() + " Lyrics for `" + title + "` found but likely not correct: " + lyrics.getURL()).queue();
            }
            else if (lyrics.getContent().length() > 2000)
            {
                event.getHook().sendMessageEmbeds(eb.setDescription(lyrics.getContent().substring(0, 2000)).build()).queue();
            }
            else
            {
                event.getHook().sendMessageEmbeds(eb.setDescription(lyrics.getContent()).build()).queue();
            }
        });
    }
}
