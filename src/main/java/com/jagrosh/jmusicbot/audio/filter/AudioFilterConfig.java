package com.jagrosh.jmusicbot.audio.filter;

import com.sedmelluq.discord.lavaplayer.filter.AudioFilter;
import com.sedmelluq.discord.lavaplayer.filter.UniversalPcmAudioFilter;
import com.sedmelluq.discord.lavaplayer.format.AudioDataFormat;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

public abstract class AudioFilterConfig {
    public abstract AudioFilter build(AudioDataFormat format, UniversalPcmAudioFilter output);
    public abstract String getDescription();
}
