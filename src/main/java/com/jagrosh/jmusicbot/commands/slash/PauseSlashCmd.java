package com.jagrosh.jmusicbot.commands.slash;

import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.audio.AudioHandler;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public class PauseSlashCmd extends SlashDJCommand
{
    public PauseSlashCmd(Bot bot)
    {
        super(bot);
        this.name = "pause";
        this.help = "pauses or resumes the current song";
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
        AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
        boolean paused = handler.getPlayer().isPaused();
        handler.getPlayer().setPaused(!paused);
        if (paused)
            event.reply(getClient().getSuccess() + " Resumed **" + handler.getPlayer().getPlayingTrack().getInfo().title + "**.").queue();
        else
            event.reply(getClient().getSuccess() + " Paused **" + handler.getPlayer().getPlayingTrack().getInfo().title + "**.").queue();
    }
}
