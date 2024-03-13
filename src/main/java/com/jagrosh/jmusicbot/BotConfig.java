/*
 * Copyright 2018 John Grosh (jagrosh)
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

import com.jagrosh.jmusicbot.entities.Prompt;
import com.jagrosh.jmusicbot.utils.FormatUtil;
import com.jagrosh.jmusicbot.utils.OtherUtil;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.typesafe.config.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;

/**
 * 
 * 
 * @author John Grosh (jagrosh)
 */
public class BotConfig
{
    private final Prompt prompt;
    private final static String CONTEXT = "Config";
    private final static String START_TOKEN = "/// START OF JMUSICBOT CONFIG ///";
    private final static String END_TOKEN = "/// END OF JMUSICBOT CONFIG ///";
    
    private Path path = null;
    private String token, prefix, altprefix, helpWord, playlistsFolder,
            successEmoji, warningEmoji, errorEmoji, loadingEmoji, searchingEmoji;
    private boolean stayInChannel, songInGame, npImages, updatealerts, useEval, dbots;
    private long owner, maxSeconds, aloneTimeUntilStop;
    private double skipratio;
    private OnlineStatus status;
    private Activity game;
    private Config aliases, transforms;

    private boolean valid = false;
    
    public BotConfig(Prompt prompt)
    {
        this.prompt = prompt;
    }
    
    public void load()
    {
        valid = false;
        
        // read config from file
        try 
        {
            // get the path to the config, default config.txt
            path = getConfigPath();
            
            // load in the config file, plus the default values
            //Config config = ConfigFactory.parseFile(path.toFile()).withFallback(ConfigFactory.load());
            Config config = ConfigFactory.load();
            
            // set values
            token = config.getString("token");
            prefix = config.getString("prefix");
            altprefix = config.getString("altprefix");
            helpWord = config.getString("help");
            owner = config.getLong("owner");
            successEmoji = config.getString("success");
            warningEmoji = config.getString("warning");
            errorEmoji = config.getString("error");
            loadingEmoji = config.getString("loading");
            searchingEmoji = config.getString("searching");
            game = OtherUtil.parseGame(config.getString("game"));
            status = OtherUtil.parseStatus(config.getString("status"));
            stayInChannel = config.getBoolean("stayinchannel");
            songInGame = config.getBoolean("songinstatus");
            npImages = config.getBoolean("npimages");
            updatealerts = config.getBoolean("updatealerts");
            useEval = config.getBoolean("eval");
            maxSeconds = config.getLong("maxtime");
            aloneTimeUntilStop = config.getLong("alonetimeuntilstop");
            playlistsFolder = config.getString("playlistsfolder");
            aliases = config.getConfig("aliases");
            transforms = config.getConfig("transforms");
            skipratio = config.getDouble("skipratio");
            dbots = owner == 113156185389092864L;
            
            // we may need to write a new config file
            boolean write = false;

            // validate bot token
            if(token==null || token.isEmpty() || token.equalsIgnoreCase("BOT_TOKEN_HERE"))
            {
                token = prompt.prompt("Please provide a bot token."
                        + "\nInstructions for obtaining a token can be found here:"
                        + "\nhttps://github.com/jagrosh/MusicBot/wiki/Getting-a-Bot-Token."
                        + "\nBot Token: ");
                if(token==null)
                {
                    prompt.alert(Prompt.Level.WARNING, CONTEXT, "No token provided! Exiting.\n\nConfig Location: " + path.toAbsolutePath().toString());
                    return;
                }
                else
                {
                    write = true;
                }
            }
            
            // validate bot owner
            if(owner<=0)
            {
                try
                {
                    owner = Long.parseLong(prompt.prompt("Owner ID was missing, or the provided owner ID is not valid."
                        + "\nPlease provide the User ID of the bot's owner."
                        + "\nInstructions for obtaining your User ID can be found here:"
                        + "\nhttps://github.com/jagrosh/MusicBot/wiki/Finding-Your-User-ID"
                        + "\nOwner User ID: "));
                }
                catch(NumberFormatException | NullPointerException ex)
                {
                    owner = 0;
                }
                if(owner<=0)
                {
                    prompt.alert(Prompt.Level.ERROR, CONTEXT, "Invalid User ID! Exiting.\n\nConfig Location: " + path.toAbsolutePath().toString());
                    return;
                }
                else
                {
                    write = true;
                }
            }
            
            if(write)
                writeToFile();
            
            // if we get through the whole config, it's good to go
            valid = true;
        }
        catch (ConfigException ex)
        {
            prompt.alert(Prompt.Level.ERROR, CONTEXT, ex + ": " + ex.getMessage() + "\n\nConfig Location: " + path.toAbsolutePath().toString());
        }
    }
    
