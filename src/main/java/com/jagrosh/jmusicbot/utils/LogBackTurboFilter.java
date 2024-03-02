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
public class LogBackTurboFilter extends TurboFilter {

    @Override
    public FilterReply decide(Marker marker, Logger logger, Level level, String format, Object[] params, Throwable t) {
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
