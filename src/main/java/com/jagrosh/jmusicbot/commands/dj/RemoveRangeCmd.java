package com.jagrosh.jmusicbot.commands.dj;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.audio.AudioHandler;
import com.jagrosh.jmusicbot.audio.QueuedTrack;
import com.jagrosh.jmusicbot.commands.DJCommand;
import com.jagrosh.jmusicbot.queue.FairQueue;
import com.jagrosh.jmusicbot.settings.Settings;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;

/**
 * Command that provides users the ability to remove a range of songs in the queue.
 */
public class RemoveRangeCmd extends DJCommand 
{
    /** 
    * RemoveRangeCmd Class constructor.
    */
    public RemoveRangeCmd(Bot bot)
    {
        super(bot);
        this.name = "removerange";
        this.help = "removes a range of songs from the queue";
        this.arguments = "<from> <to>";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.beListening = true;
        this.bePlaying = true;
    }

    
    /** 
     * Perform the command of removing a range of songs from the queue.
     * 
     * @param event the event that is being handled
     */
    @Override
    public void doCommand(CommandEvent event) 
    {
        AudioHandler handler = (AudioHandler)event.getGuild().getAudioManager().getSendingHandler();
        FairQueue<QueuedTrack> queue = handler.getQueue();
        if(queue.isEmpty())
        {
            event.replyError("There is nothing in the queue!");
            return;
        }
        int from;
        int to;

        String[] parts = event.getArgs().split("\\s+", 2);
        if(parts.length < 2)
        {
            event.replyError("Please include two valid indexes.");
            return;
        }
        
        try
        {
            // Validate the args
            from = Integer.parseInt(parts[0]);
            to = Integer.parseInt(parts[1]);
        }
        catch (NumberFormatException e)
        {
            event.replyError("Please provide two valid indexes.");
            return;
        }

        if (from > to)
        {
            event.replyError("From position cannot be greater than the to position.");
            return;
        }

        // Validate that from and to are available
        if (isUnavailablePosition(queue, from))
        {
            String reply = String.format("`%d` is not a valid position in the queue!", from);
            event.replyError(reply);
            return;
        }
        if (isUnavailablePosition(queue, to))
        {
            String reply = String.format("`%d` is not a valid position in the queue!", to);
            event.replyError(reply);
            return;
        }
      
        handler.getQueue().removeRange(from, to);
        String reply = String.format("Removed all songs from the queue from position `%d` to `%d`.", from, to);
        event.replySuccess(reply);
    }

    
    /** 
     * Checks to see if a position is valid for a given queue.
     * Queue positions start at 1.
     * 
     * @param queue the queue to be checked
     * @param position the position to be checked
     * @return boolean true if the position is valid, else false
     */
    private static boolean isUnavailablePosition(FairQueue<QueuedTrack> queue, int position)
    {
        return (position < 1 || position > queue.size());
    }
}
