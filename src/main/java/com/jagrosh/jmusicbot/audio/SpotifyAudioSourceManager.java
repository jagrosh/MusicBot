/*
 * Copyright 2024 LMusicBot Contributors
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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.track.AudioItem;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioReference;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.BasicAudioPlaylist;
import dev.lavalink.youtube.YoutubeAudioSourceManager;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpotifyAudioSourceManager extends YoutubeAudioSourceManager
{
    private static final Logger log = LoggerFactory.getLogger(SpotifyAudioSourceManager.class);

    private static final String TOKEN_URL = "https://accounts.spotify.com/api/token";
    private static final String API_BASE = "https://api.spotify.com/v1";
    private static final int MAX_PLAYLIST_TRACKS = 50;

    private static final Pattern TRACK_REGEX = Pattern.compile(
        "(?:https?://open\\.spotify\\.com/track/|spotify:track:)([a-zA-Z0-9]+)(?:\\?.*)?");
    private static final Pattern PLAYLIST_REGEX = Pattern.compile(
        "(?:https?://open\\.spotify\\.com/playlist/|spotify:playlist:)([a-zA-Z0-9]+)(?:\\?.*)?");
    private static final Pattern ALBUM_REGEX = Pattern.compile(
        "(?:https?://open\\.spotify\\.com/album/|spotify:album:)([a-zA-Z0-9]+)(?:\\?.*)?");

    private final HttpClient http;
    private final String clientId;
    private final String clientSecret;

    private String accessToken;
    private Instant tokenExpiry;

    public SpotifyAudioSourceManager(String clientId, String clientSecret)
    {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    }

    @Override
    public String getSourceName()
    {
        return "spotify";
    }

    @Override
    public AudioItem loadItem(AudioPlayerManager apm, AudioReference ar)
    {
        if (clientId == null || clientId.isEmpty() || clientSecret == null || clientSecret.isEmpty())
            return null;

        String identifier = ar.identifier;
        if (identifier == null)
            return null;

        try
        {
            Matcher trackMatcher = TRACK_REGEX.matcher(identifier);
            if (trackMatcher.matches())
                return loadTrack(apm, trackMatcher.group(1));

            Matcher playlistMatcher = PLAYLIST_REGEX.matcher(identifier);
            if (playlistMatcher.matches())
                return loadPlaylist(apm, playlistMatcher.group(1));

            Matcher albumMatcher = ALBUM_REGEX.matcher(identifier);
            if (albumMatcher.matches())
                return loadAlbum(apm, albumMatcher.group(1));
        }
        catch (Exception e)
        {
            log.warn("Failed to load Spotify item: {}", e.getMessage());
        }

        return null;
    }

    private AudioItem loadTrack(AudioPlayerManager apm, String id) throws Exception
    {
        JsonObject track = fetchJson(API_BASE + "/tracks/" + id);
        if (track == null)
            return null;

        String query = buildSearchQuery(track);
        if (query == null)
            return null;

        AudioPlaylist searchResult = (AudioPlaylist) super.loadItem(apm,
            new AudioReference("ytsearch:" + query, null));

        if (searchResult == null || searchResult.getTracks().isEmpty())
            return null;

        return searchResult.getTracks().get(0);
    }

    private AudioItem loadPlaylist(AudioPlayerManager apm, String id) throws Exception
    {
        JsonObject data = fetchJson(API_BASE + "/playlists/" + id + "?fields=name,tracks.items(track(name,artists(name)))&limit=" + MAX_PLAYLIST_TRACKS);
        if (data == null)
            return null;

        String name = getString(data, "name");
        List<AudioTrack> tracks = resolveTracks(apm, data, "tracks");
        if (tracks.isEmpty())
            return null;

        return new BasicAudioPlaylist(name != null ? name : "Spotify Playlist", tracks, null, false);
    }

    private AudioItem loadAlbum(AudioPlayerManager apm, String id) throws Exception
    {
        JsonObject data = fetchJson(API_BASE + "/albums/" + id + "?limit=" + MAX_PLAYLIST_TRACKS);
        if (data == null)
            return null;

        String name = getString(data, "name");
        List<AudioTrack> tracks = resolveTracks(apm, data, "tracks");
        if (tracks.isEmpty())
            return null;

        return new BasicAudioPlaylist(name != null ? name : "Spotify Album", tracks, null, false);
    }

    private List<AudioTrack> resolveTracks(AudioPlayerManager apm, JsonObject data, String itemsKey) throws Exception
    {
        List<AudioTrack> resolved = new ArrayList<>();
        JsonObject itemsContainer = data.getAsJsonObject(itemsKey);
        if (itemsContainer == null)
            return resolved;

        JsonArray items = itemsContainer.getAsJsonArray("items");
        if (items == null)
            return resolved;

        for (int i = 0; i < items.size() && resolved.size() < MAX_PLAYLIST_TRACKS; i++)
        {
            JsonElement elem = items.get(i);
            JsonObject trackObj;

            if (elem.isJsonObject() && elem.getAsJsonObject().has("track"))
                trackObj = elem.getAsJsonObject().getAsJsonObject("track");
            else if (elem.isJsonObject())
                trackObj = elem.getAsJsonObject();
            else
                continue;

            String trackName = getString(trackObj, "name");
            String artistName = extractArtist(trackObj);

            if (trackName == null || artistName == null)
                continue;

            String query = artistName + " - " + trackName;

            AudioPlaylist searchResult = (AudioPlaylist) super.loadItem(apm,
                new AudioReference("ytsearch:" + query, null));

            if (searchResult != null && !searchResult.getTracks().isEmpty())
                resolved.add(searchResult.getTracks().get(0));
        }

        return resolved;
    }

    private String buildSearchQuery(JsonObject track)
    {
        String name = getString(track, "name");
        String artist = extractArtist(track);
        if (name == null || artist == null)
            return null;
        return artist + " - " + name;
    }

    private String extractArtist(JsonObject item)
    {
        JsonArray artists = item.getAsJsonArray("artists");
        if (artists == null || artists.size() == 0)
            return null;

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < artists.size(); i++)
        {
            if (i > 0) sb.append(", ");
            JsonObject artist = artists.get(i).getAsJsonObject();
            sb.append(getString(artist, "name"));
        }
        return sb.toString();
    }

    private JsonObject fetchJson(String url) throws Exception
    {
        ensureToken();
        if (accessToken == null)
            return null;

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Authorization", "Bearer " + accessToken)
            .timeout(Duration.ofSeconds(15))
            .GET()
            .build();

        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200)
        {
            log.warn("Spotify API returned {} for {}", response.statusCode(), url);
            return null;
        }

        return JsonParser.parseString(response.body()).getAsJsonObject();
    }

    private void ensureToken() throws Exception
    {
        if (accessToken != null && tokenExpiry != null && Instant.now().isBefore(tokenExpiry))
            return;

        String auth = clientId + ":" + clientSecret;
        String encoded = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(TOKEN_URL))
            .header("Authorization", "Basic " + encoded)
            .header("Content-Type", "application/x-www-form-urlencoded")
            .timeout(Duration.ofSeconds(10))
            .POST(HttpRequest.BodyPublishers.ofString("grant_type=client_credentials"))
            .build();

        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200)
        {
            log.warn("Spotify token request failed: {} {}", response.statusCode(), response.body());
            accessToken = null;
            tokenExpiry = null;
            return;
        }

        JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
        accessToken = getString(json, "access_token");
        int expiresIn = json.get("expires_in") != null ? json.get("expires_in").getAsInt() : 3600;
        tokenExpiry = Instant.now().plusSeconds(expiresIn - 60);
    }

    private static String getString(JsonObject obj, String key)
    {
        JsonElement el = obj.get(key);
        return el != null && !el.isJsonNull() ? el.getAsString() : null;
    }

    @Override
    public void shutdown()
    {
    }
}
