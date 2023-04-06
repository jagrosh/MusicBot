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
package com.jagrosh.jmusicbot.queue;

/**
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public interface Queueable {
    /**
     * Deeper into the JDA, the user who added Queueable to Queue is referred to as the owner. From here on in we refer
     * to this user as the caller, as the term owner is reserved for user who owns and manages the bot.
     *
     * @return ID of user who added Queueable to Queue.
     */
    long getCallerIdentifier();
}
