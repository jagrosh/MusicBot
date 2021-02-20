package com.jagrosh.jmusicbot.audio.filter;

import com.github.natanbc.lavadsp.timescale.TimescalePcmAudioFilter;
import com.sedmelluq.discord.lavaplayer.filter.AudioFilter;
import com.sedmelluq.discord.lavaplayer.filter.UniversalPcmAudioFilter;
import com.sedmelluq.discord.lavaplayer.format.AudioDataFormat;

public class TimescaleConfig extends AudioFilterConfig {
    private float scale = 1;

    public TimescaleConfig(float scale) {
        this.scale = scale;
    }

    @Override
    public AudioFilter build(AudioDataFormat format, UniversalPcmAudioFilter output) {
        TimescalePcmAudioFilter audioFilter = new TimescalePcmAudioFilter(output, format.channelCount, format.sampleRate);
        audioFilter.setSpeed(scale);
        return audioFilter;
    }

    @Override
    public String getDescription() {
        return String.format("Timescale to `%s`", scale);
    }
}
