package com.jagrosh.jmusicbot.audio.filter

import com.github.natanbc.lavadsp.karaoke.KaraokePcmAudioFilter
import com.jagrosh.jmusicbot.audio.filter.AudioFilterConfig
import com.sedmelluq.discord.lavaplayer.filter.AudioFilter
import com.sedmelluq.discord.lavaplayer.filter.UniversalPcmAudioFilter
import com.sedmelluq.discord.lavaplayer.format.AudioDataFormat

class KaraokeConfig : AudioFilterConfig() {
    override fun build(format: AudioDataFormat, output: UniversalPcmAudioFilter): AudioFilter {
        return KaraokePcmAudioFilter(output, format.channelCount, format.sampleRate)
    }

    override fun getDescription(): String {
        return String.format("Karaoke")
    }

    init {
    }
}