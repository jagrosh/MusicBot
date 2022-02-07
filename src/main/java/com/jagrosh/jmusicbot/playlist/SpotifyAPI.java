package com.jagrosh.jmusicbot.playlist;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;

import com.jagrosh.jmusicbot.BotConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;

public class SpotifyAPI {
  private final BotConfig config;
  private String authorizationString;

  public SpotifyAPI(BotConfig config)
  {
    this.config = config;
  }

  public void init() throws IOException, JSONException {
    { // query /api/token for access token
      URL url = new URL("https://accounts.spotify.com/api/token");
      HttpURLConnection con = (HttpURLConnection) url.openConnection();
      try {
        con.setRequestMethod("POST");
        con.setConnectTimeout(5000);
        con.setReadTimeout(5000);
    
        final String encodedAuthorization = Base64.getEncoder().encodeToString(String.format("%s:%s", config.getSpotClient(), config.getSpotSecret()).getBytes());
        con.setRequestProperty("Authorization", String.format("Basic %s", encodedAuthorization));
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        con.setRequestProperty("Accept", "application/json");
    
        con.setDoOutput(true);
        String postBody = "grant_type=client_credentials";
        try (OutputStream os = con.getOutputStream()) {
          byte[] input = postBody.getBytes("utf-8");
          os.write(input, 0, input.length);			
        }
    
        int status = con.getResponseCode();
        String response = null;
        if (status > 299 && con.getErrorStream() == null) {
          throw new RuntimeException(String.format("Non-200 response from spotify /api/token endpoint (%d)", status));
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
          throw new RuntimeException(String.format("Non-200 response from spotify /api/token endpoint (%d): %s", status, response));
        }
  
          JSONObject jo = new JSONObject(response);
          if (!jo.has("access_token")) {
            throw new RuntimeException(String.format("Unexpected response from spotify /api/token endpoint (%d): %s", status, response));
          }
          String accessToken = jo.getString("access_token");
          authorizationString = String.format("Bearer %s", accessToken);
      
  
      } finally {
        con.disconnect();
      }
    }
  }

  public String tryGetPlaylistIdFromUrl(String playlistUrl) {
    try {
      URL url = new URL(playlistUrl);
      if (!url.getHost().equals("open.spotify.com")
        || !url.getPath().startsWith("/playlist/")
        || url.getPath().split("/").length != 3) {
          LoggerFactory.getLogger("MusicBot").info("Couldn't find playlist id in url: " + playlistUrl);
          return null;
        }
      return url.getPath().split("/")[2];
    } catch (Exception e) {
      LoggerFactory.getLogger("MusicBot").error("Errored finding playlist id in url: " + playlistUrl, e);
      return null;
    }
  }

  public class SpotifyPlaylistQuery {
    public final String name;
    public final String[] trackQueries;

    public SpotifyPlaylistQuery(String name, String[] trackQueries) {
      this.name = name;
      this.trackQueries = trackQueries;
    }
  }

  public SpotifyPlaylistQuery getPlaylistTracksSearchQueries(String playlistId) throws IOException, JSONException {
    URL url = new URL(String.format("https://api.spotify.com/v1/playlists/%s", playlistId.replaceAll("[^a-zA-Z0-9_-]", "")));
    HttpURLConnection con = (HttpURLConnection) url.openConnection();
    try {
      con.setRequestMethod("GET");
      con.setConnectTimeout(5000);
      con.setReadTimeout(5000);
  
      con.setRequestProperty("Authorization", authorizationString);
      con.setRequestProperty("Content-Type", "application/json");
      con.setRequestProperty("Accept", "application/json");
  
      int status = con.getResponseCode();
      String response = null;
      try(BufferedReader br = new BufferedReader(
        status > 299
        ?  new InputStreamReader(con.getErrorStream(), "utf-8")
        : new InputStreamReader(con.getInputStream(), "utf-8"))) {
          StringBuilder responseBuilder = new StringBuilder();
          String responseLine = null;
          while ((responseLine = br.readLine()) != null) {
            responseBuilder.append(responseLine.trim());
          }
          response = responseBuilder.toString();
      }
  
      if (status != 200) {
        throw new RuntimeException(String.format("Non-200 response from spotify /v1/playlists endpoint (%d): %s", status, response));
      }

      JSONObject jo = new JSONObject(response);
      if (!jo.has("id") || !jo.getString("id").equals(playlistId)
        || !jo.has("name") || !jo.has("owner") || !jo.getJSONObject("owner").has("display_name")
        || !jo.has("tracks") || !jo.getJSONObject("tracks").has("items")) {
        throw new RuntimeException(String.format("Unexpected response from spotify /v1/playlists endpoint (%d): %s", status, response));
      }
    
      JSONArray tracksData = jo.getJSONObject("tracks").getJSONArray("items");

      String playlistName = String.format("%s by %s (%d tracks)", jo.getString("name"), jo.getJSONObject("owner").getString("display_name"), tracksData.length());

      ArrayList<String> trackQueries = new ArrayList<String>();
      for (int iTrack = 0; iTrack < tracksData.length(); iTrack++) {
        JSONObject trackData = tracksData.getJSONObject(iTrack).getJSONObject("track");

        ArrayList<String> artistNames = new ArrayList<String>();
        JSONArray artistsData = trackData.getJSONArray("artists");
        for (int iArtist = 0; iArtist < artistsData.length(); iArtist++) {
          artistNames.add(artistsData.getJSONObject(iArtist).getString("name"));
        }

        String trackName = trackData.getString("name");

        trackQueries.add(String.format("%s - %s", String.join(", ", artistNames), trackName));
      }

      return new SpotifyPlaylistQuery(playlistName, Arrays.copyOf(trackQueries.toArray(), trackQueries.size(), String[].class));
    } finally {
      con.disconnect();
    }
  }
}
