/*
 *     Copyright 2016 Austin Keener
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

package spectramusic.entities;

import net.dv8tion.jda.player.hooks.events.*;
import net.dv8tion.jda.player.source.AudioSource;
import net.dv8tion.jda.player.source.AudioStream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javafx.util.Pair;
import net.dv8tion.jda.player.AbstractMusicPlayer;

public abstract class ClumpedAbstractMusicPlayer extends AbstractMusicPlayer
{
    protected ClumpedQueue<String,AudioSource> audioQueue = new ClumpedQueue<>(2);
    protected String previousRequestor = null;
    protected String currentRequestor = null;
    protected ArrayList<String> votedSkips = new ArrayList<>();

    @Override
    public ClumpedQueue<String,AudioSource> getAudioQueue()
    {
        return audioQueue;
    }
    
    public void newQueue()
    {
        audioQueue.kill();
        audioQueue = new ClumpedQueue<>(2);
    }
    
    public String getCurrentRequestor()
    {
        return currentRequestor;
    }

    public String getPreviousRequestor()
    {
        return previousRequestor;
    }

    public List<String> getCurrentSkips()
    {
        return votedSkips;
    }
    
    // ========= Internal Functions ==========

    @Override
    protected void play0(boolean fireEvent)
    {
        if (state == State.PLAYING)
            return;

        if (currentAudioSource != null)
        {
            state = State.PLAYING;
            return;
        }

        if (audioQueue.isEmpty())
            throw new IllegalStateException("MusicPlayer: The audio queue is empty! Cannot start playing.");

        loadFromSource(audioQueue.removeFirst());
        state = State.PLAYING;

        if (fireEvent)
            eventManager.handle(new PlayEvent(this));
    }

    @Override
    protected void stop0(boolean fireEvent)
    {
        if (state == State.STOPPED)
            return;

        state = State.STOPPED;
        try
        {
            currentAudioStream.close();
        }
        catch (IOException e)
        {
            LOG.log(e);
        }
        finally
        {
            previousAudioSource = currentAudioSource;
            previousRequestor = currentRequestor;
            currentAudioSource = null;
            currentAudioStream = null;
            votedSkips.clear();
        }

        if (fireEvent)
            eventManager.handle(new StopEvent(this));
    }

    @Override
    protected void reload0(boolean autoPlay, boolean fireEvent)
    {
        if (previousAudioSource == null && currentAudioSource == null)
            throw new IllegalStateException("Cannot restart or reload a player that has never been started!");

        stop0(false);
        loadFromSource(new Pair<>(previousRequestor,previousAudioSource));

        if (autoPlay)
            play0(false);
        if (fireEvent)
            eventManager.handle(new ReloadEvent(this));
    }

    @Override
    protected void playNext(boolean fireEvent)
    {
        stop0(false);
        if (audioQueue.isEmpty())
        {
            if (fireEvent)
                eventManager.handle(new FinishEvent(this));
            return;
        }

        Pair<String,AudioSource> source;
        if (shuffle)
        {
            Random rand = new Random();
            source = audioQueue.remove(rand.nextInt(audioQueue.size()));
        }
        else
            source = audioQueue.removeFirst();
        loadFromSource(source);

        play0(false);
        if (fireEvent)
            eventManager.handle(new NextEvent(this));
    }

    protected void loadFromSource(Pair<String,AudioSource> source)
    {
        AudioStream stream = source.getValue().asStream();
        currentAudioSource = source.getValue();
        currentAudioStream = stream;
        currentRequestor = source.getKey();
        votedSkips.clear();
    }
}
