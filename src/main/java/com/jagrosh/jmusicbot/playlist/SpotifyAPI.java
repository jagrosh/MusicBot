package com.jagrosh.jmusicbot.playlist;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import com.jagrosh.jmusicbot.BotConfig;

import org.json.JSONArray;
import org.json.JSONObject;

public class SpotifyAPI {
  private final BotConfig config;
  private String authorizationString;

  public SpotifyAPI(BotConfig config)
  {
    this.config = config;
  }

  private String executeQuery(String method, String host, String endpoint, String path, 
      Map<String, String> headers, String body) throws IOException {

    if (path == null) path = "";
    if (!path.equals("") && !path.startsWith("/")) path = "/" + path;

    Map<String, String> defaultHeaders = new HashMap<String, String>();
    defaultHeaders.put("Authorization", authorizationString);
    defaultHeaders.put("Accept", "application/json");

    URL url = new URL(String.format("https://%s%s%s", host, endpoint, path));
    HttpURLConnection con = (HttpURLConnection) url.openConnection();
    try {
      con.setRequestMethod(method);
      con.setConnectTimeout(5000);
      con.setReadTimeout(5000);
  
      for (Map.Entry<String, String> header : defaultHeaders.entrySet()) {
        if (headers != null && headers.containsKey(header.getKey())) {
          if (headers.get(header.getKey()) != null) con.setRequestProperty(header.getKey(), headers.get(header.getKey()));;
        } else con.setRequestProperty(header.getKey(), header.getValue());
      }
      if (headers != null) {
        for (Map.Entry<String, String> header : headers.entrySet()) {
          if (!defaultHeaders.containsKey(header.getKey())) con.setRequestProperty(header.getKey(), header.getValue());
        }
      }

      if (body != null) {
        con.setDoOutput(true);
        try (OutputStream os = con.getOutputStream()) {
          byte[] input = body.getBytes("utf-8");
          os.write(input, 0, input.length);			
        }
      }
  
      int status = con.getResponseCode();
      String response = null;
      if (status > 299 && con.getErrorStream() == null) {
        throw new RuntimeException(String.format("Non-200 response from spotify %s endpoint (%d)", endpoint, status));
      }

      try(BufferedReader br = new BufferedReader(
        status > 299
        ? new InputStreamReader(con.getErrorStream(), "utf-8")
        : new InputStreamReader(con.getInputStream(), "utf-8"))) {
          StringBuilder responseBuilder = new StringBuilder();
          String responseLine = null;
          while ((responseLine = br.readLine()) != null) {
            responseBuilder.append(responseLine.trim());
          }
          response = responseBuilder.toString();
      }
  
      if (status != 200) {
        throw new RuntimeException(String.format("Non-200 response from spotify %s endpoint (%d): %s", endpoint, status, response));
      }

      return response;
    } finally {
      con.disconnect();
    }
  }

  public void init() throws IOException {
    Map<String, String> apiTokenHeaders = new HashMap<String, String>();
    String encodedAuthorization = Base64.getEncoder().encodeToString(String.format("%s:%s", config.getSpotClient(), config.getSpotSecret()).getBytes());
    apiTokenHeaders.put("Authorization", String.format("Basic %s", encodedAuthorization));
    apiTokenHeaders.put("Content-Type", "application/x-www-form-urlencoded");

    String response = executeQuery(
      "POST", 
      "accounts.spotify.com", 
      "/api/token", 
      null,
      apiTokenHeaders,
      "grant_type=client_credentials");
    JSONObject jo = new JSONObject(response);

    if (!jo.has("access_token")) {
      throw new RuntimeException(String.format("Unexpected response from spotify /api/token endpoint: %s", response));
    }
    String accessToken = jo.getString("access_token");
    authorizationString = String.format("Bearer %s", accessToken);
  }

  public static class SpotifyUrlData
  {
    public static enum Type {
      TRACK, ALBUM, PLAYLIST;

      public String getPathPart() {
        switch (this) {
          case TRACK: return "track";
          case ALBUM: return "album";
          case PLAYLIST: return "playlist";
        }
        return null;
      }
    }

    public final Type type;
    public final String id;

    public SpotifyUrlData(Type type, String id) {
      this.type = type;
      this.id = id;
    }
  }

