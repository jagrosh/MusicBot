
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
import net.dv8tion.jda.entities.TextChannel;
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
public class SetTCCmd extends Command {
    private final JSONObject serverSettings;
    public SetTCCmd(JSONObject serverSettings)
    {
        this.serverSettings = serverSettings;
        this.command = "settc";
        this.help = "sets a text channel to be the only usable channel by the bot "
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
                settings.put("text_channel_id", "");
            }
            Sender.sendReply(SpConst.SUCCESS+event.getJDA().getSelfInfo().getUsername()+" can now use any Text Channel to receive commands", event);
        }
        else
        {
            List<TextChannel> found = FinderUtil.findTextChannel(args, event.getGuild());
            if(found.isEmpty())
            {
                Sender.sendReply(String.format(SpConst.NONE_FOUND, "Text Channels",args), event);
            }
            else if (found.size()>1)
            {
                Sender.sendReply(FormatUtil.listOfTChannels(found, args), event);
            }
            else
            {
                if(settings==null)
                {
                    settings = new JSONObject()
                            .put("voice_channel_id", "")
                            .put("text_channel_id", found.get(0).getId())
                            .put("dj_role_id","");
                }
                else
                {
                    settings = settings.put("text_channel_id", found.get(0).getId());
                }
                serverSettings.put(event.getGuild().getId(), settings);
                String msg = SpConst.SUCCESS+event.getJDA().getSelfInfo().getUsername()+" will now only receive commands in **"+found.get(0).getName()+"**";
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
