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

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.jagrosh.jmusicbot.audio.AloneInVoiceHandler;
import com.jagrosh.jmusicbot.audio.AudioHandler;
import com.jagrosh.jmusicbot.audio.NowPlayingHandler;
import com.jagrosh.jmusicbot.audio.PlayerManager;
import com.jagrosh.jmusicbot.gui.Gui;
import com.jagrosh.jmusicbot.playlist.PlaylistLoader;
import com.jagrosh.jmusicbot.settings.SettingsManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class Bot {
    private final EventWaiter waiter;
    private final ScheduledExecutorService threadPool;
    private final BotConfig config;
    private final SettingsManager settings;
    private final PlayerManager players;
    private final PlaylistLoader playlists;
    private final NowPlayingHandler nowPlaying;
    private final AloneInVoiceHandler aloneInVoiceHandler;

    private boolean shuttingDown = false;
    private JDA jda;
    private Gui gui;

    public Bot(EventWaiter waiter, BotConfig config, SettingsManager settings) {
        this.waiter = waiter;
        this.config = config;
        this.settings = settings;
        this.playlists = new PlaylistLoader(config);
        this.threadPool = Executors.newSingleThreadScheduledExecutor();
        this.players = new PlayerManager(this);
        this.players.init();
        this.nowPlaying = new NowPlayingHandler(this);
        this.nowPlaying.init();
        this.aloneInVoiceHandler = new AloneInVoiceHandler(this);
        this.aloneInVoiceHandler.init();
    }

    public BotConfig getConfig() {
        return config;
    }

    public SettingsManager getSettingsManager() {
        return settings;
    }

    public EventWaiter getWaiter() {
        return waiter;
    }

    public ScheduledExecutorService getThreadPool() {
        return threadPool;
    }

    public PlayerManager getPlayerManager() {
        return players;
    }

    public PlaylistLoader getPlaylistLoader() {
        return playlists;
    }

    public NowPlayingHandler getNowplayingHandler() {
        return nowPlaying;
    }

    public AloneInVoiceHandler getAloneInVoiceHandler() {
        return aloneInVoiceHandler;
    }

    public JDA getJDA() {
        return jda;
    }

    public void closeAudioConnection(long guildId) {
        Guild guild = jda.getGuildById(guildId);
        if(guild != null) {
            threadPool.submit(() -> guild.getAudioManager().closeAudioConnection());
        }
    }

    public void resetGame() {
        Activity game =
            config.getGame() == null || config.getGame().getName().equalsIgnoreCase("none") ? null : config.getGame();
        if(!Objects.equals(jda.getPresence().getActivity(), game)) {
            jda.getPresence().setActivity(game);
        }
    }

    public void shutdown() {
        if(shuttingDown) {
            return;
        }
        shuttingDown = true;
        threadPool.shutdownNow();
        if(jda.getStatus() != JDA.Status.SHUTTING_DOWN) {
            jda.getGuilds().forEach(g ->
            {
                g.getAudioManager().closeAudioConnection();
                AudioHandler ah = (AudioHandler) g.getAudioManager().getSendingHandler();
                if(ah != null) {
                    ah.stopAndClear();
                    ah.getPlayer().destroy();
                }
            });
            jda.shutdown();
        }
        if(gui != null) {
            gui.dispose();
        }
        System.exit(0);
    }

    public void setJda(JDA jda) {
        this.jda = jda;
    }

    public void setGui(Gui gui) {
        this.gui = gui;
    }
}
