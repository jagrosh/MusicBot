/*
 * Copyright 2023 まったりにほんご
 * 
 * Copyright 2020 John Grosh <john.a.grosh@gmail.com>.
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
package com.jagrosh.jmusicbot.settings;

/**
 *
 * @author Michaili K
 */
public enum RepeatMode
{
    OFF(null, "オフ"),
    ALL("\uD83D\uDD01", "すべて"), // 🔁
    SINGLE("\uD83D\uDD02", "一曲だけ"); // 🔂

    private final String emoji;
    private final String userFriendlyName;

    private RepeatMode(String emoji, String userFriendlyName)
    {
        this.emoji = emoji;
        this.userFriendlyName = userFriendlyName;
    }

    public String getEmoji()
    {
        return emoji;
    }

    public String getUserFriendlyName()
    {
        return userFriendlyName;
    }
}
