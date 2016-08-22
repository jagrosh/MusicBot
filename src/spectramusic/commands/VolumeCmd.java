
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
import spectramusic.util.FormatUtil;

/**
 *
 * @author John Grosh (jagrosh)
 */
public class VolumeCmd extends Command {

    public VolumeCmd(){
        this.command = "volume";
        this.arguments = "<value>";
        this.aliases = new String[]{"vol"};
        this.level = PermLevel.DJ;
        this.help = "sets the volume (must be a value between 0 and 100; default is 35)";
    }
    
    @Override
    protected void execute(String args, GuildMessageReceivedEvent event, PermLevel caller, ClumpedMusicPlayer player) {
        int newVolume;
        try{
            newVolume = Integer.parseInt(args);
        } catch(NumberFormatException e)
        {
            Sender.sendReply(SpConst.ERROR+"Volume bust be an integer value between 0 and 100", event);
            return;
        }
        if(newVolume<0 || newVolume>100)
        {
            Sender.sendReply(SpConst.ERROR+"Volume bust be an integer value between 0 and 100", event);
            return;
        }
        int oldVolume = (int)(player.getVolume()*100);
        player.setVolume(newVolume/100f);
        Sender.sendReply(FormatUtil.volumeIcon(newVolume/100.0)+" Volume changed from `"+oldVolume+"` to `"+newVolume+"`", event);
    }
    
}
