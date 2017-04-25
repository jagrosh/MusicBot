/*
 * Copyright 2016 John Grosh <john.a.grosh@gmail.com>.
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
package com.jagrosh.jmusicbot.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;

/**
 *
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class FinderUtil {
    
    public static List<TextChannel> findTextChannel(String query, Guild guild)
    {
        String id;
        if(query.matches("<#\\d+>"))
        {
            id = query.replaceAll("<#(\\d+)>", "$1");
            TextChannel tc = guild.getJDA().getTextChannelById(id);
            if(tc!=null && tc.getGuild().equals(guild))
                return Collections.singletonList(tc);
        }
        ArrayList<TextChannel> exact = new ArrayList<>();
        ArrayList<TextChannel> wrongcase = new ArrayList<>();
        ArrayList<TextChannel> startswith = new ArrayList<>();
        ArrayList<TextChannel> contains = new ArrayList<>();
        String lowerquery = query.toLowerCase();
        guild.getTextChannels().stream().forEach((tc) -> {
            if(tc.getName().equals(lowerquery))
                exact.add(tc);
            else if (tc.getName().equalsIgnoreCase(lowerquery) && exact.isEmpty())
                wrongcase.add(tc);
            else if (tc.getName().toLowerCase().startsWith(lowerquery) && wrongcase.isEmpty())
                startswith.add(tc);
            else if (tc.getName().toLowerCase().contains(lowerquery) && startswith.isEmpty())
                contains.add(tc);
        });
        if(!exact.isEmpty())
            return exact;
        if(!wrongcase.isEmpty())
            return wrongcase;
        if(!startswith.isEmpty())
            return startswith;
        return contains;
    }
    
    public static List<VoiceChannel> findVoiceChannel(String query, Guild guild)
    {
        String id;
        if(query.matches("<#\\d+>"))
        {
            id = query.replaceAll("<#(\\d+)>", "$1");
            VoiceChannel tc = guild.getJDA().getVoiceChannelById(id);
            if(tc!=null && tc.getGuild().equals(guild))
                return Collections.singletonList(tc);
        }
        ArrayList<VoiceChannel> exact = new ArrayList<>();
        ArrayList<VoiceChannel> wrongcase = new ArrayList<>();
        ArrayList<VoiceChannel> startswith = new ArrayList<>();
        ArrayList<VoiceChannel> contains = new ArrayList<>();
        String lowerquery = query.toLowerCase();
        guild.getVoiceChannels().stream().forEach((tc) -> {
            if(tc.getName().equals(lowerquery))
                exact.add(tc);
            else if (tc.getName().equalsIgnoreCase(lowerquery) && exact.isEmpty())
                wrongcase.add(tc);
            else if (tc.getName().toLowerCase().startsWith(lowerquery) && wrongcase.isEmpty())
                startswith.add(tc);
            else if (tc.getName().toLowerCase().contains(lowerquery) && startswith.isEmpty())
                contains.add(tc);
        });
        if(!exact.isEmpty())
            return exact;
        if(!wrongcase.isEmpty())
            return wrongcase;
        if(!startswith.isEmpty())
            return startswith;
        return contains;
    }
    
    public static List<Role> findRole(String query, Guild guild)
    {
        String id;
        if(query.matches("<@&\\d+>"))
        {
            id = query.replaceAll("<@&(\\d+)>", "$1");
            Role role = guild.getRoleById(id);
            if(role!=null)
                return Collections.singletonList(role);
        }
        if(query.matches("[Ii][Dd]\\s*:\\s*\\d+"))
        {
            id = query.replaceAll("[Ii][Dd]\\s*:\\s*(\\d+)", "$1");
            for(Role role: guild.getRoles())
                if(role.getId().equals(id))
                    return Collections.singletonList(role);
        }
        ArrayList<Role> exact = new ArrayList<>();
        ArrayList<Role> wrongcase = new ArrayList<>();
        ArrayList<Role> startswith = new ArrayList<>();
        ArrayList<Role> contains = new ArrayList<>();
        String lowerQuery = query.toLowerCase();
        guild.getRoles().stream().forEach((role) -> {
            if(role.getName().equals(query))
                exact.add(role);
            else if (role.getName().equalsIgnoreCase(query) && exact.isEmpty())
                wrongcase.add(role);
            else if (role.getName().toLowerCase().startsWith(lowerQuery) && wrongcase.isEmpty())
                startswith.add(role);
            else if (role.getName().toLowerCase().contains(lowerQuery) && startswith.isEmpty())
                contains.add(role);
        });
        if(!exact.isEmpty())
            return exact;
        if(!wrongcase.isEmpty())
            return wrongcase;
        if(!startswith.isEmpty())
            return startswith;
        return contains;
    }
}
