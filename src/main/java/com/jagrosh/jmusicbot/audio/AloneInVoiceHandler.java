/*
 * Copyright 2021 John Grosh <john.a.grosh@gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jagrosh.jmusicbot.audio;

import com.jagrosh.jmusicbot.Bot;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;

import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Michaili K (mysteriouscursor+git@protonmail.com)
 */
public class AloneInVoiceHandler
{
    private final Bot bot;
    private final HashMap<Long, Instant> aloneSince = new HashMap<>();
    private long aloneTimeUntilStop = 0;

    public AloneInVoiceHandler(Bot bot)
    {
        this.bot = bot;
    }
    
    public void init()
    {
        aloneTimeUntilStop = bot.getConfig().getAloneTimeUntilStop();
        if(aloneTimeUntilStop > 0)
            bot.getThreadpool().scheduleWithFixedDelay(() -> check(), 0, 5, TimeUnit.SECONDS);
    }
    
    private void check()
    {
        Set<Long> toRemove = new HashSet<>();
        for(Map.Entry<Long, Instant> entrySet: aloneSince.entrySet())
        {
            if(entrySet.getValue().getEpochSecond() > Instant.now().getEpochSecond() - aloneTimeUntilStop) continue;

            Guild guild = bot.getJDA().getGuildById(entrySet.getKey());

            if(guild == null)
            {
                toRemove.add(entrySet.getKey());
                continue;
            }

            ((AudioHandler) guild.getAudioManager().getSendingHandler()).stopAndClear();
            guild.getAudioManager().closeAudioConnection();

            toRemove.add(entrySet.getKey());
        }
        toRemove.forEach(id -> aloneSince.remove(id));
    }

    public void onVoiceUpdate(GuildVoiceUpdateEvent event)
    {
        if(aloneTimeUntilStop <= 0) return;

        Guild guild = event.getEntity().getGuild();
        if(!bot.getPlayerManager().hasHandler(guild)) return;

        boolean alone = isAlone(guild);
        boolean inList = aloneSince.containsKey(guild.getIdLong());

        if(!alone && inList)
            aloneSince.remove(guild.getIdLong());
        else if(alone && !inList)
            aloneSince.put(guild.getIdLong(), Instant.now());
    }

    private boolean isAlone(Guild guild)
    {
        if(guild.getAudioManager().getConnectedChannel() == null) return false;
        return guild.getAudioManager().getConnectedChannel().getMembers().stream()
                .noneMatch(x ->
                        !x.getVoiceState().isDeafened()
                        && !x.getUser().isBot());
    }
}
