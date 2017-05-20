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
    public final static Settings DEFAULT_SETTINGS = new Settings(0, 0, 0, 100, null);

    private long textId;
    private long voiceId;
    private long djRoleId;
    private long blacklistRoleId;
    private int volume;
    private String defaultPlaylist;

    public Settings(String textId, String voiceId, String djRoleId, String blacklistRoleId int volume, String defaultPlaylist)
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
            this.djRoleId = Long.parseLong(djRoleId);
        }
        catch(NumberFormatException e)
        {
            this.djRoleId = 0;
        }
        try
        {
            this.blacklistRoleId = Long.parseLong(blacklistRoleId);
        }
        catch(NumberFormatException e)
        {
            this.blacklistRoleId = 0;
        }
        this.volume = volume;
        this.defaultPlaylist = defaultPlaylist;
    }

    public Settings(long textId, long voiceId, long roleId, long blacklistRoleId, int volume, String defaultPlaylist)
    {
        this.textId = textId;
        this.voiceId = voiceId;
        this.djRoleId = djRoleId;
        this.blacklistRoleId = blacklistRoleId;
        this.volume = volume;
        this.defaultPlaylist = defaultPlaylist;
    }

    public long getTextId()
    {
        return textId;
    }

    public long getVoiceId()
    {
        return voiceId;
    }

    public long getDjRoleId()
    {
        return djRoleId;
    }

    public long getBlacklistRoleId()
    {
        return blacklistRoleId;
    }

    public int getVolume()
    {
        return volume;
    }

    public String getDefaultPlaylist()
    {
        return defaultPlaylist;
    }

    public void setTextId(long id)
    {
        this.textId = id;
    }

    public void setVoiceId(long id)
    {
        this.voiceId = id;
    }

    public void setDjRoleId(long id)
    {
        this.djRoleId = id;
    }

    public void setBlacklistRoleId(long id)
    {
        this.blacklistRoleId = id;
    }

    public void setVolume(int volume)
    {
        this.volume = volume;
    }

    public void setDefaultPlaylist(String defaultPlaylist)
    {
        this.defaultPlaylist = defaultPlaylist;
    }


}
