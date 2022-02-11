/*
 * Copyright 2018 John Grosh <john.a.grosh@gmail.com>.
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

import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
import com.jagrosh.jdautilities.command.GuildSettingsManager;
import com.jagrosh.jmusicbot.settings.Settings.EmojiOption;
import com.jagrosh.jmusicbot.utils.OtherUtil;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import net.dv8tion.jda.api.entities.Guild;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;

/**
 *
 * @author John Grosh (john.a.grosh@gmail.com)
 */
public class SettingsManager implements GuildSettingsManager<Settings>
{
    private final static double SKIP_RATIO = .55;
    private final HashMap<Long,Settings> settings;

    public SettingsManager()
    {
        this.settings = new HashMap<>();
        try {
            JSONObject loadedSettings = new JSONObject(OtherUtil.decodeStringWithBOM(Files.readAllBytes(OtherUtil.getPath("serversettings.json"))));            
            loadedSettings.keySet().forEach((id) -> {
                JSONObject o = loadedSettings.getJSONObject(id);

                // Legacy version support: On versions 0.3.3 and older, the repeat mode was represented as a boolean.
                if (!o.has("repeat_mode") && o.has("repeat") && o.getBoolean("repeat"))
                    o.put("repeat_mode", RepeatMode.ALL);


                settings.put(Long.parseLong(id), new Settings(this,
                        o.has("text_channel_id") ? o.getString("text_channel_id")            : null,
                        o.has("voice_channel_id")? o.getString("voice_channel_id")           : null,
                        o.has("dj_role_id")      ? o.getString("dj_role_id")                 : null,
                        o.has("volume")          ? o.getInt("volume")                        : 100,
                        o.has("default_playlist")? o.getString("default_playlist")           : null,
                        o.has("repeat_mode")     ? o.getEnum(RepeatMode.class, "repeat_mode"): RepeatMode.OFF,
                        o.has("prefix")          ? o.getString("prefix")                     : null,
                        o.has("skip_ratio")      ? o.getDouble("skip_ratio")                 : SKIP_RATIO,
                        getEmojiOptions(o, "success"),
                        getEmojiOptions(o, "warning"),
                        getEmojiOptions(o, "error"),
                        getEmojiOptions(o, "loading"),
                        getEmojiOptions(o, "searching")));
            });
        } catch(IOException | JSONException e) {
            LoggerFactory.getLogger("Settings").warn("Failed to load server settings (this is normal if no settings have been set yet): "+e);
        }
    }

    private static Settings.EmojiOption[] getEmojiOptions(JSONObject o, String key) {
        if (!o.has(key)) return null;
        if (o.get(key) instanceof String) return new Settings.EmojiOption[] { new Settings.EmojiOption(o.getString(key), 1) };
        JSONArray options = o.getJSONArray(key);
        if (options.length() <= 0) return null;
        Settings.EmojiOption[] parsedOptions = new Settings.EmojiOption[options.length()];
        boolean hasWeights = false; // initial value won't be used.
        for (int i = 0; i < options.length(); i++) {
            if (options.get(i) instanceof String) {
                if (i != 0 && hasWeights) { 
                    LoggerFactory.getLogger("ServerSettings").error("Emoji list \""+key+"\" uses a mix of emojis and weighted emojis. Ignoring list.");
                    return null;
                }
                hasWeights = false;
                parsedOptions[i] = new Settings.EmojiOption(options.getString(i), 1);
            } else {
                if (i != 0 && !hasWeights) { 
                    LoggerFactory.getLogger("ServerSettings").error("Emoji list \""+key+"\" uses a mix of emojis and weighted emojis. Ignoring list.");
                    return null;
                }
                hasWeights = true;
                JSONObject option = options.getJSONObject(i);
                parsedOptions[i] = new Settings.EmojiOption(option.getString("emoji"), option.getDouble("weight"));
            }
        }
        return parsedOptions;
    }
    
    /**
     * Gets non-null settings for a Guild
     * 
     * @param guild the guild to get settings for
     * @return the existing settings, or new settings for that guild
     */
    @Override
    public Settings getSettings(Guild guild)
    {
        if (guild == null) return createDefaultSettings();
        return getSettings(guild.getIdLong());
    }
    
    public Settings getSettings(long guildId)
    {
        return settings.computeIfAbsent(guildId, id -> createDefaultSettings());
    }
    
    private Settings createDefaultSettings()
    {
        return new Settings(this, 0, 0, 0, 100, null, RepeatMode.OFF, null, SKIP_RATIO, null, null, null, null, null);
    }
    
    protected void writeSettings()
    {
        JSONObject obj = new JSONObject();
        settings.keySet().stream().forEach(key -> {
            JSONObject o = new JSONObject();
            Settings s = settings.get(key);
            if(s.textId!=0)
                o.put("text_channel_id", Long.toString(s.textId));
            if(s.voiceId!=0)
                o.put("voice_channel_id", Long.toString(s.voiceId));
            if(s.roleId!=0)
                o.put("dj_role_id", Long.toString(s.roleId));
            if(s.getVolume()!=100)
                o.put("volume",s.getVolume());
            if(s.getDefaultPlaylist() != null)
                o.put("default_playlist", s.getDefaultPlaylist());
            if(s.getRepeatMode()!=RepeatMode.OFF)
                o.put("repeat_mode", s.getRepeatMode());
            if(s.getPrefix() != null)
                o.put("prefix", s.getPrefix());
            if(s.getSkipRatio() != SKIP_RATIO)
                o.put("skip_ratio", s.getSkipRatio());
            putEmojiOptions(o, "success", s.getSuccessEmojis());
            putEmojiOptions(o, "warning", s.getWarningEmojis());
            putEmojiOptions(o, "error", s.getErrorEmojis());
            putEmojiOptions(o, "loading", s.getLoadingEmojis());
            putEmojiOptions(o, "searching", s.getSearchingEmojis());
            obj.put(Long.toString(key), o);
        });
        try {
            Files.write(OtherUtil.getPath("serversettings.json"), obj.toString(4).getBytes("utf-16"));
        } catch(IOException ex){
            LoggerFactory.getLogger("Settings").warn("Failed to write to file: "+ex);
        }
    }

    private static void putEmojiOptions(JSONObject o, String key, Settings.EmojiOption[] options) {
        if (options == null || options.length == 0) return;
        if (options.length == 1) o.put(key, options[0].emoji);
        else {
            boolean defaultWeights = true;
            for (Settings.EmojiOption option : options) {
                if (option.weight != 1) {
                    defaultWeights = false;
                    break;
                }
            }

            JSONArray serializedOptions = new JSONArray();
            if (defaultWeights) {
                for (Settings.EmojiOption option : options) {
                    serializedOptions.put(option.emoji);
                }
            } else {
                for (Settings.EmojiOption option : options) {
                    JSONObject serializedOption = new JSONObject();
                    serializedOption.put("emoji", option.emoji);
                    serializedOption.put("weight", option.weight);
                    serializedOptions.put(serializedOption);
                }
            }

            o.put(key, serializedOptions);
        }
    }
}
