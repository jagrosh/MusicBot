/*
 * Copyright 2022 John Grosh (jagrosh).
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
 *
 * @author Wolfgang Schwendtbauer
 * @param <T>
 */
public class LinearQueue<T extends Queueable> extends AbstractQueue<T>
{
    public LinearQueue(AbstractQueue<T> queue)
    {
        super(queue);
    }

    @Override
    public int add(T item)
    {
        list.add(item);
        return list.size() - 1;
    }

}
