/*
 * Copyright 2018 John Grosh (jagrosh).
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
package com.jagrosh.jmusicbot.playlist;

import com.jagrosh.jmusicbot.audio.AudioHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
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
    private final boolean shuffle;
    
    private Playlist(String name, List<String> items, boolean shuffle)
    {
        this.name = name;
        this.items = items;
        this.shuffle = shuffle;
    }
    
    public void loadTracks(AudioPlayerManager manager, Consumer<AudioTrack> consumer, Runnable callback)
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
                        if(AudioHandler.isTooLong(at))
                            errors.add(new PlaylistLoadError(index, items.get(index), "This track is longer than the allowed maximum"));
                        else
                        {
                            tracks.add(at);
                            consumer.accept(at);
                        }
                        if(last)
                        {
                            if(callback!=null)
                                callback.run();
                        }
                    }
                    @Override
                    public void playlistLoaded(AudioPlaylist ap) {
                        if(ap.isSearchResult())
                        {
                            if(AudioHandler.isTooLong(ap.getTracks().get(0)))
                                errors.add(new PlaylistLoadError(index, items.get(index), "This track is longer than the allowed maximum"));
                            else
                            {
                                tracks.add(ap.getTracks().get(0));
                                consumer.accept(ap.getTracks().get(0));
                            }
                        }
                        else if(ap.getSelectedTrack()!=null)
                        {
                            if(AudioHandler.isTooLong(ap.getSelectedTrack()))
                                errors.add(new PlaylistLoadError(index, items.get(index), "This track is longer than the allowed maximum"));
                            else
                            {
                                tracks.add(ap.getSelectedTrack());
                                consumer.accept(ap.getSelectedTrack());
                            }
                        }
                        else
                        {
                            List<AudioTrack> loaded = new ArrayList<>(ap.getTracks());
                            if(shuffle)
                                for(int first =0; first<loaded.size(); first++)
                                {
                                    int second = (int)(Math.random()*loaded.size());
                                    AudioTrack tmp = loaded.get(first);
                                    loaded.set(first, loaded.get(second));
                                    loaded.set(second, tmp);
                                }
                            loaded.removeIf(track -> AudioHandler.isTooLong(track));
                            tracks.addAll(loaded);
                            loaded.forEach(at -> consumer.accept(at));
                        }
                        if(last)
                        {
                            if(callback!=null)
                                callback.run();
                        }
                    }

                    @Override
                    public void noMatches() {
                        errors.add(new PlaylistLoadError(index, items.get(index), "No matches found."));
                        if(last)
                        {
                            if(callback!=null)
                                callback.run();
                        }
                    }

                    @Override
                    public void loadFailed(FriendlyException fe) {
                        errors.add(new PlaylistLoadError(index, items.get(index), "Failed to load track: "+fe.getLocalizedMessage()));
                        if(last)
                        {
                            if(callback!=null)
                                callback.run();
                        }
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
    
    public void shuffleTracks()
    {
        if(tracks!=null)
        {
            for(int first =0; first<tracks.size(); first++)
            {
                int second = (int)(Math.random()*tracks.size());
                AudioTrack tmp = tracks.get(first);
                tracks.set(first, tracks.get(second));
                tracks.set(second, tmp);
            }
        }
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
                boolean[] shuffle = {false};
                List<String> list = new ArrayList<>();
                Files.readAllLines(Paths.get("Playlists"+File.separator+name+".txt")).forEach(str -> {
                    String s = str.trim();
                    if(s.isEmpty())
                        return;
                    if(s.startsWith("#") || s.startsWith("//"))
                    {
                        s = s.replaceAll("\\s+", "");
                        if(s.equalsIgnoreCase("#shuffle") || s.equalsIgnoreCase("//shuffle"))
                            shuffle[0]=true;
                    }
                    else
                        list.add(s);
                });
                if(shuffle[0])
                {
                    for(int first =0; first<list.size(); first++)
                    {
                        int second = (int)(Math.random()*list.size());
                        String tmp = list.get(first);
                        list.set(first, list.get(second));
                        list.set(second, tmp);
                    }
                }
                return new Playlist(name, list, shuffle[0]);
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
