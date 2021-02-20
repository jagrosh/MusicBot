package com.jagrosh.jmusicbot.audio;

import com.sedmelluq.discord.lavaplayer.filter.PcmFilterFactory;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerOptions;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEvent;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventListener;
import com.sedmelluq.discord.lavaplayer.player.event.PlayerPauseEvent;
import com.sedmelluq.discord.lavaplayer.player.event.PlayerResumeEvent;
import com.sedmelluq.discord.lavaplayer.player.event.TrackEndEvent;
import com.sedmelluq.discord.lavaplayer.player.event.TrackExceptionEvent;
import com.sedmelluq.discord.lavaplayer.player.event.TrackStartEvent;
import com.sedmelluq.discord.lavaplayer.player.event.TrackStuckEvent;
import com.sedmelluq.discord.lavaplayer.tools.ExceptionTools;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.track.InternalAudioTrack;
import com.sedmelluq.discord.lavaplayer.track.TrackStateListener;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrameProvider;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrameProviderTools;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioTrackExecutor;
import com.sedmelluq.discord.lavaplayer.track.playback.LocalAudioTrackExecutor;
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason.CLEANUP;
import static com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason.FINISHED;
import static com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason.LOAD_FAILED;
import static com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason.REPLACED;
import static com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason.STOPPED;

/**
 * An audio player that is capable of playing audio tracks and provides audio frames from the currently playing track.
 */
public class SplitPixlAudioPlayer implements AudioPlayer, TrackStateListener {
    private static final Logger log = LoggerFactory.getLogger(AudioPlayer.class);

    private volatile InternalAudioTrack activeTrack;
    private volatile long lastRequestTime;
    private volatile long lastReceiveTime;
    private volatile boolean stuckEventSent;
    private volatile InternalAudioTrack shadowTrack;
    private final AtomicBoolean paused;
    private final DefaultAudioPlayerManager manager;
    private final List<AudioEventListener> listeners;
    private final Object trackSwitchLock;
    private final AudioPlayerOptions options;

    /**
     * @param manager Audio player manager which this player is attached to
     */
    public SplitPixlAudioPlayer(DefaultAudioPlayerManager manager) {
        this.manager = manager;
        activeTrack = null;
        paused = new AtomicBoolean();
        listeners = new ArrayList<>();
        trackSwitchLock = new Object();
        options = new AudioPlayerOptions();
    }

    /**
     * @return Currently playing track
     */
    public AudioTrack getPlayingTrack() {
        return activeTrack;
    }

    /**
     * @param track The track to start playing
     */
    public void playTrack(AudioTrack track) {
        startTrack(track, false);
    }

    /**
     * @param track The track to start playing, passing null will stop the current track and return false
     * @param noInterrupt Whether to only start if nothing else is playing
     * @return True if the track was started
     */
    public boolean startTrack(AudioTrack track, boolean noInterrupt) {
        InternalAudioTrack newTrack = (InternalAudioTrack) track;
        InternalAudioTrack previousTrack;

        synchronized (trackSwitchLock) {
            previousTrack = activeTrack;

            if (noInterrupt && previousTrack != null) {
                return false;
            }

            activeTrack = newTrack;
            lastRequestTime = System.currentTimeMillis();
            lastReceiveTime = System.nanoTime();
            stuckEventSent = false;

            if (previousTrack != null) {
                previousTrack.stop();
                dispatchEvent(new TrackEndEvent(this, previousTrack, newTrack == null ? STOPPED : REPLACED));

                shadowTrack = previousTrack;
            }
        }

        if (newTrack == null) {
            shadowTrack = null;
            return false;
        }

        dispatchEvent(new TrackStartEvent(this, newTrack));

        manager.executeTrack(this, newTrack, manager.getConfiguration(), options);
        return true;
    }

    /**
     * Stop currently playing track.
     */
    public void stopTrack() {
        stopWithReason(STOPPED);
    }

    private void stopWithReason(AudioTrackEndReason reason) {
        shadowTrack = null;

        synchronized (trackSwitchLock) {
            InternalAudioTrack previousTrack = activeTrack;
            activeTrack = null;

            if (previousTrack != null) {
                previousTrack.stop();
                dispatchEvent(new TrackEndEvent(this, previousTrack, reason));
            }
        }
    }

    private AudioFrame provideShadowFrame() {
        InternalAudioTrack shadow = shadowTrack;
        AudioFrame frame = null;

        if (shadow != null) {
            frame = shadow.provide();

            if (frame != null && frame.isTerminator()) {
                shadowTrack = null;
                frame = null;
            }
        }

        return frame;
    }

    private boolean provideShadowFrame(MutableAudioFrame targetFrame) {
        InternalAudioTrack shadow = shadowTrack;

        if (shadow != null && shadow.provide(targetFrame)) {
            if (targetFrame.isTerminator()) {
                shadowTrack = null;
                return false;
            }

            return true;
        }

        return false;
    }

    @Override
    public AudioFrame provide() {
        return AudioFrameProviderTools.delegateToTimedProvide(this);
    }

    @Override
    public AudioFrame provide(long timeout, TimeUnit unit) throws TimeoutException, InterruptedException {
        InternalAudioTrack track;

        lastRequestTime = System.currentTimeMillis();

        if (timeout == 0 && paused.get()) {
            return null;
        }

        while ((track = activeTrack) != null) {
            AudioFrame frame = timeout > 0 ? track.provide(timeout, unit) : track.provide();

            if (frame != null) {
                lastReceiveTime = System.nanoTime();
                shadowTrack = null;

                if (frame.isTerminator()) {
                    handleTerminator(track);
                    continue;
                }
            } else if (timeout == 0) {
                checkStuck(track);

                frame = provideShadowFrame();
            }

            return frame;
        }

        return null;
    }

