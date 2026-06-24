package com.jagrosh.jmusicbot.commands.slash;

import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.audio.AudioHandler;
import com.jagrosh.jmusicbot.audio.RequestMetadata;
import com.jagrosh.jmusicbot.utils.FormatUtil;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public class SkipSlashCmd extends SlashMusicCommand
{
    public SkipSlashCmd(Bot bot)
    {
        super(bot);
        this.name = "skip";
        this.help = "votes to skip the current song";
        this.beListening = true;
        this.bePlaying = true;
    }

    @Override
    protected void doCommand(SlashCommandEvent event)
    {
        AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
        RequestMetadata rm = handler.getRequestMetadata();
        double skipRatio = bot.getSettingsManager().getSettings(event.getGuild()).getSkipRatio();
        if (skipRatio == -1)
            skipRatio = bot.getConfig().getSkipRatio();

        if (event.getUser().getIdLong() == rm.getOwner() || skipRatio == 0)
        {
            event.reply(getClient().getSuccess() + " Skipped **" + handler.getPlayer().getPlayingTrack().getInfo().title + "**").queue();
            handler.getPlayer().stopTrack();
        }
        else
        {
            int listeners = (int) event.getGuild().getSelfMember().getVoiceState().getChannel().getMembers().stream()
                .filter(m -> !m.getUser().isBot() && !m.getVoiceState().isDeafened()).count();
            String msg;
            if (handler.getVotes().contains(event.getUser().getId()))
                msg = getClient().getWarning() + " You already voted to skip this song `[";
            else
            {
                msg = getClient().getSuccess() + " You voted to skip the song `[";
                handler.getVotes().add(event.getUser().getId());
            }
            int skippers = (int) event.getGuild().getSelfMember().getVoiceState().getChannel().getMembers().stream()
                .filter(m -> handler.getVotes().contains(m.getUser().getId())).count();
            int required = (int) Math.ceil(listeners * skipRatio);
            msg += skippers + " votes, " + required + "/" + listeners + " needed]`";
            if (skippers >= required)
            {
                msg += "\n" + getClient().getSuccess() + " Skipped **" + handler.getPlayer().getPlayingTrack().getInfo().title + "** "
                    + (rm.getOwner() == 0L ? "(autoplay)" : "(requested by **" + FormatUtil.formatUsername(rm.user) + "**)");
                handler.getPlayer().stopTrack();
            }
            event.reply(msg).queue();
        }
    }
}
