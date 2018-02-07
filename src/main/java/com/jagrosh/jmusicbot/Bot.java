/*
 * Copyright 2016 John Grosh <john.a.grosh@gmail.com>.
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
package com.jagrosh.jmusicbot;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import com.jagrosh.jdautilities.command.Command.Category;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.jagrosh.jmusicbot.audio.AudioHandler;
import com.jagrosh.jmusicbot.gui.GUI;
import com.jagrosh.jmusicbot.utils.FormatUtil;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import java.util.Objects;
import javafx.util.Pair;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.ShutdownEvent;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.message.MessageDeleteEvent;
import net.dv8tion.jda.core.exceptions.PermissionException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class Bot extends ListenerAdapter {
    
    private final HashMap<String,Settings> settings;
    private final HashMap<Long,Pair<Long,Long>> lastNP; // guild -> channel,message
    private final AudioPlayerManager manager;
    private final EventWaiter waiter;
    private final ScheduledExecutorService threadpool;
    private final Config config;
    private JDA jda;
    private GUI gui;
    //private GuildsPanel panel;
    public final Category MUSIC = new Category("Music");
    public final Category DJ = new Category("DJ", event -> 
    {
        if(event.getAuthor().getId().equals(event.getClient().getOwnerId()))
            return true;
        if(event.getGuild()==null)
            return true;
        if(event.getMember().hasPermission(Permission.MANAGE_SERVER))
            return true;
        Role dj = event.getGuild().getRoleById(getSettings(event.getGuild()).getRoleId());
        return event.getMember().getRoles().contains(dj);
    });
    
    public final Category ADMIN = new Category("Admin", event -> 
    {
        if(event.getAuthor().getId().equals(event.getClient().getOwnerId()))
            return true;
        if(event.getGuild()==null)
            return true;
        return event.getMember().hasPermission(Permission.MANAGE_SERVER);
    });
    
    public final Category OWNER = new Category("Owner");
    
    public Bot(EventWaiter waiter, Config config)
    {
        this.config = config;
        this.waiter = waiter;
        this.settings = new HashMap<>();
        this.lastNP = new HashMap<>();
        manager = new DefaultAudioPlayerManager();
        threadpool = Executors.newSingleThreadScheduledExecutor();
        AudioSourceManagers.registerRemoteSources(manager);
        AudioSourceManagers.registerLocalSource(manager);
        manager.source(YoutubeAudioSourceManager.class).setPlaylistPageCount(10);
        try {
            JSONObject loadedSettings = new JSONObject(new String(Files.readAllBytes(Paths.get("serversettings.json"))));
            loadedSettings.keySet().forEach((id) -> {
                JSONObject o = loadedSettings.getJSONObject(id);
                
                settings.put(id, new Settings(
                        o.has("text_channel_id") ? o.getString("text_channel_id") : null,
                        o.has("voice_channel_id")? o.getString("voice_channel_id"): null,
                        o.has("dj_role_id")      ? o.getString("dj_role_id")      : null,
                        o.has("volume")          ? o.getInt("volume")             : 100,
                        o.has("default_playlist")? o.getString("default_playlist"): null,
                        o.has("repeat")          ? o.getBoolean("repeat")         : false));
            });
        } catch(IOException | JSONException e) {
            LoggerFactory.getLogger("Settings").warn("Failed to load server settings (this is normal if no settings have been set yet): "+e);
        }
    }
    
    public JDA getJDA()
    {
        return jda;
    }
    
    public EventWaiter getWaiter()
    {
        return waiter;
    }
    
    public AudioPlayerManager getAudioManager()
    {
        return manager;
    }
    
    public ScheduledExecutorService getThreadpool()
    {
        return threadpool;
    }
    
    public int queueTrack(CommandEvent event, AudioTrack track)
    {
        return setUpHandler(event).addTrack(track, event.getAuthor());
    }
    
    public AudioHandler setUpHandler(CommandEvent event)
    {
        return setUpHandler(event.getGuild());
    }
    
    public AudioHandler setUpHandler(Guild guild)
    {
        AudioHandler handler;
        if(guild.getAudioManager().getSendingHandler()==null)
        {
            AudioPlayer player = manager.createPlayer();
            if(settings.containsKey(guild.getId()))
                player.setVolume(settings.get(guild.getId()).getVolume());
            handler = new AudioHandler(player, guild, this);
            player.addListener(handler);
            guild.getAudioManager().setSendingHandler(handler);
            if(AudioHandler.USE_NP_REFRESH)
                threadpool.scheduleWithFixedDelay(() -> updateLastNP(guild.getIdLong()), 0, 5, TimeUnit.SECONDS);
        }
        else
            handler = (AudioHandler)guild.getAudioManager().getSendingHandler();
        return handler;
    }
    
    public void resetGame()
    {
        Game game = config.getGame()==null || config.getGame().getName().equalsIgnoreCase("none") ? null : config.getGame();
        if(!Objects.equals(jda.getPresence().getGame(), game))
            jda.getPresence().setGame(game);
    }
    
    public void setLastNP(Message m)
    {
        lastNP.put(m.getGuild().getIdLong(), new Pair<>(m.getTextChannel().getIdLong(), m.getIdLong()));
    }

    @Override
    public void onMessageDelete(MessageDeleteEvent event) {
        if(lastNP.containsKey(event.getGuild().getIdLong()))
        {
            Pair<Long,Long> pair = lastNP.get(event.getGuild().getIdLong());
            if(pair.getValue()==event.getMessageIdLong())
                lastNP.remove(event.getGuild().getIdLong());
        }
    }
    
    private void updateLastNP(long guildId)
    {
        Guild guild = jda.getGuildById(guildId);
        if(guild==null)
            return;
        if(!lastNP.containsKey(guildId))
            return;
        Pair<Long,Long> pair = lastNP.get(guildId);
        if(pair==null)
            return;
        TextChannel tc = guild.getTextChannelById(pair.getKey());
        if(tc==null)
        {
            lastNP.remove(guildId);
            return;
        }
        try {
            tc.editMessageById(pair.getValue(), FormatUtil.nowPlayingMessage(guild, config.getSuccess())).queue(m->{}, t -> lastNP.remove(guildId));
        } catch(Exception e) {
            lastNP.remove(guildId);
        }
    }
    
    public void updateTopic(long guildId, AudioHandler handler)
    {
        Guild guild = jda.getGuildById(guildId);
        if(guild==null)
            return;
        TextChannel tchan = guild.getTextChannelById(getSettings(guild).getTextId());
        if(tchan!=null && guild.getSelfMember().hasPermission(tchan, Permission.MANAGE_CHANNEL))
        {
            String otherText;
            if(tchan.getTopic()==null || tchan.getTopic().isEmpty())
                otherText = "\u200B";
            else if(tchan.getTopic().contains("\u200B"))
                otherText = tchan.getTopic().substring(tchan.getTopic().lastIndexOf("\u200B"));
            else
                otherText = "\u200B\n "+tchan.getTopic();
            String text = FormatUtil.topicFormat(handler, guild.getJDA())+otherText;
            if(!text.equals(tchan.getTopic()))
                try {
                    tchan.getManager().setTopic(text).queue();
                } catch(PermissionException e){}
        }
    }

    public void shutdown(){
        manager.shutdown();
        threadpool.shutdownNow();
        jda.getGuilds().stream().forEach(g -> {
            g.getAudioManager().closeAudioConnection();
            AudioHandler ah = (AudioHandler)g.getAudioManager().getSendingHandler();
            if(ah!=null)
            {
                ah.getQueue().clear();
                ah.getPlayer().destroy();
                updateTopic(g.getIdLong(), ah);
            }
        });
        jda.shutdown();
    }

    public void setGUI(GUI gui)
    {
        this.gui = gui;
    }
    
    @Override
    public void onShutdown(ShutdownEvent event) {
        if(gui!=null)
            gui.dispose();
    }

    @Override
    public void onReady(ReadyEvent event) {
        this.jda = event.getJDA();
        if(jda.getGuilds().isEmpty())
        {
            Logger log = LoggerFactory.getLogger("MusicBot");
            log.warn("This bot is not on any guilds! Use the following link to add the bot to your guilds!");
            log.warn(event.getJDA().asBot().getInviteUrl(JMusicBot.RECOMMENDED_PERMS));
        }
        credit(event.getJDA());
        jda.getGuilds().forEach((guild) -> {
            try
            {
                String defpl = getSettings(guild).getDefaultPlaylist();
                VoiceChannel vc = guild.getVoiceChannelById(getSettings(guild).getVoiceId());
                if(defpl!=null && vc!=null)
                {
                    if(setUpHandler(guild).playFromDefault())
                        guild.getAudioManager().openAudioConnection(vc);
                }
            }
            catch(Exception ex) {System.err.println(ex);}
        });
    }

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        credit(event.getJDA());
    }
    
    // make sure people aren't adding clones to dbots
    private void credit(JDA jda)
    {
        Guild dbots = jda.getGuildById(110373943822540800L);
        if(dbots==null)
            return;
        if(config.getDBots())
            return;
        jda.getTextChannelById(119222314964353025L)
                .sendMessage("<@113156185389092864>: This account is running JMusicBot. Please do not list bot clones on this server, <@"+config.getOwnerId()+">.").complete();
        dbots.leave().queue();
    }
    
    // settings
    
    public Settings getSettings(Guild guild)
    {
        return settings.getOrDefault(guild.getId(), Settings.DEFAULT_SETTINGS);
    }
    
    public void setTextChannel(TextChannel channel)
    {
        Settings s = settings.get(channel.getGuild().getId());
        if(s==null)
        {
            settings.put(channel.getGuild().getId(), new Settings(channel.getId(),null,null,100,null,false));
        }
        else
        {
            s.setTextId(channel.getIdLong());
        }
        writeSettings();
    }
    
    public void setVoiceChannel(VoiceChannel channel)
    {
        Settings s = settings.get(channel.getGuild().getId());
        if(s==null)
        {
            settings.put(channel.getGuild().getId(), new Settings(null,channel.getId(),null,100,null,false));
        }
        else
        {
            s.setVoiceId(channel.getIdLong());
        }
        writeSettings();
    }
    
    public void setRole(Role role)
    {
        Settings s = settings.get(role.getGuild().getId());
        if(s==null)
        {
            settings.put(role.getGuild().getId(), new Settings(null,null,role.getId(),100,null,false));
        }
        else
        {
            s.setRoleId(role.getIdLong());
        }
        writeSettings();
    }
    
    public void setDefaultPlaylist(Guild guild, String playlist)
    {
        Settings s = settings.get(guild.getId());
        if(s==null)
        {
            settings.put(guild.getId(), new Settings(null,null,null,100,playlist,false));
        }
        else
        {
            s.setDefaultPlaylist(playlist);
        }
        writeSettings();
    }
    
    public void setVolume(Guild guild, int volume)
    {
        Settings s = settings.get(guild.getId());
        if(s==null)
        {
            settings.put(guild.getId(), new Settings(null,null,null,volume,null,false));
        }
        else
        {
            s.setVolume(volume);
        }
        writeSettings();
    }
    
    public void setRepeatMode(Guild guild, boolean mode)
    {
        Settings s = settings.get(guild.getId());
        if(s==null)
        {
            settings.put(guild.getId(), new Settings(null,null,null,100,null,mode));
        }
        else
        {
            s.setRepeatMode(mode);
        }
        writeSettings();
    }
    
    public void clearTextChannel(Guild guild)
    {
        Settings s = getSettings(guild);
        if(s!=Settings.DEFAULT_SETTINGS)
        {
            if(s.getVoiceId()==0 && s.getRoleId()==0)
                settings.remove(guild.getId());
            else
                s.setTextId(0);
            writeSettings();
        }
    }
    
    public void clearVoiceChannel(Guild guild)
    {
        Settings s = getSettings(guild);
        if(s!=Settings.DEFAULT_SETTINGS)
        {
            if(s.getTextId()==0 && s.getRoleId()==0)
                settings.remove(guild.getId());
            else
                s.setVoiceId(0);
            writeSettings();
        }
    }
    
    public void clearRole(Guild guild)
    {
        Settings s = getSettings(guild);
        if(s!=Settings.DEFAULT_SETTINGS)
        {
            if(s.getVoiceId()==0 && s.getTextId()==0)
                settings.remove(guild.getId());
            else
                s.setRoleId(0);
            writeSettings();
        }
    }
    
    private void writeSettings()
    {
        JSONObject obj = new JSONObject();
        settings.keySet().stream().forEach(key -> {
            JSONObject o = new JSONObject();
            Settings s = settings.get(key);
            if(s.getTextId()!=0)
                o.put("text_channel_id", Long.toString(s.getTextId()));
            if(s.getVoiceId()!=0)
                o.put("voice_channel_id", Long.toString(s.getVoiceId()));
            if(s.getRoleId()!=0)
                o.put("dj_role_id", Long.toString(s.getRoleId()));
            if(s.getVolume()!=100)
                o.put("volume",s.getVolume());
            if(s.getDefaultPlaylist()!=null)
                o.put("default_playlist", s.getDefaultPlaylist());
            if(s.getRepeatMode())
                o.put("repeat", true);
            obj.put(key, o);
        });
        try {
            Files.write(Paths.get("serversettings.json"), obj.toString(4).getBytes());
        } catch(IOException ex){
            LoggerFactory.getLogger("Settings").warn("Failed to write to file: "+ex);
        }
    }
    
    //gui stuff
    /*public void registerPanel(GuildsPanel panel)
    {
        this.panel = panel;
        threadpool.scheduleWithFixedDelay(() -> updatePanel(), 0, 5, TimeUnit.SECONDS);
    }
    
    public void updatePanel()
    {
        System.out.println("updating...");
        Guild guild = jda.getGuilds().get(panel.getIndex());
        panel.updatePanel((AudioHandler)guild.getAudioManager().getSendingHandler());
    }

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        if(panel!=null)
            panel.updateList(event.getJDA().getGuilds());
    }

    @Override
    public void onGuildLeave(GuildLeaveEvent event) {
        if(panel!=null)
            panel.updateList(event.getJDA().getGuilds());
    }

    @Override
    public void onShutdown(ShutdownEvent event) {
        ((GUI)panel.getTopLevelAncestor()).dispose();
    }*/
    
}
