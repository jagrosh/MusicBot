/*
 * Copyright 2016 John Grosh <john.a.grosh@gmail.com>.
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
package com.jagrosh.jmusicbot.settings;

import com.jagrosh.jdautilities.command.GuildSettingsProvider;
import java.util.Collection;
import java.util.Collections;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import org.json.JSONArray;

/**
 *
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class Settings implements GuildSettingsProvider
{
    private final SettingsManager manager;
    protected long textId;
    protected long voiceId;
    protected long roleId;
    private int volume;
    private String defaultPlaylist;
    private RepeatMode repeatMode;
    private String prefix;
    private double skipRatio;
    private JSONArray blacklistedUsers;
    private JSONArray whitelistedUsers;
    private boolean blacklistEnabled;
    private boolean whitelistEnabled;

    public Settings(SettingsManager manager, String textId, String voiceId, String roleId, int volume, String defaultPlaylist, RepeatMode repeatMode, String prefix, double skipRatio, JSONArray blacklistedUsers, JSONArray whitelistedUsers, boolean blacklistEnabled, boolean whitelistEnabled)
    {
        this.manager = manager;
        try
        {
            this.textId = Long.parseLong(textId);
        }
        catch(NumberFormatException e)
        {
            this.textId = 0;
        }
        try
        {
            this.voiceId = Long.parseLong(voiceId);
        }
        catch(NumberFormatException e)
        {
            this.voiceId = 0;
        }
        try
        {
            this.roleId = Long.parseLong(roleId);
        }
        catch(NumberFormatException e)
        {
            this.roleId = 0;
        }
        this.volume = volume;
        this.defaultPlaylist = defaultPlaylist;
        this.repeatMode = repeatMode;
        this.prefix = prefix;
        this.skipRatio = skipRatio;
        this.blacklistedUsers = blacklistedUsers;
        this.whitelistedUsers = whitelistedUsers;
        this.blacklistEnabled = blacklistEnabled;
        this.whitelistEnabled = whitelistEnabled;
    }
    
    public Settings(SettingsManager manager, long textId, long voiceId, long roleId, int volume, String defaultPlaylist, RepeatMode repeatMode, String prefix, double skipRatio, JSONArray blacklistedUsers, JSONArray whitelistedUsers, boolean blacklistEnabled, boolean whitelistEnabled)
    {
        this.manager = manager;
        this.textId = textId;
        this.voiceId = voiceId;
        this.roleId = roleId;
        this.volume = volume;
        this.defaultPlaylist = defaultPlaylist;
        this.repeatMode = repeatMode;
        this.prefix = prefix;
        this.skipRatio = skipRatio;
        this.blacklistedUsers = blacklistedUsers;
        this.whitelistedUsers = whitelistedUsers;
        this.blacklistEnabled = blacklistEnabled;
        this.whitelistEnabled = whitelistEnabled;
    }
    
    // Getters
    public boolean getBlacklistSettings()
    {
        return this.blacklistEnabled;
    }
    public boolean getWhiteListSettings()
    {
        return this.whitelistEnabled;
    }
    public JSONArray getBlacklistedUsers()
    {
        return this.blacklistedUsers;
    }
    public JSONArray getWhitelistedUsers()
    {
        return this.whitelistedUsers;
    }
    public TextChannel getTextChannel(Guild guild)
    {
        return guild == null ? null : guild.getTextChannelById(textId);
    }
    
    public VoiceChannel getVoiceChannel(Guild guild)
    {
        return guild == null ? null : guild.getVoiceChannelById(voiceId);
    }
    
    public Role getRole(Guild guild)
    {
        return guild == null ? null : guild.getRoleById(roleId);
    }
    
    public int getVolume()
    {
        return volume;
    }
    
    public String getDefaultPlaylist()
    {
        return defaultPlaylist;
    }
    
    public RepeatMode getRepeatMode()
    {
        return repeatMode;
    }
    
    public String getPrefix()
    {
        return prefix;
    }
    
    public double getSkipRatio()
    {
        return skipRatio;
    }

    @Override
    public Collection<String> getPrefixes()
    {
        return prefix == null ? Collections.emptySet() : Collections.singleton(prefix);
    }
    
    // Setters
    public String setPermissionListSettings(String type, String action)
    {
        if(type.equalsIgnoreCase("blacklist"))
        {
            if(action.equalsIgnoreCase("enable"))
            {
                if(!this.whitelistEnabled)
                {
                    this.blacklistEnabled = true;
                    return "blacklist enabled";
                }
                else
                {
                    return "please disable whitelist before using this command";
                }
            }
            else if (action.equalsIgnoreCase("disable"))
            {
                this.blacklistEnabled = false;
                return "blacklist disabled";
            }
        }
        else if (type.equalsIgnoreCase("whitelist"))
        {
            if(action.equalsIgnoreCase("enable")) {
                if(!this.blacklistEnabled)
                {
                    this.whitelistEnabled = true;
                    return "whitelist enabled";
                }
                else
                {
                    return "please disable blacklist before using this command";
                }
            }
            else if (action.equalsIgnoreCase("disable"))
            {
                this.whitelistEnabled = false;
                return "whitelist disabled";
            }
        }
        return "invalid list type provided";
    }
    public void setBlacklistedUsers(String action, String user)
    {
        if(action.equalsIgnoreCase("add"))
        {
            boolean contains_user = false;
            for (int i=0;i<this.blacklistedUsers.length();i++){
                if(this.blacklistedUsers.get(i).equals(user)) {
                    contains_user = true;
                    break;
                }
            }
            if (!contains_user) {
                this.blacklistedUsers.put(user);
            } else {
                return;
            }
        }
        else if(action.equalsIgnoreCase("remove"))
        {
            for (int i=0;i<this.blacklistedUsers.length();i++){
                if(this.blacklistedUsers.get(i).equals(user)) {
                    this.blacklistedUsers.remove(i);
                    break;
                }
            }
        }
        this.manager.writeSettings();
    }
    public void setWhitelistUsers(String action, String user)
    {
        if(action.equalsIgnoreCase("add"))
        {
            boolean contains_user = false;
            for (int i=0;i<this.whitelistedUsers.length();i++){
                if(this.whitelistedUsers.get(i).equals(user)) {
                    contains_user = true;
                    break;
                }
            }
            if (!contains_user) {
                this.whitelistedUsers.put(user);
            } else {
                return;
            }
        }
        else if(action.equalsIgnoreCase("remove"))
        {
            for (int i=0;i<this.whitelistedUsers.length();i++){
                if(this.whitelistedUsers.get(i).equals(user)) {
                    this.whitelistedUsers.remove(i);
                    break;
                }
            }
        }
        this.manager.writeSettings();
    }
    public void setTextChannel(TextChannel tc)
    {
        this.textId = tc == null ? 0 : tc.getIdLong();
        this.manager.writeSettings();
    }
    
    public void setVoiceChannel(VoiceChannel vc)
    {
        this.voiceId = vc == null ? 0 : vc.getIdLong();
        this.manager.writeSettings();
    }
    
    public void setDJRole(Role role)
    {
        this.roleId = role == null ? 0 : role.getIdLong();
        this.manager.writeSettings();
    }
    
    public void setVolume(int volume)
    {
        this.volume = volume;
        this.manager.writeSettings();
    }
    
    public void setDefaultPlaylist(String defaultPlaylist)
    {
        this.defaultPlaylist = defaultPlaylist;
        this.manager.writeSettings();
    }
    
    public void setRepeatMode(RepeatMode mode)
    {
        this.repeatMode = mode;
        this.manager.writeSettings();
    }
    
    public void setPrefix(String prefix)
    {
        this.prefix = prefix;
        this.manager.writeSettings();
    }

    public void setSkipRatio(double skipRatio)
    {
        this.skipRatio = skipRatio;
        this.manager.writeSettings();
    }
}
