/*
 * Copyright 2016 John Grosh (jagrosh).
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

import com.jagrosh.jmusicbot.settings.QueueType;

import java.util.HashSet;
import java.util.List;

/**
 *
 * @author John Grosh (jagrosh)
 * @param <T>
 */
public class FairQueue<T extends Queueable> extends Queue<T>
{
    public FairQueue()
    {
    }

    public FairQueue(List<T> tList)
    {
        super(tList);
    }

    @Override
    public int add(T item)
    {
        // Get index of Queueable furthest in queue from same owner
        int lastIndex;
        for (lastIndex = super.list.size() - 1; lastIndex > -1; lastIndex--)
        {
            if (super.list.get(lastIndex).getCallerIdentifier() == item.getCallerIdentifier())
            {
                break;
            }
        }

        // Set working index to be one after last Queueable from same owner
        lastIndex++;

        // Skip everybody else once
        HashSet<Long> callers = new HashSet<>();
        for (; lastIndex < super.list.size(); lastIndex++)
        {
            if (callers.contains(super.list.get(lastIndex).getCallerIdentifier()))
            {
                break;
            }
            callers.add(super.list.get(lastIndex).getCallerIdentifier());
        }

        super.list.add(lastIndex, item);
        return lastIndex;
    }

    @Override
    public QueueType getQueueType()
    {
        return QueueType.FAIR_QUEUE;
    }
}
