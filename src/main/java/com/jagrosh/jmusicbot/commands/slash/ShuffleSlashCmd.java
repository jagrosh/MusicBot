package com.jagrosh.jmusicbot.commands.slash;

import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.audio.AudioHandler;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public class ShuffleSlashCmd extends SlashMusicCommand
{
    public ShuffleSlashCmd(Bot bot)
    {
        super(bot);
        this.name = "shuffle";
        this.help = "shuffles the queue";
        this.beListening = true;
        this.bePlaying = true;
    }

    @Override
    protected void doCommand(SlashCommandEvent event)
    {
        AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
        int size = handler.getQueue().size();
        if (size == 0)
        {
            event.reply(getClient().getWarning() + " The queue is empty!").queue();
            return;
        }
        int s = handler.getQueue().shuffle(event.getUser().getIdLong());
        if (s == 0)
            event.reply(getClient().getError() + " You don't have any music in the queue to shuffle!").queue();
        else if (s == 1)
            event.reply(getClient().getWarning() + " You only have one song in the queue!").queue();
        else
            event.reply(getClient().getSuccess() + " You successfully shuffled your " + s + " entries.").queue();
    }
}
