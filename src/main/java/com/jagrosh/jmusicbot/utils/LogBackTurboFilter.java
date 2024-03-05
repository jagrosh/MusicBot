/*
 * Copyright 2024 John Grosh <john.a.grosh@gmail.com>.
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
package com.jagrosh.jmusicbot.utils;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.turbo.TurboFilter;
import ch.qos.logback.core.spi.FilterReply;
import org.slf4j.Marker;

/**
 * A TurboFilter, currently only used to suppress specific log messages from libraries.
 *
 * @author Michaili K. <git@michaili.dev>
 */
public class LogBackTurboFilter extends TurboFilter 
{
    @Override
    public FilterReply decide(Marker marker, Logger logger, Level level, String format, Object[] params, Throwable t) 
    {
        // Suppresses the auth token warning from the YoutubeAudioSourceManager
        // https://github.com/jagrosh/MusicBot/pull/1490#issuecomment-1974070225
        if (logger.getName().equals("com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAccessTokenTracker")
                && format.equals("YouTube auth tokens can't be retrieved because email and password is not set in YoutubeAudioSourceManager, age restricted videos will throw exceptions.")
        ) {
            return FilterReply.DENY;
        }

        return FilterReply.NEUTRAL;
    }
}
