
/*
 * Copyright 2016 jagrosh.
 *
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
package spectramusic.commands;

import javafx.util.Pair;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.player.source.AudioInfo;
import net.dv8tion.jda.player.source.AudioSource;
import net.dv8tion.jda.player.source.AudioTimestamp;
import spectramusic.Command;
import spectramusic.Sender;
import spectramusic.SpConst;
import spectramusic.entities.ClumpedMusicPlayer;
import spectramusic.entities.ClumpedQueue;

/**
 *
 * @author John Grosh (jagrosh)
 */
public class QueueCmd extends Command {

    public QueueCmd()
    {
        this.command = "queue";
        this.aliases = new String[]{"list"};
        this.help = "shows the current queue";
    }
    
    @Override
    protected void execute(String args, GuildMessageReceivedEvent event, PermLevel caller, ClumpedMusicPlayer player) {
        ClumpedQueue<String,AudioSource> queue = player.getAudioQueue();
        if(queue.isEmpty())
        {
            Sender.sendReply(SpConst.WARNING+"There is no music in the queue!", event);
            return;
        }
        int size = queue.size();
        StringBuilder builder = new StringBuilder(SpConst.SUCCESS+"Current Queue ("+size+" entries):");
        for(int i=0; i<10 && i<size; i++ )
        {
            Pair<String,AudioSource> item = queue.get(i);
            AudioInfo info = item.getValue().getInfo();
            builder.append("\n**").append(i<9 ? "0" : "").append(i+1).append(".** ");
            if (info == null)
                builder.append("*No info found*");
            else
            {
                AudioTimestamp duration = info.getDuration();
                builder.append("`[");
                if (duration == null)
                    builder.append("N/A");
                else
                    builder.append(duration.getTimestamp());
                builder.append("]` **").append(info.getTitle()).append("**");
            }
            User user = event.getJDA().getUserById(item.getKey());
            builder.append(" - requested by ").append(user==null ? "an unknown user..." : "**"+user.getUsername()+"**");
        }
        boolean error = false;
        int totalSeconds = 0;
        for (int i=0; i<queue.size(); i++)
        {
            AudioInfo info = queue.get(i).getValue().getInfo();
            if (info == null || info.getDuration() == null)
            {
                error = true;
                continue;
            }
            totalSeconds += info.getDuration().getTotalSeconds();
        }

        builder.append("\nTotal Queue Time Length: `").append(AudioTimestamp.fromSeconds(totalSeconds).getTimestamp()).append("`");
        if (error)
            builder.append("\nAn error occured calculating total time. Might not be completely valid.");
        Sender.sendReply(builder.toString(), event);
    }
    
}
