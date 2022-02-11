/*
 * Copyright 2018 John Grosh <john.a.grosh@gmail.com>.
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

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.jagrosh.jmusicbot.audio.AloneInVoiceHandler;
import com.jagrosh.jmusicbot.audio.AudioHandler;
import com.jagrosh.jmusicbot.audio.NowplayingHandler;
import com.jagrosh.jmusicbot.audio.PlayerManager;
import com.jagrosh.jmusicbot.gui.GUI;
import com.jagrosh.jmusicbot.playlist.PlaylistLoader;
import com.jagrosh.jmusicbot.settings.Settings;
import com.jagrosh.jmusicbot.settings.SettingsManager;
import com.jagrosh.jmusicbot.settings.Settings.EmojiOption;

import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Random;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;

/**
 *
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class Bot
{
    private final EventWaiter waiter;
    private final ScheduledExecutorService threadpool;
    private final BotConfig config;
    private final SettingsManager settings;
    private final PlayerManager players;
    private final PlaylistLoader playlists;
    private final NowplayingHandler nowplaying;
    private final AloneInVoiceHandler aloneInVoiceHandler;
    private final SpotifyAPI spotifyApi;

    private final Random rng = new Random();
    
    private boolean shuttingDown = false;
    private JDA jda;
    private GUI gui;
    
    public Bot(EventWaiter waiter, BotConfig config, SettingsManager settings)
    {
        this.waiter = waiter;
        this.config = config;
        this.settings = settings;
        this.playlists = new PlaylistLoader(config);
        this.threadpool = Executors.newSingleThreadScheduledExecutor();
        this.players = new PlayerManager(this);
        this.players.init();
        this.nowplaying = new NowplayingHandler(this);
        this.nowplaying.init();
        this.aloneInVoiceHandler = new AloneInVoiceHandler(this);
        this.aloneInVoiceHandler.init();
        if (!config.hasSpotifyInfo()) this.spotifyApi = null;
        else {
            this.spotifyApi = new SpotifyAPI(config);
            try {
                this.spotifyApi.authorize();
            } catch (Exception e) {
                LoggerFactory.getLogger("Spotify").error("Failed to authorize. Exiting application due to error: ", e);
                System.exit(1);
            }
        }
    }
    
    public BotConfig getConfig()
    {
        return config;
    }
    
    public SettingsManager getSettingsManager()
    {
        return settings;
    }
    
    public EventWaiter getWaiter()
    {
        return waiter;
    }
    
    public ScheduledExecutorService getThreadpool()
    {
        return threadpool;
    }
    
    public PlayerManager getPlayerManager()
    {
        return players;
    }
    
    public PlaylistLoader getPlaylistLoader()
    {
        return playlists;
    }
    
    public NowplayingHandler getNowplayingHandler()
    {
        return nowplaying;
    }

    public AloneInVoiceHandler getAloneInVoiceHandler()
    {
        return aloneInVoiceHandler;
    }

    public SpotifyAPI getSpotifyAPI()
    {
        return spotifyApi;
    }
    
    public JDA getJDA()
    {
        return jda;
    }
    
    public void closeAudioConnection(long guildId)
    {
        Guild guild = jda.getGuildById(guildId);
        if(guild!=null)
            threadpool.submit(() -> guild.getAudioManager().closeAudioConnection());
    }
    
    public void resetGame()
    {
        Activity game = config.getGame()==null || config.getGame().getName().equalsIgnoreCase("none") ? null : config.getGame();
        if(!Objects.equals(jda.getPresence().getActivity(), game))
            jda.getPresence().setActivity(game);
    }

    public void shutdown()
    {
        if(shuttingDown)
            return;
        shuttingDown = true;
        threadpool.shutdownNow();
        if(jda.getStatus()!=JDA.Status.SHUTTING_DOWN)
        {
            jda.getGuilds().stream().forEach(g -> 
            {
                g.getAudioManager().closeAudioConnection();
                AudioHandler ah = (AudioHandler)g.getAudioManager().getSendingHandler();
                if(ah!=null)
                {
                    ah.stopAndClear();
                    ah.getPlayer().destroy();
                    nowplaying.updateTopic(g.getIdLong(), ah, true);
                }
            });
            jda.shutdown();
        }
        if(gui!=null)
            gui.dispose();
        System.exit(0);
    }

    public void setJDA(JDA jda)
    {
        this.jda = jda;
    }
    
    public void setGUI(GUI gui)
    {
        this.gui = gui;
    }

    private Settings getSettings(CommandEvent event) {
        try { return getSettingsManager().getSettings(event.getGuild()); }
        catch (IllegalStateException ignore) {}
        return getSettingsManager().getSettings(null);
    }

    private String chooseEmoji(EmojiOption[] options, String backup) {
        if (options == null || options.length == 0) return backup;
        if (options.length == 1) return options[0].emoji;
        double sum = 0;
        for (EmojiOption option : options) sum += option.weight;
        double rand = rng.nextDouble() * sum;
        for (EmojiOption option : options) {
            rand -= option.weight;
            if (rand <= 0) return option.emoji;
        }
        // loop may not have reached rand=0 due to double precision issues
        return options[options.length - 1].emoji;
    }

    public String getSuccess(CommandEvent event) 
    {
        return chooseEmoji(getSettings(event).getSuccessEmojis(), getConfig().getSuccess()); 
    }

    public String getWarning(CommandEvent event) 
    {
        return chooseEmoji(getSettings(event).getWarningEmojis(), getConfig().getWarning()); 
    }

    public String getError(CommandEvent event) 
    {
        return chooseEmoji(getSettings(event).getErrorEmojis(), getConfig().getError()); 
    }

    public String getLoading(CommandEvent event) 
    {
        return chooseEmoji(getSettings(event).getLoadingEmojis(), getConfig().getLoading()); 
    }

    public String getSearching(CommandEvent event) 
    {
        return chooseEmoji(getSettings(event).getSearchingEmojis(), getConfig().getSearching()); 
    }
}
