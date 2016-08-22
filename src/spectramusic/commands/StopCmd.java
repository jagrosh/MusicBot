
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

import net.dv8tion.jda.events.message.guild.GuildMessageReceivedEvent;
import spectramusic.Command;
import spectramusic.Sender;
import spectramusic.SpConst;
import spectramusic.entities.ClumpedMusicPlayer;

/**
 *
 * @author John Grosh (jagrosh)
 */
public class StopCmd extends Command {

    public StopCmd()
    {
        this.command = "stop";
        this.help = "clears the queue and leaves the voice chat";
        this.level = PermLevel.DJ;
        this.mustBePlaying = true;
    }
    
    @Override
    protected void execute(String args, GuildMessageReceivedEvent event, PermLevel caller, ClumpedMusicPlayer player) {
        player.stop();
        player.getAudioQueue().clear();
        event.getGuild().getAudioManager().closeAudioConnection();
        Sender.sendReply(SpConst.SUCCESS+"The queue has been cleared and the bot has disconnected", event);
    }
    
}
