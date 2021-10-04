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
 *
 * @author John Grosh (john.a.grosh@gmail.com)
 */
public class RequestMetadata
{
    public static final RequestMetadata EMPTY = new RequestMetadata(null);
    
    public final UserInfo user;
    
    public RequestMetadata(User user)
    {
        this.user = user == null ? null : new UserInfo(user.getIdLong(), user.getName(), user.getDiscriminator(), user.getEffectiveAvatarUrl());
    }
    
    public long getOwner()
    {
        return user == null ? 0L : user.id;
    }
    
    public class RequestInfo
    {
        public final String query, url;
        
        private RequestInfo(String query, String url)
        {
            this.query = query;
            this.url = url;
        }
    }
    
    public class UserInfo
    {
        public final long id;
        public final String username, discrim, avatar;
        
        private UserInfo(long id, String username, String discrim, String avatar)
        {
            this.id = id;
            this.username = username;
            this.discrim = discrim;
            this.avatar = avatar;
        }
    }
}
