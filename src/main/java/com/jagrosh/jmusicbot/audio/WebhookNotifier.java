package com.jagrosh.jmusicbot.audio;

import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.utils.FormatUtil;
import com.jagrosh.jmusicbot.utils.TimeUtil;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import java.awt.Color;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebhookNotifier
{
    private final Logger log = LoggerFactory.getLogger("WebhookNotifier");
    private final Bot bot;
    private final String webhookUrl;
    private final int updateTime;

    public WebhookNotifier(Bot bot, String webhookUrl, int updateTime)
    {
        this.bot = bot;
        this.webhookUrl = webhookUrl;
        this.updateTime = updateTime;
    }

    public int getUpdateTime()
    {
        return updateTime;
    }

    public void sendNowPlaying(AudioTrack track, Guild guild)
    {
        if (webhookUrl == null || webhookUrl.isEmpty())
            return;
        if (guild == null)
            return;
        TextChannel tc = guild.getTextChannels().stream().findFirst().orElse(null);
        String channelName = tc == null ? "Unknown" : tc.getAsMention();
        String title = track.getInfo().title;
        String uri = track.getInfo().uri;
        String author = track.getInfo().author;
        long duration = track.getDuration();
        String thumbnail = track.getInfo().uri != null && track.getInfo().uri.contains("youtube.com")
                ? "https://img.youtube.com/vi/" + track.getIdentifier() + "/mqdefault.jpg"
                : null;

        StringBuilder embed = new StringBuilder();
        embed.append("{\"embeds\":[{");
        embed.append("\"title\":\"Now Playing\",");

        if (uri != null && !uri.isEmpty())
            embed.append("\"url\":\"").append(escapeJson(uri)).append("\",");

        embed.append("\"description\":\"[").append(escapeJson(title)).append("](").append(escapeJson(uri != null ? uri : "")).append(")\\n\\n");
        embed.append("Duration: `").append(TimeUtil.formatTime(duration)).append("`");

        AudioHandler handler = (AudioHandler) guild.getAudioManager().getSendingHandler();
        if (handler != null)
            embed.append(" | Volume: ").append(FormatUtil.volumeIcon(handler.getPlayer().getVolume()));

        embed.append("\",");

        embed.append("\"color\":").append(Color.BLUE.getRGB() & 0xFFFFFF).append(",");
        embed.append("\"footer\":{\"text\":\"Requested in ").append(escapeJson(channelName)).append("\"}");

        if (thumbnail != null)
            embed.append(",\"thumbnail\":{\"url\":\"").append(escapeJson(thumbnail)).append("\"}");

        if (author != null && !author.isEmpty())
            embed.append(",\"author\":{\"name\":\"").append(escapeJson(author)).append("\"}");

        embed.append("}],");
        embed.append("\"username\":\"LMusicBot\",");
        embed.append("\"avatar_url\":\"https://i.imgur.com/zrE80HY.png\"");
        embed.append("}");

        sendJson(embed.toString());
    }

    public void sendQueueUpdate(Guild guild)
    {
        if (webhookUrl == null || webhookUrl.isEmpty())
            return;
        if (guild == null)
            return;
        AudioHandler handler = (AudioHandler) guild.getAudioManager().getSendingHandler();
        if (handler == null)
            return;

        int queueSize = handler.getQueue().size();
        String queueInfo;
        if (queueSize == 0)
            queueInfo = "The queue is empty.";
        else
        {
            StringBuilder sb = new StringBuilder("**").append(queueSize).append("** tracks in queue\\n");
            int show = Math.min(queueSize, 10);
            for (int i = 0; i < show; i++)
            {
                QueuedTrack qt = handler.getQueue().get(i);
                if (qt != null)
                {
                    AudioTrack t = qt.getTrack();
                    sb.append("`").append(i + 1).append(".` [").append(escapeJson(t.getInfo().title)).append("](").append(escapeJson(t.getInfo().uri != null ? t.getInfo().uri : "")).append(")\\n");
                }
            }
            if (queueSize > show)
                sb.append("... and ").append(queueSize - show).append(" more");
            queueInfo = sb.toString();
        }

        String json = "{\"embeds\":[{"
                + "\"title\":\"Queue Update\","
                + "\"description\":\"" + queueInfo + "\","
                + "\"color\":" + (Color.CYAN.getRGB() & 0xFFFFFF)
                + "}],"
                + "\"username\":\"LMusicBot\""
                + "}";

        sendJson(json);
    }

    private void sendJson(String json)
    {
        try
        {
            URI uri = new URI(webhookUrl);
            HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            try (OutputStream os = conn.getOutputStream())
            {
                os.write(json.getBytes(StandardCharsets.UTF_8));
            }
            int code = conn.getResponseCode();
            if (code < 200 || code > 299)
                log.warn("Webhook returned non-2xx status: {}", code);
        }
        catch (Exception e)
        {
            log.warn("Failed to send webhook: {}", e.getMessage());
        }
    }

    private static String escapeJson(String s)
    {
        if (s == null)
            return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
