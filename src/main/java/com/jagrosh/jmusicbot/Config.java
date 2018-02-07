/*
 * Copyright 2016 John Grosh (jagrosh)
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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import javax.swing.JOptionPane;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;

/**
 *
 * @author John Grosh (jagrosh)
 */
public class Config {
    private boolean nogui;
    private String prefix, altprefix, token, owner, success, warning, error, game, 
            help, loadingEmoji, searchingEmoji;
    private boolean stay, dbots, songingame, useEval, npimages;
    private long maxSeconds;
    private OnlineStatus status = OnlineStatus.UNKNOWN;
    
    public Config(boolean nogui)
    {
        this.nogui = nogui;
        List<String> lines;
        try {
            lines = Files.readAllLines(Paths.get("config.txt"), StandardCharsets.UTF_8);
            System.out.println("[INFO] Loading config: "+Paths.get("config.txt").toFile().getAbsolutePath());
            for(String line: lines)
            {
                String[] parts = line.split("=",2);
                String key = parts[0].trim().toLowerCase();
                String value = parts.length>1 ? parts[1].trim() : null;
                switch(key)
                {
                    case "token":
                        token = value;
                        break;
                    case "prefix":
                        prefix = value;
                        break;
                    case "altprefix":
                        altprefix = value;
                        break;
                    case "owner":
                        owner = value;
                        break;
                    case "success":
                        success = value;
                        break;
                    case "warning":
                        warning = value;
                        break;
                    case "error":
                        error = value;
                        break;
                    case "loading":
                        loadingEmoji = value;
                        break;
                    case "searching":
                        searchingEmoji = value;
                        break;
                    case "game":
                        game = value;
                        break;
                    case "help":
                        help = value;
                        break;
                    case "songinstatus":
                        songingame = "true".equalsIgnoreCase(value);
                        break;
                    case "stayinchannel":
                        stay = "true".equalsIgnoreCase(value);
                        break;
                    case "eval":
                        useEval = "true".equalsIgnoreCase(value);
                        break;
                    case "dbots":
                        dbots = "110373943822540800".equals(value);
                        break;
                    case "npimages":
                        npimages = "true".equalsIgnoreCase(value);
                        break;
                    case "maxtime":
                        try{
                            maxSeconds = Long.parseLong(value);
                        }catch(NumberFormatException e){}
                        break;
                    case "status":
                        status = OnlineStatus.fromKey(value);
                        break;
                }
            }
        } catch (IOException ex) {
            alert("'config.txt' was not found!");
            lines = new LinkedList<>();
        }
        boolean write = false;
        if(token==null || token.isEmpty())
        {
            token = prompt("Please provide a bot token."
                    + "\nInstructions for obtaining a token can be found here:"
                    + "\nhttps://github.com/jagrosh/MusicBot/wiki/Getting-a-Bot-Token."
                    + "\nBot Token: ");
            if(token==null)
            {
                alert("No token provided! Exiting.");
                System.exit(0);
            }
            else
            {
                lines.add("token="+token);
                write = true;
            }
        }
        if(owner==null || !owner.matches("\\d{17,20}"))
        {
            owner = prompt("Owner ID was missing, or the provided owner ID is not valid."
                    + "\nPlease provide the User ID of the bot's owner."
                    + "\nInstructions for obtaining your User ID can be found here:"
                    + "\nhttps://github.com/jagrosh/MusicBot/wiki/Finding-Your-User-ID"
                    + "\nOwner User ID: ");
            if(owner==null || !owner.matches("\\d{17,20}"))
            {
                alert("Invalid User ID! Exiting.");
                System.exit(0);
            }
            else
            {
                lines.add("owner="+owner);
                write = true;
            }
        }
        if(write)
        {
            StringBuilder builder = new StringBuilder();
            lines.stream().forEach(s -> builder.append(s).append("\r\n"));
            try {
                Files.write(Paths.get("config.txt"), builder.toString().trim().getBytes());
            } catch(IOException ex) {
                alert("Failed to write new config options to config.txt: "+ex
                    + "\nPlease make sure that the files are not on your desktop or some other restricted area.");
            }
        }
    }
    
    public String getPrefix()
    {
        return prefix;
    }
    
    public String getAltPrefix()
    {
        return altprefix;
    }
    
    public String getToken()
    {
        return token;
    }
    
    public String getOwnerId()
    {
        return owner;
    }
    
    public String getSuccess()
    {
        return success==null ? "\uD83C\uDFB6" : success;
    }
    
    public String getWarning()
    {
        return warning==null ? "\uD83D\uDCA1" : warning;
    }
    
    public String getError()
    {
        return error==null ? "\uD83D\uDEAB" : error;
    }
    
    public String getLoading()
    {
        return loadingEmoji==null ? "\u231A" : loadingEmoji;
    }
    
    public String getSearching()
    {
        return searchingEmoji==null ? "\uD83D\uDD0E" : searchingEmoji;
    }
    
    public Game getGame()
    {
        if(game==null || game.isEmpty())
            return null;
        if(game.toLowerCase().startsWith("playing"))
            return Game.playing(game.substring(7).trim());
        if(game.toLowerCase().startsWith("listening to"))
            return Game.listening(game.substring(12).trim());
        if(game.toLowerCase().startsWith("listening"))
            return Game.listening(game.substring(9).trim());
        if(game.toLowerCase().startsWith("watching"))
            return Game.watching(game.substring(8).trim());
        return Game.playing(game);
    }
    
    public String getHelp()
    {
        return help==null ? "help" : help;
    }
    
    public boolean getNoGui()
    {
        return nogui;
    }
    
    public boolean getStay()
    {
        return stay;
    }
    
    public boolean getSongInStatus()
    {
        return songingame;
    }
    
    public boolean getDBots()
    {
        return dbots;
    }
    
    public boolean useEval()
    {
        return useEval;
    }
    
    public boolean useNPImages()
    {
        return npimages;
    }
    
    public long getMaxSeconds()
    {
        return maxSeconds;
    }
    
    public OnlineStatus getStatus()
    {
        return status;
    }
    
    private void alert(String message)
    {
        if(nogui)
            System.out.println("[WARNING] "+message);
        else
        {
            try {
                JOptionPane.showMessageDialog(null, message, "JMusicBot", JOptionPane.WARNING_MESSAGE);
            } catch(Exception e) {
                nogui = true;
                alert("Switching to nogui mode. You can manually start in nogui mode by including the -nogui flag.");
                alert(message);
            }
        }
    }
    
    private String prompt(String content)
    {
        if(nogui)
        {
            Scanner scanner = new Scanner(System.in);
            System.out.println(content);
            return scanner.next();
        }
        else
        {
            try {
                return JOptionPane.showInputDialog(null, content, "JMusicBot", JOptionPane.WARNING_MESSAGE);
            } catch(Exception e) {
                nogui = true;
                alert("Switching to nogui mode. You can manually start in nogui mode by including the -nogui flag.");
                return prompt(content);
            }
        }
    }
}
