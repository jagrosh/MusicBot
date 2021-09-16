package com.jagrosh.jmusicbot.audio.filter;

import com.github.natanbc.lavadsp.timescale.TimescalePcmAudioFilter;
import com.sedmelluq.discord.lavaplayer.filter.AudioFilter;
import com.sedmelluq.discord.lavaplayer.filter.UniversalPcmAudioFilter;
import com.sedmelluq.discord.lavaplayer.format.AudioDataFormat;

public class PitchConfig extends AudioFilterConfig {
    private float pitch = 1;

    public PitchConfig(float pitch) {
        this.pitch = pitch;
    }

    @Override
    public AudioFilter build(AudioDataFormat format, UniversalPcmAudioFilter output) {
        TimescalePcmAudioFilter audioFilter = new TimescalePcmAudioFilter(output, format.channelCount, format.sampleRate);
        audioFilter.setPitch(pitch);
        return audioFilter;
    }

    @Override
    public String getDescription() {
        return String.format("Pitch to `%s`", pitch);
    }
}
