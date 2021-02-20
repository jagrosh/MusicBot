package com.jagrosh.jmusicbot.audio;

import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.audio.filter.AudioFilterConfig;
import com.jagrosh.jmusicbot.audio.filter.SplitPixlFilterFactory;
import com.sedmelluq.discord.lavaplayer.filter.AudioFilter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FilterManager {
    private final Bot bot;
    private Map<Long, SplitPixlFilterFactory> storage = new HashMap<>();

    public SplitPixlFilterFactory get(long id) {
        SplitPixlFilterFactory filters = storage.get(id);
        if (filters == null) {
            filters = new SplitPixlFilterFactory();
            storage.put(id, filters);
        }
        return filters;
    }

    public void addFilter(long id, AudioFilterConfig filter) {
        List<AudioFilterConfig> filters = get(id).getStack();
        filters.add(filter);
        SplitPixlFilterFactory factory = new SplitPixlFilterFactory(filters);
        update(id, factory);
    }

    public AudioFilterConfig removeFilter(long id, int val) {
        List<AudioFilterConfig> filters = get(id).getStack();
        AudioFilterConfig top = filters.remove(val);
        SplitPixlFilterFactory factory = new SplitPixlFilterFactory(filters);
        update(id, factory);
        return top;
    }

    public AudioFilterConfig removeLastFilter(long id) {
        List<AudioFilterConfig> filters = get(id).getStack();
        AudioFilterConfig top = filters.remove(filters.size() - 1);
        SplitPixlFilterFactory factory = new SplitPixlFilterFactory(filters);
        update(id, factory);
        return top;
    }

    public void clear(long id) {
        SplitPixlFilterFactory factory = new SplitPixlFilterFactory();
        update(id, factory);
    }

    private void update(long id, SplitPixlFilterFactory factory) {
        try {
            AudioHandler h = (AudioHandler) bot.getJDA().getGuildById(id).getAudioManager().getSendingHandler();
            h.getPlayer().setFilterFactory(factory);
        } catch (NullPointerException n) {
            // ignore
        }
        storage.put(id, factory);
    }


    public FilterManager(Bot bot) {
        this.bot = bot;
    }
}
