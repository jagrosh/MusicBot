package com.jagrosh.jmusicbot.utils;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.turbo.TurboFilter;
import ch.qos.logback.core.spi.FilterReply;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

import java.nio.file.Files;

/**
 * A logback turbo filter, used retrieve the YouTube OAuth2 refresh token that gets logged once authorized with YouTube.
 *
 * @author Michaili K. <git@michaili.dev>
 */
public class YouTubeOauth2TurboFilter extends TurboFilter {
    public final static org.slf4j.Logger LOGGER = LoggerFactory.getLogger(YouTubeOauth2TurboFilter.class);

    @Override
    public FilterReply decide(Marker marker, Logger logger, Level level, String format, Object[] params, Throwable t)
    {
        if (!logger.getName().equals("dev.lavalink.youtube.http.YoutubeOauth2Handler")
                || !format.equals("OAUTH INTEGRATION: Token retrieved successfully. Store your refresh token as this can be reused. ({})"))
            return FilterReply.NEUTRAL;
        
        LOGGER.info("Authorization successful & retrieved token! Storing the token in {}", OtherUtil.getPath("youtubetoken.txt").toAbsolutePath());
        try
        {
            Files.write(OtherUtil.getPath("youtubetoken.txt"), params[0].toString().getBytes());
        }
        catch (Exception e)
        {
            LOGGER.error(
                "Failed to write the YouTube OAuth2 refresh token to storage! You will need to authorize again on the next reboot",
                e
            );
        }

        return FilterReply.DENY;
    }
}
