package com.jagrosh.jmusicbot.commands.music;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.audio.AudioHandler;
import com.jagrosh.jmusicbot.commands.MusicCommand;
import org.json.JSONArray;
import org.json.JSONObject;
import net.dv8tion.jda.api.Permission;
// import net.dv8tion.jda.api.entities.Message;

public class skipSegCmd extends MusicCommand {
    public skipSegCmd(Bot bot) {

        super(bot);
        this.name = "skipsegment";
        this.help = "Skips to the end of the current non-music segment if it is logged in the SponsorBlock database";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.botPermissions = new Permission[] { Permission.MESSAGE_EMBED_LINKS };
    }

    /**
     * compares given track URL to a regex to extract the unique video id code at
     * the end of the URL.
     * Needed for feeding into sponsorblock API
     * 
     * @param youtubeVideoURL video URL string
     * @return String of video ID code
     */

    private String extractYTIDRegex(String youtubeVideoURL) {
        String pattern = "(?<=youtu.be/|watch\\?v=|/videos/|embed\\/)[^#\\&\\?]*";
        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(youtubeVideoURL);
        if (matcher.find()) {
            return matcher.group();
        } else {
            return "fail"; //probably not a youtube video
        }
    }

    /**
     * Gets the current time stamp of the song in milliseconds
     * 
     * @param handler audio handler object to extract the current time
     * @return returns time in milliseconds
     */
    private long getCurrentTimeStamp(AudioHandler handler) {
        return handler.getPlayer().getPlayingTrack().getPosition();
    }

    /**
     * Takes string response from api, and parses the JSON to extract any skippable
     * segments. Compares start and end of segment to current time.
     * If current time is within the bounds of a segment, return the time stamp of
     * the end of that segment so it canbee skipped to.
     * 
     * @param jsonString Raw string of API response
     * @param curTime    current time of track
     * @return returns end of current skippable segment in milliseconds. If no
     *         segment is currently playing, return -1
     */

    private long parseMusicJSON(String jsonString, long curTime) {
        JSONObject obj = new JSONObject(jsonString);
        JSONArray segmentArr = obj.getJSONArray("segments");
        for (int i = 0; i < segmentArr.length(); i++) {
            Float segStart = segmentArr.getJSONObject(i).getFloat("startTime");
            Float segEnd = segmentArr.getJSONObject(i).getFloat("endTime");
            if (curTime >= segStart && curTime <= segEnd) {
                return (long) (segEnd * 1000);
            }
        }
        return -1; //no skippable segment currently playing

    }

    /**
     * connect to sponsorblock api and attempt to make a GET call to the
     * searchSegments option. Attempts to get the end of whatever segment
     * the player is currently in so the player can skip ahead.
     * 
     * @param videoID string of the video id for the song that is currently playing
     *                off youtube
     * @param handler audio handler to get current time
     * @param event   message event to send error responses
     * @return either returns the end of the current segment in milliseconds, -2 if
     *         no segments were found, or -1 if segments were found but
     *         the track is not currently within a segment.
     * @throws UnsupportedEncodingException
     * @throws IOException
     */
    public long connectToAPI(String videoID, AudioHandler handler, CommandEvent event)
            throws UnsupportedEncodingException, IOException {
        HttpURLConnection con = null;
        long curTime = getCurrentTimeStamp(handler) / 1000; // convert to seconds
        boolean musicSegment = false;

        try {
            String nonMusicURLString = "https://sponsor.ajay.app/api/searchSegments?videoID=";
            nonMusicURLString += videoID;
            URL nonMusicURL = new URL(nonMusicURLString);
            String apiResponse = null;
            try {
                con = (HttpURLConnection) nonMusicURL.openConnection();
                con.setRequestMethod("GET");
                int responsecode = con.getResponseCode();
                if (responsecode == 200) {
                    event.replySuccess("Found non-music segments in database!");
                    musicSegment = true;
                    BufferedReader in = new BufferedReader(new InputStreamReader(
                            con.getInputStream()));
                    while ((apiResponse = in.readLine()) != null) {
                        return parseMusicJSON(apiResponse, curTime);
                    }

                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        // no segments found
        if (!musicSegment) {
            return -2;

        // segments found but player is not in one currently
        } else {
            return -1;
        }
    }

    @Override
    public void doCommand(CommandEvent event) {
        AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
        try {
            String videoId = extractYTIDRegex(handler.getPlayer().getPlayingTrack().getInfo().uri);
            if (videoId != "fail") {
                long reply = connectToAPI(videoId, handler, event);

                if (reply == -2) {
                    event.replyError("No segments found!");
                } else if (reply == -1) {
                    event.replyError("Cannot skip here because no segment is currently playing!");
                } else {
                    handler.getPlayer().getPlayingTrack().setPosition(reply);
                    
                    long absSeconds = reply / 1000;
                    long minutes = absSeconds / 60;
                    long modSeconds = absSeconds % 60;
                    String formattedSeconds = String.format("%02d", modSeconds);
                    String msg = "Skipping ahead to " + minutes + ":" + formattedSeconds + "!";
                    event.replySuccess(msg);
                }
            } else {
                event.replyError("Could not get video ID!"); //should only come up on non-youtube links
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
