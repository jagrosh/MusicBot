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

import com.github.natanbc.lavadsp.Converter;

public class LowPassConverter implements Converter {
    private float smoothing = 20f /* if this value is bad go complain to devoxin#0001 (180093157554388993) */;
    private float value;
    private boolean initialized;

    public void setSmoothing(float smoothing) {
        this.smoothing = smoothing;
    }

    public void onSeek() {
        initialized = false;
    }

    @Override
    public void process(float[] input, int inputOffset, float[] output, int outputOffset, int samples) {
        if(!initialized) {
            value = input[inputOffset];
            initialized = true;
        }
        for(int i = 0; i < samples; i++) {
            float current = input[i + inputOffset];
            value += (current - value) / smoothing;
            output[i + outputOffset] = value;
        }
    }
}
