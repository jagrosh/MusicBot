package com.jagrosh.jmusicbot.commands.slash;

import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.audio.AudioHandler;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public class ForceskipSlashCmd extends SlashDJCommand
{
    public ForceskipSlashCmd(Bot bot)
    {
        super(bot);
        this.name = "forceskip";
        this.help = "skips the current song (DJ only)";
        this.beListening = true;
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
        event.reply(getClient().getSuccess() + " Skipped **" + handler.getPlayer().getPlayingTrack().getInfo().title + "**").queue();
        handler.getPlayer().stopTrack();
    }
}
