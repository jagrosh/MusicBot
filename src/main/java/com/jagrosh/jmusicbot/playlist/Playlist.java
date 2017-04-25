/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jagrosh.jmusicbot.playlist;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author John Grosh (john.a.grosh@gmail.com)
 */
public class Playlist {
    
    private final String name;
    private final List<String> items;
    private List<AudioTrack> tracks;
    private List<PlaylistLoadError> errors;
    
    private Playlist(String name, List<String> items)
    {
        this.name = name;
        this.items = items;
    }
    
    public void loadTracks(AudioPlayerManager manager, Runnable callback)
    {
        if(tracks==null)
        {
            tracks = new LinkedList<>();
            errors = new LinkedList<>();
            for(int i=0; i<items.size(); i++)
            {
                boolean last = i+1==items.size();
                int index = i;
                manager.loadItemOrdered(name, items.get(i), new AudioLoadResultHandler() {
                    @Override
                    public void trackLoaded(AudioTrack at) {
                        tracks.add(at);
                        if(last && callback!=null)
                            callback.run();
                    }
                    @Override
                    public void playlistLoaded(AudioPlaylist ap) {
                        if(ap.isSearchResult())
                            tracks.add(ap.getTracks().get(0));
                        else if(ap.getSelectedTrack()!=null)
                            tracks.add(ap.getSelectedTrack());
                        else
                            tracks.addAll(ap.getTracks());
                        if(last && callback!=null)
                            callback.run();
                    }

                    @Override
                    public void noMatches() {
                        errors.add(new PlaylistLoadError(index, items.get(index), "No matches found."));
                        if(last && callback!=null)
                            callback.run();
                    }

                    @Override
                    public void loadFailed(FriendlyException fe) {
                        errors.add(new PlaylistLoadError(index, items.get(index), "Failed to load track: "+fe.getLocalizedMessage()));
                        if(last && callback!=null)
                            callback.run();
                    }
                });
            }
        }
    }
    
    public String getName()
    {
        return name;
    }
    
    public List<String> getItems()
    {
        return items;
    }
    
    public List<AudioTrack> getTracks()
    {
        return tracks;
    }
    
    public List<PlaylistLoadError> getErrors()
    {
        return errors;
    }
    
    public static void createFolder()
    {
        try
        {
            Files.createDirectory(Paths.get("Playlists"));
        } catch (IOException ex)
        {}
    }
    
    public static boolean folderExists()
    {
        return Files.exists(Paths.get("Playlists"));
    }
    
    public static List<String> getPlaylists()
    {
        if(folderExists())
        {
            File folder = new File("Playlists");
            return Arrays.asList(folder.listFiles((pathname) -> pathname.getName().endsWith(".txt")))
                    .stream().map(f -> f.getName().substring(0,f.getName().length()-4)).collect(Collectors.toList());
        }
        else
        {
            createFolder();
            return null;
        }
    }
    
    public static Playlist loadPlaylist(String name)
    {
        try
        {
            if(folderExists())
            {
                return new Playlist(name, Files.readAllLines(Paths.get("Playlists"+File.separator+name+".txt"))
                        .stream()
                        .map((str) -> str.trim())
                        .filter((s) -> (!s.isEmpty() && !s.startsWith("#") && !s.startsWith("//")))
                        .collect(Collectors.toList()));
            }
            else
            {
                createFolder();
                return null;
            }
        }
        catch(IOException e)
        {
            return null;
        }
    }
    
    public class PlaylistLoadError {
        private final int number;
        private final String item;
        private final String reason;
        
        private PlaylistLoadError(int number, String item, String reason)
        {
            this.number = number;
            this.item = item;
            this.reason = reason;
        }
        
        public int getIndex()
        {
            return number;
        }
        
        public String getItem()
        {
            return item;
        }
        
        public String getReason()
        {
            return reason;
        }
    }
}