    private void writeToFile()
    {
        byte[] bytes = loadDefaultConfig().replace("BOT_TOKEN_HERE", token)
                .replace("0 // OWNER ID", Long.toString(owner))
                .trim().getBytes();
        try 
        {
            Files.write(path, bytes);
        }
        catch(IOException ex) 
        {
            prompt.alert(Prompt.Level.WARNING, CONTEXT, "Failed to write new config options to config.txt: "+ex
                + "\nPlease make sure that the files are not on your desktop or some other restricted area.\n\nConfig Location: " 
                + path.toAbsolutePath().toString());
        }
    }
    
    private static String loadDefaultConfig()
    {
        String original = OtherUtil.loadResource(new JMusicBot(), "/reference.conf");
        return original==null 
                ? "token = BOT_TOKEN_HERE\r\nowner = 0 // OWNER ID" 
                : original.substring(original.indexOf(START_TOKEN)+START_TOKEN.length(), original.indexOf(END_TOKEN)).trim();
    }
    
    private static Path getConfigPath()
    {
        Path path = OtherUtil.getPath(System.getProperty("config.file", System.getProperty("config", "config.txt")));
        if(path.toFile().exists())
        {
            if(System.getProperty("config.file") == null)
                System.setProperty("config.file", System.getProperty("config", path.toAbsolutePath().toString()));
            ConfigFactory.invalidateCaches();
        }
        return path;
    }
    
    public static void writeDefaultConfig()
    {
        Prompt prompt = new Prompt(null, null, true, true);
        prompt.alert(Prompt.Level.INFO, "JMusicBot Config", "Generating default config file");
        Path path = BotConfig.getConfigPath();
        byte[] bytes = BotConfig.loadDefaultConfig().getBytes();
        try
        {
            prompt.alert(Prompt.Level.INFO, "JMusicBot Config", "Writing default config file to " + path.toAbsolutePath().toString());
            Files.write(path, bytes);
        }
        catch(Exception ex)
        {
            prompt.alert(Prompt.Level.ERROR, "JMusicBot Config", "An error occurred writing the default config file: " + ex.getMessage());
        }
    }
    
    public boolean isValid()
    {
        return valid;
    }
    
    public String getConfigLocation()
    {
        return path.toFile().getAbsolutePath();
    }
    
    public String getPrefix()
    {
        return prefix;
    }
    
    public String getAltPrefix()
    {
        return "NONE".equalsIgnoreCase(altprefix) ? null : altprefix;
    }
    
    public String getToken()
    {
        return token;
    }
    
    public double getSkipRatio()
    {
        return skipratio;
    }
    
    public long getOwnerId()
    {
        return owner;
    }
    
    public String getSuccess()
    {
        return successEmoji;
    }
    
    public String getWarning()
    {
        return warningEmoji;
    }
    
    public String getError()
    {
        return errorEmoji;
    }
    
    public String getLoading()
    {
        return loadingEmoji;
    }
    
    public String getSearching()
    {
        return searchingEmoji;
    }
    
    public Activity getGame()
    {
        return game;
    }
    
    public boolean isGameNone()
    {
        return game != null && game.getName().equalsIgnoreCase("none");
    }
    
    public OnlineStatus getStatus()
    {
        return status;
    }
    
    public String getHelp()
    {
        return helpWord;
    }
    
    public boolean getStay()
    {
        return stayInChannel;
    }
    
    public boolean getSongInStatus()
    {
        return songInGame;
    }
    
    public String getPlaylistsFolder()
    {
        return playlistsFolder;
    }
    
    public boolean getDBots()
    {
        return dbots;
    }
    
    public boolean useUpdateAlerts()
    {
        return updatealerts;
    }
    
    public boolean useEval()
    {
        return useEval;
    }
    
    public boolean useNPImages()
    {
        return npImages;
    }
    
    public long getMaxSeconds()
    {
        return maxSeconds;
    }
    
    public String getMaxTime()
    {
        return FormatUtil.formatTime(maxSeconds * 1000);
    }

    public long getAloneTimeUntilStop()
    {
        return aloneTimeUntilStop;
    }
    
    public boolean isTooLong(AudioTrack track)
    {
        if(maxSeconds<=0)
            return false;
        return Math.round(track.getDuration()/1000.0) > maxSeconds;
    }

    public String[] getAliases(String command)
    {
        try
        {
            return aliases.getStringList(command).toArray(new String[0]);
        }
        catch(NullPointerException | ConfigException.Missing e)
        {
            return new String[0];
        }
    }
    
    public Config getTransforms()
    {
        return transforms;
    }
}
