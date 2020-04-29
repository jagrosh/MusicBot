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
package com.jagrosh.jmusicbot.audio;

import com.jagrosh.jmusicbot.Bot;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.local.LocalAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.AudioItem;
import com.sedmelluq.discord.lavaplayer.track.AudioReference;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.BasicAudioPlaylist;
import net.dv8tion.jda.core.entities.Guild;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author John Grosh (john.a.grosh@gmail.com)
 */
public class PlayerManager extends DefaultAudioPlayerManager
{
    private final Bot bot;

    public PlayerManager(Bot bot)
    {
        this.bot = bot;
    }

    public void init()
    {
        AudioSourceManagers.registerRemoteSources(this);
        AudioSourceManagers.registerLocalSource(this);
        registerSourceManager(new RecursiveLocalAudioSourceManager());
        source(YoutubeAudioSourceManager.class).setPlaylistPageCount(10);
    }

    public Bot getBot()
    {
        return bot;
    }

    public boolean hasHandler(Guild guild)
    {
        return guild.getAudioManager().getSendingHandler() != null;
    }

    public AudioHandler setUpHandler(Guild guild)
    {
        AudioHandler handler;
        if (guild.getAudioManager().getSendingHandler() == null) {
            AudioPlayer player = createPlayer();
            player.setVolume(bot.getSettingsManager().getSettings(guild).getVolume());
            handler = new AudioHandler(this, guild, player);
            player.addListener(handler);
            guild.getAudioManager().setSendingHandler(handler);
        } else
            handler = (AudioHandler) guild.getAudioManager().getSendingHandler();
        return handler;
    }

    private static class RecursiveLocalAudioSourceManager extends LocalAudioSourceManager
    {
        private static final String SEARCH_PREFIX = "reclocal:";

        @Override
        public AudioItem loadItem(DefaultAudioPlayerManager manager, AudioReference reference) {
            try {
                List<AudioTrack> tracks = searchPathsCorrespondingQuery(reference.getIdentifier())
                        .stream()
                        .map(path -> super.loadItem(manager, new AudioReference(path.toFile().toString(), path.getFileName().toString())))
                        .filter(audioItem -> audioItem instanceof AudioTrack)
                        .map(audioItem -> (AudioTrack) audioItem)
                        .collect(Collectors.toList());

                if (tracks.isEmpty()) return null;
                if (tracks.size() == 1) return tracks.get(0);

                return new BasicAudioPlaylist(reference.identifier, tracks, tracks.get(0), true);
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            return null;
        }

        private List<Path> searchPathsCorrespondingQuery(String query) throws IOException {
            if (!query.startsWith(SEARCH_PREFIX)) return Collections.emptyList();

            String word = query.substring(SEARCH_PREFIX.length()).trim();
            return Files.walk(Paths.get("."))
                    .filter(path -> path.getFileName().toString().contains(word))
                    .collect(Collectors.toList());
        }
    }
}
