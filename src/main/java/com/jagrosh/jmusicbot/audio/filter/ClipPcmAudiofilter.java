package com.jagrosh.jmusicbot.audio.filter;

import com.github.natanbc.lavadsp.util.FloatToFloatFunction;
import com.sedmelluq.discord.lavaplayer.filter.AudioFilter;
import com.sedmelluq.discord.lavaplayer.filter.FloatPcmAudioFilter;
import com.sedmelluq.discord.lavaplayer.filter.UniversalPcmAudioFilter;
import com.sedmelluq.discord.lavaplayer.format.AudioDataFormat;

/**
 * Updates the effect volume, with a multiplier ranging from 0 to 5.
 */
public class ClipPcmAudiofilter implements FloatPcmAudioFilter {
    private final FloatPcmAudioFilter downstream;
    private float volume = 1.0f;
    private int fac = 2;
    private int fac2 = 8;

    public ClipPcmAudiofilter(FloatPcmAudioFilter downstream) {
        this.downstream = downstream;
    }

    @Deprecated
    public ClipPcmAudiofilter(FloatPcmAudioFilter downstream, int channelCount, int bufferSize) {
        this(downstream);
    }

    @Deprecated
    public ClipPcmAudiofilter(FloatPcmAudioFilter downstream, int channelCount) {
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
    public ClipPcmAudiofilter setVolume(float volume) {
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
    public ClipPcmAudiofilter updateVolume(FloatToFloatFunction function) {
        return setVolume(function.apply(volume));
    }

    @Override
    public void process(float[][] input, int offset, int length) throws InterruptedException {
        for(float[] array : input) {
            for (int i = 0; i < array.length; i++) {
                if (array[i] == 0)
                    continue;
                if (array[i] > 0) {
                    array[i] = 1;
                } else {
                    array[i] = -1;
                }
            }
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

        public Config() {
            this.volume = 1;
        }

        @Override
        public AudioFilter build(AudioDataFormat format, UniversalPcmAudioFilter output) {
            ClipPcmAudiofilter audioFilter = new ClipPcmAudiofilter(output);
            return audioFilter;
        }

        @Override
        public String getDescription() {
            return String.format("Clip");
        }
    }
}
