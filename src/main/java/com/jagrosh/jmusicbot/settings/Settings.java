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

/**
 *
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class Settings implements GuildSettingsProvider
{
    public static class EmojiOption {
        public final String emoji;
        public final double weight;

        public EmojiOption(String emoji, double weight) {
            this.emoji = emoji;
            this.weight = weight;
        }
    }

    private final SettingsManager manager;
    protected long textId;
    protected long voiceId;
    protected long roleId;
    private int volume;
    private String defaultPlaylist;
    private RepeatMode repeatMode;
    private String prefix;
    private double skipRatio;
    private EmojiOption[] successEmojis, warningEmojis, errorEmojis, loadingEmojis, searchingEmojis;

    public Settings(SettingsManager manager, String textId, String voiceId, String roleId, int volume, String defaultPlaylist, RepeatMode repeatMode, String prefix, double skipRatio, EmojiOption[] successEmojis, EmojiOption[] warningEmojis, EmojiOption[] errorEmojis, EmojiOption[] loadingEmojis, EmojiOption[] searchingEmojis)
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
        this.successEmojis = successEmojis;
        this.warningEmojis = warningEmojis;
        this.errorEmojis = errorEmojis;
        this.loadingEmojis = loadingEmojis;
        this.searchingEmojis = searchingEmojis;
    }
    
    public Settings(SettingsManager manager, long textId, long voiceId, long roleId, int volume, String defaultPlaylist, RepeatMode repeatMode, String prefix, double skipRatio, EmojiOption[] successEmojis, EmojiOption[] warningEmojis, EmojiOption[] errorEmojis, EmojiOption[] loadingEmojis, EmojiOption[] searchingEmojis)
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
        this.successEmojis = successEmojis;
        this.warningEmojis = warningEmojis;
        this.errorEmojis = errorEmojis;
        this.loadingEmojis = loadingEmojis;
        this.searchingEmojis = searchingEmojis;
    }
    
    // Getters
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

    public EmojiOption[] getSuccessEmojis() 
    {
        return successEmojis;
    }

    public EmojiOption[] getWarningEmojis() 
    {
        return warningEmojis;
    }

    public EmojiOption[] getErrorEmojis() 
    {
        return errorEmojis;
    }

    public EmojiOption[] getLoadingEmojis() 
    {
        return loadingEmojis;
    }

    public EmojiOption[] getSearchingEmojis() 
    {
        return searchingEmojis;
    }

    @Override
    public Collection<String> getPrefixes()
    {
        return prefix == null ? Collections.emptySet() : Collections.singleton(prefix);
    }
    
    // Setters
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

    public void setSuccessEmojis(EmojiOption[] success)
    {
        if (success.length == 0) success = null;
        this.successEmojis = success;
        this.manager.writeSettings();
    }

    public void setWarningEmojis(EmojiOption[] warning)
    {
        if (warning.length == 0) warning = null;
        this.warningEmojis = warning;
        this.manager.writeSettings();
    }

    public void setErrorEmojis(EmojiOption[] error)
    {
        if (error.length == 0) error = null;
        this.errorEmojis = error;
        this.manager.writeSettings();
    }

    public void setLoadingEmojis(EmojiOption[] loading)
    {
        if (loading.length == 0) loading = null;
        this.loadingEmojis = loading;
        this.manager.writeSettings();
    }

    public void setSearchingEmojis(EmojiOption[] searching)
    {
        if (searching.length == 0) searching = null;
        this.searchingEmojis = searching;
        this.manager.writeSettings();
    }
}
