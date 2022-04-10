/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jagrosh.jmusicbot.commands.dj;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jmusicbot.audio.AudioHandler;
import com.jagrosh.jmusicbot.audio.QueuedTrack;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.commands.DJCommand;
import com.jagrosh.jmusicbot.queue.FairQueue;

/**
 * Command for toggling the lock status of a queue. When the queue is locked, DJ
 * Permissions are required to add tracks to the queue. Users may still remove
 * tracks which they had previously added.
 * 
 * @author Jared Morris (https://github.com/morrisj95)
 */
public class LockQueueCmd extends DJCommand
{
    public LockQueueCmd(Bot bot)
    {
        super(bot);
        this.name = "lockqueue";
        this.help = "locks or unlocks the queue";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.beListening = false;
        this.bePlaying = false;
    }

    @Override
    public void doCommand(CommandEvent event)
    {
        final AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
        final FairQueue<QueuedTrack> queue = handler.getQueue();
  
        queue.toggleLock();
  
        final String lockStatusString = queue.isLocked() ? "locked" : "unlocked";
        event.reply(event.getClient().getSuccess() + " The queue is now " + lockStatusString
            + ". Run the `lockqueue` command again to toggle the queue lock.");
    }
}
