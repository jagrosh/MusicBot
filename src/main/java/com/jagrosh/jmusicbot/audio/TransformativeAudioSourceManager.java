/*
 * Copyright 2021 John Grosh <john.a.grosh@gmail.com>.
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
package com.jagrosh.jmusicbot.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.AudioItem;
import com.sedmelluq.discord.lavaplayer.track.AudioReference;
import com.typesafe.config.Config;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author John Grosh (john.a.grosh@gmail.com)
 */
public class TransformativeAudioSourceManager extends YoutubeAudioSourceManager
{
    private final static Logger log = LoggerFactory.getLogger(TransformativeAudioSourceManager.class);
    private final String name, regex, replacement, selector, format;
    
    public TransformativeAudioSourceManager(String name, Config object)
    {
        this(name, object.getString("regex"), object.getString("replacement"), object.getString("selector"), object.getString("format"));
    }
    
    public TransformativeAudioSourceManager(String name, String regex, String replacement, String selector, String format)
    {
        this.name = name;
        this.regex = regex;
        this.replacement = replacement;
        this.selector = selector;
        this.format = format;
    }

    @Override
    public String getSourceName()
    {
        return name;
    }

    @Override
    public AudioItem loadItem(AudioPlayerManager apm, AudioReference ar)
    {
        if(ar.identifier == null || !ar.identifier.matches(regex))
            return null;
        try
        {
            String url = ar.identifier.replaceAll(regex, replacement);
            Document doc = Jsoup.connect(url).get();
            String value = doc.selectFirst(selector).ownText();
            String formattedValue = String.format(format, value);
            return super.loadItem(apm, new AudioReference(formattedValue, null));
        }
        catch (PatternSyntaxException ex)
        {
            log.info(String.format("Invalid pattern syntax '%s' in source '%s'", regex, name));
        }
        catch (IOException ex)
        {
            log.warn(String.format("Failed to resolve URL in source '%s': ", name), ex);
        }
        catch (Exception ex)
        {
            log.warn(String.format("Exception in source '%s'", name), ex);
        }
        return null;
    }
    
    public static List<TransformativeAudioSourceManager> createTransforms(Config transforms)
    {
        try
        {
            return transforms.root().entrySet().stream()
                    .map(e -> new TransformativeAudioSourceManager(e.getKey(), transforms.getConfig(e.getKey())))
                    .collect(Collectors.toList());
        }
        catch (Exception ex)
        {
            log.warn("Invalid transform ", ex);
            return Collections.emptyList();
        }
    }
}