  /**
   * @return the data from the spotify url, or null if this isn't a valid spotify url
   */
  public SpotifyUrlData tryParseUrl(String maybeSpotifyUrl) {
    try {
      URL url = new URL(maybeSpotifyUrl);
      if (!url.getHost().equals("open.spotify.com")) return null;

      return Arrays.stream(SpotifyUrlData.Type.values())
        .filter(t -> url.getPath().startsWith("/" + t.getPathPart() + "/"))
        .findFirst()
        .map(urlType -> {
          String[] pathParts = url.getPath().split("/");
          if (pathParts.length != 3) return null;

          return new SpotifyUrlData(urlType, pathParts[2]);
        })
        .orElseGet(null);

    } catch (Exception e) {
      return null;
    }
  }

  public static class SpotifyTrack {
    public final String name;
    public final String[] artists;

    public SpotifyTrack(String name, String[] artists) {
      this.name = name;
      this.artists = artists;
    }
  }

  public static class SpotifyPlaylist {
    public final String name;
    public final SpotifyTrack[] tracks;

    public SpotifyPlaylist(String name, SpotifyTrack[] tracks) {
      this.name = name;
      this.tracks = tracks;
    }
  }

  private SpotifyTrack parseTrack(JSONObject track) {
    ArrayList<String> artistNames = new ArrayList<String>();
    JSONArray artistsData = track.getJSONArray("artists");
    for (int iArtist = 0; iArtist < artistsData.length(); iArtist++) {
      artistNames.add(artistsData.getJSONObject(iArtist).getString("name"));
    }

    String trackName = track.getString("name");

    return new SpotifyTrack(trackName, Arrays.copyOf(artistNames.toArray(), artistNames.size(), String[].class));
  }

  public SpotifyTrack getTrack(String trackId) throws IOException {
    String response = executeQuery(
      "GET", 
      "api.spotify.com", 
      "/v1/tracks",
      trackId.replaceAll("[^a-zA-Z0-9_-]", ""),
      null,
      null);

    JSONObject jo = new JSONObject(response);
    if (!jo.has("id") || !jo.getString("id").equals(trackId)
      || !jo.has("name")) {
      throw new RuntimeException(String.format("Unexpected response from spotify /v1/tracks endpoint: %s", response));
    }

    return parseTrack(jo);
  }

  public SpotifyPlaylist getPlaylist(String playlistId) throws IOException {
    String response = executeQuery(
      "GET", 
      "api.spotify.com", 
      "/v1/playlists",
      playlistId.replaceAll("[^a-zA-Z0-9_-]", ""),
      null,
      null);

    JSONObject jo = new JSONObject(response);
    if (!jo.has("id") || !jo.getString("id").equals(playlistId)
      || !jo.has("name") 
      || !jo.has("owner") || !jo.getJSONObject("owner").has("display_name")
      || !jo.has("tracks") || !jo.getJSONObject("tracks").has("items")) {
      throw new RuntimeException(String.format("Unexpected response from spotify /v1/playlists endpoint: %s", response));
    }
  
    String playlistName = String.format("%s by %s", jo.getString("name"), jo.getJSONObject("owner").getString("display_name"));

    JSONArray tracksData = jo.getJSONObject("tracks").getJSONArray("items");
    ArrayList<SpotifyTrack> tracks = new ArrayList<SpotifyTrack>();
    for (int iTrack = 0; iTrack < tracksData.length(); iTrack++) {
      tracks.add(parseTrack(tracksData.getJSONObject(iTrack).getJSONObject("track")));
    }

    return new SpotifyPlaylist(playlistName, Arrays.copyOf(tracks.toArray(), tracks.size(), SpotifyTrack[].class));
  }

  public SpotifyPlaylist getAlbum(String albumId) throws IOException {
    String response = executeQuery(
      "GET", 
      "api.spotify.com", 
      "/v1/albums",
      albumId.replaceAll("[^a-zA-Z0-9_-]", ""),
      null,
      null);

    JSONObject jo = new JSONObject(response);
    if (!jo.has("id") || !jo.getString("id").equals(albumId)
      || !jo.has("name") 
      || !jo.has("tracks") || !jo.getJSONObject("tracks").has("items")) {
      throw new RuntimeException(String.format("Unexpected response from spotify /v1/albums endpoint: %s", response));
    }
  
    String albumName = jo.getString("name");

    JSONArray tracksData = jo.getJSONObject("tracks").getJSONArray("items");
    ArrayList<SpotifyTrack> tracks = new ArrayList<SpotifyTrack>();
    for (int iTrack = 0; iTrack < tracksData.length(); iTrack++) {
      tracks.add(parseTrack(tracksData.getJSONObject(iTrack)));
    }

    return new SpotifyPlaylist(albumName, Arrays.copyOf(tracks.toArray(), tracks.size(), SpotifyTrack[].class));
  }
}
