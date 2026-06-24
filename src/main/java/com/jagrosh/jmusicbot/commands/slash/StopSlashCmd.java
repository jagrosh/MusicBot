package com.jagrosh.jmusicbot.commands.slash;

import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.audio.AudioHandler;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public class StopSlashCmd extends SlashDJCommand
{
    public StopSlashCmd(Bot bot)
    {
        super(bot);
        this.name = "stop";
        this.help = "stops the current song and clears the queue";
        this.bePlaying = false;
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
        handler.stopAndClear();
        event.getGuild().getAudioManager().closeAudioConnection();
        event.reply(getClient().getSuccess() + " The player has stopped and the queue has been cleared.").queue();
    }
}
