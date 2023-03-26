package com.jagrosh.jmusicbot.commands.admin;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.audio.AudioHandler;
import com.jagrosh.jmusicbot.audio.QueuedTrack;
import com.jagrosh.jmusicbot.commands.AdminCommand;
import com.jagrosh.jmusicbot.queue.FairQueue;
import com.jagrosh.jmusicbot.queue.SimpleQueue;
import com.jagrosh.jmusicbot.settings.QueueType;
import com.jagrosh.jmusicbot.settings.Settings;

import java.util.List;

public class SetQueueTypeCmd extends AdminCommand
{
    public SetQueueTypeCmd(Bot bot)
    {
        this.name = "setqueuetype";
        this.help = "changes the behavior of the queue";
        this.aliases = bot.getConfig().getAliases(this.name);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<");
        for (int i = 0; i < QueueType.values().length; i++)
        {
            stringBuilder.append(QueueType.values()[i]);
            if (i != QueueType.values().length - 1)
            {
                stringBuilder.append(", ");
            }
        }
        stringBuilder.append(">");

        this.arguments = stringBuilder.toString();
    }

    @Override
    protected void execute(CommandEvent event)
    {
        String newQueueTypeArgument = event.getArgs().toLowerCase().replaceAll("[ _-]", "");

        QueueType newQueueType;
        try
        {
            newQueueType = QueueType.getFromParam(newQueueTypeArgument);
        } catch (IllegalArgumentException e)
        {
            event.replyError("The provided argument is not a valid queue type.");
            return;
        }

        Settings settings = event.getClient().getSettingsFor(event.getGuild());

        if (settings.getQueueType() == newQueueType)
        {
            event.replyError(String.format("The queue is already a %s.", newQueueType.getFriendlyName()));
            return;
        }

        AudioHandler audioHandler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
        settings.setQueueType(newQueueType);

        if (audioHandler == null)
        {
            event.replySuccess(String.format("Queue type switched to %s.", newQueueType.getFriendlyName()));
            return;
        }

        List<QueuedTrack> currentQueuedItems = audioHandler.getQueue().getList();

        if (newQueueType.equals(QueueType.FAIR_QUEUE))
        {
            audioHandler.setQueue(new FairQueue<>(currentQueuedItems));
        } else
        {
            audioHandler.setQueue(new SimpleQueue<>(currentQueuedItems));
        }

        event.replySuccess(String.format("Queue type set to %s.", newQueueType.getFriendlyName()));

    }
}
