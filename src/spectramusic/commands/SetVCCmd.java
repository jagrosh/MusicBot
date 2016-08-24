
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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import net.dv8tion.jda.entities.VoiceChannel;
import net.dv8tion.jda.events.message.guild.GuildMessageReceivedEvent;
import org.json.JSONObject;
import spectramusic.Command;
import spectramusic.Sender;
import spectramusic.SpConst;
import spectramusic.entities.ClumpedMusicPlayer;
import spectramusic.util.FinderUtil;
import spectramusic.util.FormatUtil;

/**
 *
 * @author John Grosh (jagrosh)
 */
public class SetVCCmd extends Command {
    private final JSONObject serverSettings;
    public SetVCCmd(JSONObject serverSettings)
    {
        this.serverSettings = serverSettings;
        this.command = "setvc";
        this.help = "sets a voice channel to be the only usable channel by the bot "
                + "(1 per server). If set to NONE, the bot can use any channel";
        this.arguments = "<channel|NONE>";
        this.level = PermLevel.ADMIN;
    }
    
    @Override
    protected void execute(String args, GuildMessageReceivedEvent event, PermLevel caller, ClumpedMusicPlayer player) {
        JSONObject settings = serverSettings.has(event.getGuild().getId()) ? serverSettings.getJSONObject(event.getGuild().getId()) : null;
        if(args.equalsIgnoreCase("none"))
        {
            if(settings!=null)
            {
                settings.put(SpConst.VC_JSON, "");
            }
            Sender.sendReply(SpConst.SUCCESS+event.getJDA().getSelfInfo().getUsername()+" can now use any Voice Channel for music", event);
        }
        else
        {
            List<VoiceChannel> found = FinderUtil.findVoiceChannel(args, event.getGuild());
            if(found.isEmpty())
            {
                Sender.sendReply(String.format(SpConst.NONE_FOUND, "Voice Channels",args), event);
            }
            else if (found.size()>1)
            {
                Sender.sendReply(FormatUtil.listOfVChannels(found, args), event);
            }
            else
            {
                if(settings==null)
                {
                    settings = new JSONObject()
                            .put(SpConst.VC_JSON, found.get(0).getId())
                            .put(SpConst.TC_JSON, "")
                            .put(SpConst.DJ_JSON,"");
                }
                else
                {
                    settings = settings.put(SpConst.VC_JSON, found.get(0).getId());
                }
                serverSettings.put(event.getGuild().getId(), settings);
                String msg = SpConst.SUCCESS+event.getJDA().getSelfInfo().getUsername()+" can now only play music in **"+found.get(0).getName()+"**";
                try {
                    Files.write(Paths.get("serversettings.json"), serverSettings.toString(4).getBytes());
                    
                } catch (IOException e) {
                    msg+="\n"+SpConst.WARNING+"There was an error saving the settings file.";
                }
                Sender.sendReply(msg, event);
            }
        }
    }
    
}
