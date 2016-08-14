/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spectramusic;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import net.dv8tion.jda.Permission;
import net.dv8tion.jda.entities.Role;
import net.dv8tion.jda.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.hooks.ListenerAdapter;
import net.dv8tion.jda.managers.AudioManager;
import net.dv8tion.jda.player.MusicPlayer;
import net.dv8tion.jda.utils.PermissionUtil;
import org.json.JSONObject;

/**
 *
 * @author John Grosh (jagrosh)
 */
public class Bot extends ListenerAdapter {
    
    private final String[] prefixes;
    private final String ownerId;
    private final JSONObject serverSettings;
    private final String helpFile;
    
    public Bot(String ownerId, String[] prefixes){
        this.prefixes = prefixes;
        this.ownerId = ownerId;
        JSONObject loadedSettings = new JSONObject();
        try {
            loadedSettings = new JSONObject(new String(Files.readAllBytes(Paths.get("serversettings.json"))));
        } catch (IOException ex) {
            System.out.println("No server settings found; using new settings for all servers.");
        }
        serverSettings = loadedSettings;
        helpFile = "**Spectra Music** help:"
                + "\n`"+prefixes[0]+"play <url>` - plays the song at the specified URL (or youtube video ID)"
                + "\n`"+prefixes[0]+"play <number>` - plays the song at the specified number if you just did a search"
                + "\n`"+prefixes[0]+"queue` - shows the current queue (also `list`)"
                + "\n`"+prefixes[0]+"nowplaying` - shows what is currently playing (also `np` or `current`)"
                + "\n`"+prefixes[0]+"voteskip` - votes to skip the current song (needs majority vote of listeners)"
                + "\n`"+prefixes[0]+"remove <number>` - removes the entry from the queue (if you added it or are a DJ or Admin)"
                + "\n\n**DJ Commands**"
                + "\n`"+prefixes[0]+"skip` - skips the current song"
                + "\n`"+prefixes[0]+"stop` - stops the player, clears the queue, and leave the voice channel"
                + "\n`"+prefixes[0]+"volume` - sets the volume (default 35%)"
                + "\n\n**Admin Commands**"
                + "\n`"+prefixes[0]+"setDJ <rolename or NONE>` - sets the given role as the DJ role (only 1 per server)"
                + "\n`"+prefixes[0]+"setTC [textchannel or NONE]` - sets the channel for displaying song info and allowing commands"
                + "\n`"+prefixes[0]+"setVC <voicechannel or NONE>` - sets a channel which will be the only one allowed for music";
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        String content = null;
        for(String prefix : prefixes)
            if(event.getMessage().getRawContent().toLowerCase().startsWith(prefix))
            {
                content = event.getMessage().getRawContent().substring(prefix.length()).trim();
                break;
            }
        if(content==null)
            return;
        if(content.toLowerCase().equals("musichelp") || content.toLowerCase().equals("music help"))
        {
            
            
            return;
        }
        JSONObject settings = serverSettings.has(event.getGuild().getId()) ? serverSettings.getJSONObject(event.getGuild().getId()) : null;
        boolean isOwner = event.getAuthor().getId().equals(ownerId);
        boolean isAdmin = isOwner || PermissionUtil.checkPermission(event.getAuthor(), Permission.MANAGE_SERVER, event.getGuild());
        Role djRole = settings==null ? null : event.getGuild().getRoleById(settings.getString("dj_role_id"));
        boolean isDJ = isAdmin || (djRole!=null && event.getGuild().getRolesForUser(event.getAuthor()).contains(djRole));
        boolean isValidChannel = isDJ || settings==null || settings.getString("music_channel_id").equals("") || settings.getString("music_channel_id").equals(event.getChannel().getId());
        String[] parts = content.split("\\s+");
        if(isValidChannel)
        {
            AudioManager manager = event.getGuild().getAudioManager();
            MusicPlayer player;
            if (manager.getSendingHandler() == null)
            {
                player = new MusicPlayer();
                player.setVolume(.35f);
                manager.setSendingHandler(player);
            }
            else
            {
                player = (MusicPlayer) manager.getSendingHandler();
            }
            
            switch(parts[0].toLowerCase())
            {
                case "play":
                    break;
                case "queue":
                case "list":
                    break;
                case "nowplaying":
                case "np":
                case "current":
                    break;
                case "voteskip":
                    break;
                case "remove":
                    break;

                case "skip":
                    break;
                case "stop":
                    player.stop();
                    player.getAudioQueue().clear();
                    
                    break;
                case "volume":
                    break;

                case "setVC":
                    break;
                case "setTC":
                    break;
                case "setDJ":
                    break;

                case "authorize":
                    break;
                case "deauthorize":
                    break;
            }
        }
    }
}
