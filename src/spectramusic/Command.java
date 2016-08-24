
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
package spectramusic;

import javafx.util.Pair;
import net.dv8tion.jda.events.message.guild.GuildMessageReceivedEvent;
import spectramusic.entities.ClumpedMusicPlayer;

/**
 *
 * @author John Grosh (jagrosh)
 */
public abstract class Command {
    protected String command = "null";
    protected String arguments = null;
    protected String[] aliases = new String[0];
    protected PermLevel level = PermLevel.EVERYONE;
    protected String help = "";
    protected boolean mustBePlaying = false;
    protected boolean userMustBeInVC = false;
    
    protected abstract void execute(String args, GuildMessageReceivedEvent event, PermLevel caller, ClumpedMusicPlayer player);
    
    public void run(String args, GuildMessageReceivedEvent event, PermLevel caller, ClumpedMusicPlayer player, Pair<Boolean,String> voiceState)
    {
        if(!caller.isAtLeast(level))
            return;
        if(mustBePlaying && (player==null || !player.isPlaying()))
        {
            Sender.sendReply(SpConst.MUST_BE_PLAYING, event);
            return;
        }
        if(userMustBeInVC && !voiceState.getKey())
        {
            Sender.sendReply(String.format(SpConst.MUST_BE_IN_VC,voiceState.getValue()), event);
            return;
        }
        execute(args,event,caller,player);
    }
    
    public String getHelp()
    {
        return help;
    }
    
    public boolean isCommandFor(String input)
    {
        if(command.equalsIgnoreCase(input))
            return true;
        for(String str: aliases)
            if(str.equalsIgnoreCase(input))
                return true;
        return false;
    }
    
    public enum PermLevel {
        EVERYONE(0), DJ(1), ADMIN(2), OWNER(3);
        
        private final int value;
        
        private PermLevel(int value)
        {
            this.value = value;
        }
        
        public boolean isAtLeast(PermLevel level)
        {
            return value >= level.value;
        }
    }
}
