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
package com.jagrosh.jmusicbot;

/**
 *
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class Settings {
    public final static Settings DEFAULT_SETTINGS = new Settings(0, 0, 0, 100, null, false);
    
    private long textId;
    private long voiceId;
    private long roleId;
    private int volume;
    private String defaultPlaylist;
    private boolean repeatMode;
    
    public Settings(String textId, String voiceId, String roleId, int volume, String defaultPlaylist, boolean repeatMode)
    {
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
    }
    
    public Settings(long textId, long voiceId, long roleId, int volume, String defaultPlaylist, boolean repeatMode)
    {
        this.textId = textId;
        this.voiceId = voiceId;
        this.roleId = roleId;
        this.volume = volume;
        this.defaultPlaylist = defaultPlaylist;
        this.repeatMode = repeatMode;
    }
    
    public long getTextId()
    {
        return textId;
    }
    
    public long getVoiceId()
    {
        return voiceId;
    }
    
    public long getRoleId()
    {
        return roleId;
    }
    
    public int getVolume()
    {
        return volume;
    }
    
    public String getDefaultPlaylist()
    {
        return defaultPlaylist;
    }
    
    public boolean getRepeatMode()
    {
        return repeatMode;
    }
    
    public void setTextId(long id)
    {
        this.textId = id;
    }
    
    public void setVoiceId(long id)
    {
        this.voiceId = id;
    }
    
    public void setRoleId(long id)
    {
        this.roleId = id;
    }
    
    public void setVolume(int volume)
    {
        this.volume = volume;
    }
    
    public void setDefaultPlaylist(String defaultPlaylist)
    {
        this.defaultPlaylist = defaultPlaylist;
    }
    
    public void setRepeatMode(boolean mode)
    {
        this.repeatMode = mode;
    }
}
