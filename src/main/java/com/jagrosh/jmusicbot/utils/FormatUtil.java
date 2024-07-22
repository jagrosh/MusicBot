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

import com.jagrosh.jmusicbot.audio.RequestMetadata.UserInfo;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;

import java.util.List;

/**
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class FormatUtil {
    private FormatUtil() {}

    public static String formatUsername(String username, String discrim) {
        if(discrim == null || discrim.equals("0000")) {
            return username;
        }
        else {
            return username + "#" + discrim;
        }
    }

    public static String formatUsername(UserInfo userinfo) {
        return formatUsername(userinfo.username, userinfo.discrim);
    }

    public static String formatUsername(User user) {
        return formatUsername(user.getName(), user.getDiscriminator());
    }

    public static String progressBar(double percent) {
        StringBuilder str = new StringBuilder();
        for(int i = 0; i < 12; i++) {
            if(i == (int) (percent * 12)) {
                str.append("\uD83D\uDD18"); // 🔘
            }
            else {
                str.append("▬");
            }
        }
        return str.toString();
    }

    public static String volumeIcon(int volume) {
        if(volume == 0) {
            return "\uD83D\uDD07"; // 🔇
        }
        if(volume < 30) {
            return "\uD83D\uDD08"; // 🔈
        }
        if(volume < 70) {
            return "\uD83D\uDD09"; // 🔉
        }
        return "\uD83D\uDD0A";     // 🔊
    }

    public static String listOfTChannels(List<TextChannel> list, String query) {
        StringBuilder out = new StringBuilder(" Multiple text channels found matching \"" + query + "\":");
        for(int i = 0; i < 6 && i < list.size(); i++) {
            out.append("\n - ")
                .append(list.get(i).getName())
                .append(" (<#")
                .append(list.get(i).getId())
                .append(">)");
        }
        if(list.size() > 6) {
            out.append("\n**And ").append(list.size() - 6).append(" more...**");
        }
        return out.toString();
    }

    public static String listOfVChannels(List<VoiceChannel> list, String query) {
        StringBuilder out = new StringBuilder(" Multiple voice channels found matching \"" + query + "\":");
        for(int i = 0; i < 6 && i < list.size(); i++) {
            out.append("\n - ")
                .append(list.get(i).getAsMention())
                .append(" (ID:")
                .append(list.get(i).getId())
                .append(")");
        }
        if(list.size() > 6) {
            out.append("\n**And ").append(list.size() - 6).append(" more...**");
        }
        return out.toString();
    }

    public static String listOfRoles(List<Role> list, String query) {
        StringBuilder out = new StringBuilder(" Multiple text channels found matching \"" + query + "\":");
        for(int i = 0; i < 6 && i < list.size(); i++) {
            out.append("\n - ").append(list.get(i).getName()).append(" (ID:").append(list.get(i).getId()).append(")");
        }
        if(list.size() > 6) {
            out.append("\n**And ").append(list.size() - 6).append(" more...**");
        }
        return out.toString();
    }

    public static String filter(String input) {
        return input.replace("\u202E", "")
            .replace("@everyone", "@\u0435veryone") // cyrillic letter e
            .replace("@here", "@h\u0435re") // cyrillic letter e
            .trim();
    }
}
