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

import net.dv8tion.jda.api.entities.User;

/**
 * @author John Grosh (john.a.grosh@gmail.com)
 */
public class RequestMetadata {
    public static final RequestMetadata EMPTY = new RequestMetadata(null);

    public final UserInfo user;

    public RequestMetadata(User user) {
        this.user = user == null ? null : new UserInfo(user.getIdLong(), user.getName(), user.getDiscriminator(), user.getEffectiveAvatarUrl());
    }

    public long getOwner() {
        return user == null ? 0L : user.id;
    }

    public static class UserInfo {
        public final long id;
        public final String username, discriminator, avatar;

        private UserInfo(long id, String username, String discriminator, String avatar) {
            this.id = id;
            this.username = username;
            this.discriminator = discriminator;
            this.avatar = avatar;
        }
    }
}
