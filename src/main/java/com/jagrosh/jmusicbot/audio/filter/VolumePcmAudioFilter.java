package com.jagrosh.jmusicbot.audio.filter;

import com.github.natanbc.lavadsp.util.FloatToFloatFunction;
import com.jagrosh.jmusicbot.utils.VectorSupport;
import com.sedmelluq.discord.lavaplayer.filter.AudioFilter;
import com.sedmelluq.discord.lavaplayer.filter.FloatPcmAudioFilter;
import com.sedmelluq.discord.lavaplayer.filter.UniversalPcmAudioFilter;
import com.sedmelluq.discord.lavaplayer.format.AudioDataFormat;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

/**
 * Updates the effect volume, with a multiplier ranging from 0 to 5.
 */
public class VolumePcmAudioFilter implements FloatPcmAudioFilter {
    private final FloatPcmAudioFilter downstream;
    private float volume = 1.0f;

    public VolumePcmAudioFilter(FloatPcmAudioFilter downstream) {
        this.downstream = downstream;
    }

    @Deprecated
    public VolumePcmAudioFilter(FloatPcmAudioFilter downstream, int channelCount, int bufferSize) {
        this(downstream);
    }

    @Deprecated
    public VolumePcmAudioFilter(FloatPcmAudioFilter downstream, int channelCount) {
        this(downstream);
    }

    /**
     * Returns the volume multiplier. 1.0 means unmodified.
     *
     * @return The current volume.
     */
    public float getVolume() {
        return volume;
    }

    /**
     * Sets the volume multiplier. 1.0 means unmodified.
     *
     * @param volume Volume to use.
     *
     * @return {@code this}, for chaining calls.
     */
    public VolumePcmAudioFilter setVolume(float volume) {
//        if(volume <= 0) {
//            throw new IllegalArgumentException("Volume <= 0.0");
//        }
//        if(volume > 5) {
//            throw new IllegalArgumentException("Volume > 5.0");
//        }
        this.volume = volume;
        return this;
    }

    /**
     * Updates the volume multiplier, using a function that accepts the current value
     * and returns a new value.
     *
     * @param function Function used to map the depth.
     *
     * @return {@code this}, for chaining calls
     */
    public VolumePcmAudioFilter updateVolume(FloatToFloatFunction function) {
        return setVolume(function.apply(volume));
    }

    @Override
    public void process(float[][] input, int offset, int length) throws InterruptedException {
        for(float[] array : input) {
            VectorSupport.volume(array, offset, length, volume);
        }
        downstream.process(input, offset, length);
    }

    @Override
    public void seekPerformed(long requestedTime, long providedTime) {
        //nothing to do
    }

    @Override
    public void flush() throws InterruptedException {
        //nothing to do
    }

    @Override
    public void close() {
        //nothing to do
    }

    public static class Config extends AudioFilterConfig {
        private float volume = 1;

        public Config(float volume) {
            this.volume = volume;
        }

        @Override
        public AudioFilter build(AudioDataFormat format, UniversalPcmAudioFilter output) {
            VolumePcmAudioFilter audioFilter = new VolumePcmAudioFilter(output);
            audioFilter.setVolume(volume);
            return audioFilter;
        }

        @Override
        public String getDescription() {
            return String.format("Volume boost to `%s`", volume);
        }
    }
}
