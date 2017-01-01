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

import java.io.IOException;
import java.util.Arrays;

import net.dv8tion.jda.audio.AudioConnection;
import net.dv8tion.jda.audio.AudioSendHandler;

public class ClumpedMusicPlayer extends ClumpedAbstractMusicPlayer implements AudioSendHandler
{

    public static final int PCM_FRAME_SIZE = 4;
    private byte[] buffer = new byte[AudioConnection.OPUS_FRAME_SIZE * PCM_FRAME_SIZE];

    @Override
    public boolean canProvide()
    {
        return state.equals(State.PLAYING);
    }

    @Override
    public byte[] provide20MsAudio()
    {
//        if (currentAudioStream == null || audioFormat == null)
//            throw new IllegalStateException("The Audio source was never set for this player!\n" +
//                    "Please provide an AudioInputStream using setAudioSource.");
        try
        {
            int amountRead = currentAudioStream.read(buffer, 0, buffer.length);
            if (amountRead > -1)
            {
                if (amountRead < buffer.length) {
                    Arrays.fill(buffer, amountRead, buffer.length - 1, (byte) 0);
                }
                if (volume != 1) {
                    short sample;
                    for (int i = 0; i < buffer.length; i+=2) {
                        sample = (short)((buffer[ i+ 1] & 0xff) | (buffer[i] << 8));
                        sample = (short) (sample * volume);
                        buffer[i + 1] = (byte)(sample & 0xff);
                        buffer[i] = (byte)((sample >> 8) & 0xff);
                    }
                }
                return buffer;
            }
            else
            {
                sourceFinished();
                return null;
            }
        }
        catch (IOException e)
        {
            if(this.getCurrentTimestamp().getMilliseconds()>0)
            {
                LOG.warn("A source closed unexpectantly? Oh well I guess...");
                sourceFinished();
            }
        }
        return null;
    }
}