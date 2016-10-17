/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spectramusic.commands;

import net.dv8tion.jda.entities.Role;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.VoiceChannel;
import net.dv8tion.jda.events.message.guild.GuildMessageReceivedEvent;
import org.json.JSONObject;
import spectramusic.Command;
import spectramusic.Sender;
import spectramusic.SpConst;
import spectramusic.entities.ClumpedMusicPlayer;

/**
 *
 * @author John Grosh (jagrosh)
 */
public class StatusCmd extends Command {
    private final JSONObject serverSettings;
    public StatusCmd(JSONObject serverSettings)
    {
        this.serverSettings = serverSettings;
        this.command = "settings";
        this.aliases = new String[]{"status"};
        this.help = "shows the status and current settings";
    }
    
    @Override
    protected void execute(String args, GuildMessageReceivedEvent event, PermLevel caller, ClumpedMusicPlayer player) {
        JSONObject settings = serverSettings.has(event.getGuild().getId()) ? serverSettings.getJSONObject(event.getGuild().getId()) : null;
        TextChannel tc = settings==null ? null : event.getJDA().getTextChannelById(settings.getString(SpConst.TC_JSON));
        VoiceChannel vc = settings==null ? null : event.getJDA().getVoiceChannelById(settings.getString(SpConst.VC_JSON));
        Role dj = settings==null ? null : event.getGuild().getRoleById(settings.getString(SpConst.DJ_JSON));
        String str = "\uD83C\uDFA7 **"+event.getJDA().getSelfInfo().getUsername()+"** status:\n"
                +SpConst.LINESTART+"Total Servers: **"+event.getJDA().getGuilds().size()+"**\n"+SpConst.LINESTART+"Audio Connections: **"
                +event.getJDA().getGuilds().stream().filter(g -> g.getVoiceStatusOfUser(event.getJDA().getSelfInfo()).inVoiceChannel()).count()
                +"**\n\n\uD83D\uDDA5 Settings on **"+event.getGuild().getName()+"**:\n"
                +SpConst.LINESTART+"Text Channel: "+(tc==null ? "any" : "**#"+tc.getName()+"**")+"\n"
                +SpConst.LINESTART+"Voice Channel: "+(vc==null ? "any" : "**"+vc.getName()+"**")+"\n"
                +SpConst.LINESTART+"DJ Role: "+(dj==null ? "none" : "**"+dj.getName()+"**");
        Sender.sendReply(str, event);
    }
    
}