    @Override
    public boolean provide(MutableAudioFrame targetFrame) {
        try {
            return provide(targetFrame, 0, TimeUnit.MILLISECONDS);
        } catch (TimeoutException | InterruptedException e) {
            ExceptionTools.keepInterrupted(e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean provide(MutableAudioFrame targetFrame, long timeout, TimeUnit unit)
            throws TimeoutException, InterruptedException {

        InternalAudioTrack track;

        lastRequestTime = System.currentTimeMillis();

        if (timeout == 0 && paused.get()) {
            return false;
        }

        while ((track = activeTrack) != null) {
            if (timeout > 0 ? track.provide(targetFrame, timeout, unit) : track.provide(targetFrame)) {
                lastReceiveTime = System.nanoTime();
                shadowTrack = null;

                if (targetFrame.isTerminator()) {
                    handleTerminator(track);
                    continue;
                }

                return true;
            } else if (timeout == 0) {
                checkStuck(track);
                return provideShadowFrame(targetFrame);
            } else {
                return false;
            }
        }

        return false;
    }

    private void handleTerminator(InternalAudioTrack track) {
        synchronized (trackSwitchLock) {
            if (activeTrack == track) {
                activeTrack = null;

                dispatchEvent(new TrackEndEvent(this, track, track.getActiveExecutor().failedBeforeLoad() ? LOAD_FAILED : FINISHED));
            }
        }
    }

    private void checkStuck(AudioTrack track) {
        if (!stuckEventSent && System.nanoTime() - lastReceiveTime > manager.getTrackStuckThresholdNanos()) {
            stuckEventSent = true;

            StackTraceElement[] stackTrace = getStackTrace(track);
            long threshold = TimeUnit.NANOSECONDS.toMillis(manager.getTrackStuckThresholdNanos());

            dispatchEvent(new TrackStuckEvent(this, track, threshold, stackTrace));
        }
    }

    private StackTraceElement[] getStackTrace(AudioTrack track) {
        if (track instanceof InternalAudioTrack) {
            AudioTrackExecutor executor = ((InternalAudioTrack) track).getActiveExecutor();

            if (executor instanceof LocalAudioTrackExecutor) {
                return ((LocalAudioTrackExecutor) executor).getStackTrace();
            }
        }

        return null;
    }

    public int getVolume() {
        return options.volumeLevel.get();
    }

    public void setVolume(int volume) {
        options.volumeLevel.set(volume);
    }

    public void setFilterFactory(PcmFilterFactory factory) {
        options.filterFactory.set(factory);
    }

    public PcmFilterFactory getFilterFactory() {
        return options.filterFactory.get();
    }

    public void setFrameBufferDuration(Integer duration) {
        if (duration != null) {
            duration = Math.max(200, duration);
        }

        options.frameBufferDuration.set(duration);
    }

    /**
     * @return Whether the player is paused
     */
    public boolean isPaused() {
        return paused.get();
    }

    /**
     * @param value True to pause, false to resume
     */
    public void setPaused(boolean value) {
        if (paused.compareAndSet(!value, value)) {
            if (value) {
                dispatchEvent(new PlayerPauseEvent(this));
            } else {
                dispatchEvent(new PlayerResumeEvent(this));
                lastReceiveTime = System.nanoTime();
            }
        }
    }

    /**
     * Destroy the player and stop playing track.
     */
    public void destroy() {
        stopTrack();
    }

    /**
     * Add a listener to events from this player.
     * @param listener New listener
     */
    public void addListener(AudioEventListener listener) {
        synchronized (trackSwitchLock) {
            listeners.add(listener);
        }
    }

    /**
     * Remove an attached listener using identity comparison.
     * @param listener The listener to remove
     */
    public void removeListener(AudioEventListener listener) {
        synchronized (trackSwitchLock) {
            for (Iterator<AudioEventListener> iterator = listeners.iterator(); iterator.hasNext(); ) {
                if (iterator.next() == listener) {
                    iterator.remove();
                }
            }
        }
    }

    private void dispatchEvent(AudioEvent event) {
        log.debug("Firing an event with class {}", event.getClass().getSimpleName());

        synchronized (trackSwitchLock) {
            for (AudioEventListener listener : listeners) {
                try {
                    listener.onEvent(event);
                } catch (Exception e) {
                    log.error("Handler of event {} threw an exception.", event, e);
                }
            }
        }
    }

    @Override
    public void onTrackException(AudioTrack track, FriendlyException exception) {
        dispatchEvent(new TrackExceptionEvent(this, track, exception));
    }

    @Override
    public void onTrackStuck(AudioTrack track, long thresholdMs) {
        dispatchEvent(new TrackStuckEvent(this, track, thresholdMs, null));
    }

    /**
     * Check if the player should be "cleaned up" - stopped due to nothing using it, with the given threshold.
     * @param threshold Threshold in milliseconds to use
     */
    public void checkCleanup(long threshold) {
        AudioTrack track = getPlayingTrack();
        if (track != null && System.currentTimeMillis() - lastRequestTime >= threshold) {
            log.debug("Triggering cleanup on an audio player playing track {}", track);

            stopWithReason(CLEANUP);
        }
    }
}
