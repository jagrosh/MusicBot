/*
 * Copyright 2020 natanbc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jagrosh.jmusicbot.audio.filter;

import com.github.natanbc.lavadsp.ConverterPcmAudioFilter;
import com.github.natanbc.lavadsp.util.FloatToFloatFunction;
import com.sedmelluq.discord.lavaplayer.filter.AudioFilter;
import com.sedmelluq.discord.lavaplayer.filter.FloatPcmAudioFilter;
import com.sedmelluq.discord.lavaplayer.filter.UniversalPcmAudioFilter;
import com.sedmelluq.discord.lavaplayer.format.AudioDataFormat;

/**
 * Implements the algorithm described <a href="http://phrogz.net/js/framerate-independent-low-pass-filter.html">here</a>.
 *
 * Higher frequencies get suppressed, while lower frequencies pass through this filter, thus the name low pass.
 */
public class LowPassPcmAudioFilter extends ConverterPcmAudioFilter<LowPassConverter> {
    private volatile float smoothing;

    public LowPassPcmAudioFilter(FloatPcmAudioFilter downstream, int channelCount, int bufferSize) {
        super(LowPassConverter::new, downstream, channelCount, bufferSize);
    }

    public LowPassPcmAudioFilter(FloatPcmAudioFilter downstream, int channelCount) {
        super(LowPassConverter::new, downstream, channelCount);
    }

    /**
     * @return The current smoothing. The default is 20.0.
     */
    public float getSmoothing() {
        return smoothing;
    }

    /**
     * @param smoothing Smoothing to use. The default is 20.0.
     *
     * @return {@code this}, for chaining calls.
     */
    public LowPassPcmAudioFilter setSmoothing(float smoothing) {
        for(LowPassConverter converter : converters()) {
            converter.setSmoothing(smoothing);
        }
        this.smoothing = smoothing;
        return this;
    }

    /**
     * Updates the smoothing factor, using a function that accepts the current value
     * and returns a new value.
     *
     * @param function Function used to map the factor.
     *
     * @return {@code this}, for chaining calls
     */
    public LowPassPcmAudioFilter updateSmoothing(FloatToFloatFunction function) {
        return setSmoothing(function.apply(smoothing));
    }

    @Override
    public void seekPerformed(long requestedTime, long providedTime) {
        for(LowPassConverter converter : converters()) {
            converter.onSeek();
        }
        super.seekPerformed(requestedTime, providedTime);
    }

    public static class Config extends AudioFilterConfig {
        public Config() {
        }

        @Override
        public AudioFilter build(AudioDataFormat format, UniversalPcmAudioFilter output) {
            LowPassPcmAudioFilter audioFilter = new LowPassPcmAudioFilter(output, format.channelCount);
            return audioFilter;
        }

        @Override
        public String getDescription() {
            return String.format("Lowpass");
        }
    }
}
