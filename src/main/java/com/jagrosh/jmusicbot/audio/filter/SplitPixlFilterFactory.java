package com.jagrosh.jmusicbot.audio.filter;

import com.sedmelluq.discord.lavaplayer.filter.*;
import com.sedmelluq.discord.lavaplayer.filter.converter.ToFloatAudioFilter;
import com.sedmelluq.discord.lavaplayer.filter.converter.ToShortAudioFilter;
import com.sedmelluq.discord.lavaplayer.filter.converter.ToSplitShortAudioFilter;
import com.sedmelluq.discord.lavaplayer.format.AudioDataFormat;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SplitPixlFilterFactory implements PcmFilterFactory {

    private List<AudioFilterConfig> stack = new ArrayList<>();

    public SplitPixlFilterFactory(){}

    public SplitPixlFilterFactory(List<AudioFilterConfig> stack) {
        this.stack = stack;
    }

    public List<AudioFilterConfig> getStack() {
        return stack;
    }

    public String[] getDescriptions() {
        List<String> descriptions = new ArrayList<>();
        for (AudioFilterConfig filterConfig : stack) {
            descriptions.add(filterConfig.getDescription());
        }
        return descriptions.toArray(new String[0]);
    }

    @Override
    public List<AudioFilter> buildChain(AudioTrack audioTrack, AudioDataFormat format, UniversalPcmAudioFilter output) {
        if (stack.size() == 0)
            return Collections.emptyList();
//        FilterChainBuilder builder = new FilterChainBuilder();
//        builder.addFirst(output);
        UniversalPcmAudioFilter last = output;
        List<AudioFilter> list = new ArrayList<>();
        for (AudioFilterConfig config : stack) {
//            AudioFilter filter = config.build(format, builder.makeFirstUniversal(format.channelCount));
            AudioFilter first = config.build(format, last);
            if (first != null) {
//                builder.addFirst(filter);
                UniversalPcmAudioFilter universalInput;

                if (first instanceof SplitShortPcmAudioFilter) {
                    universalInput = new ToSplitShortAudioFilter((SplitShortPcmAudioFilter) first, format.channelCount);
                } else if (first instanceof FloatPcmAudioFilter) {
                    universalInput = new ToFloatAudioFilter((FloatPcmAudioFilter) first, format.channelCount);
                } else if (first instanceof ShortPcmAudioFilter) {
                    universalInput = new ToShortAudioFilter((ShortPcmAudioFilter) first, format.channelCount);
                } else {
                    throw new RuntimeException("Filter must implement at least one data type.");
                }

                list.add(universalInput);
                last = universalInput;
            }
        }
        Collections.reverse(list);
        return list;
    }

}
