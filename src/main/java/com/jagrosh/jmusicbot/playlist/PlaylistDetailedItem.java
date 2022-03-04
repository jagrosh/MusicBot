package com.jagrosh.jmusicbot.playlist;

import com.jagrosh.jmusicbot.audio.RequestMetadata;
import com.jagrosh.jmusicbot.queue.Queueable;
import com.jagrosh.jmusicbot.utils.FormatUtil;

public class PlaylistDetailedItem implements Queueable {

    private long duration;

    private String title;

    private String uri;

    private String username;

    public PlaylistDetailedItem(long duration, String title, String uri, String username){
        this.duration = duration;
        this.title = title;
        this.uri = uri;
        this.username = username;
    }

    @Override
    public long getIdentifier() {
        return 0;
    }

    public long getDuration(){
        return this.duration;
    }


    @Override
    public String toString()
    {
        return "`[" + FormatUtil.formatTime(this.duration) + "]` [**" + this.title + "**](" + this.uri + ") - <@" + this.username + ">";
    }
}
