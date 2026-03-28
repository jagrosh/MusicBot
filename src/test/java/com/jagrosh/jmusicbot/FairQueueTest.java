/*
 * Copyright 2018 John Grosh <john.a.grosh@gmail.com>.
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
package com.jagrosh.jmusicbot;

import com.jagrosh.jmusicbot.queue.FairQueue;
import com.jagrosh.jmusicbot.queue.LinearQueue;
import com.jagrosh.jmusicbot.queue.Queueable;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author John Grosh (john.a.grosh@gmail.com)
 */
public class FairQueueTest
{
    @Test
    public void differentIdentifierSize()
    {
        FairQueue<Q> queue = new FairQueue<>(null);
        int size = 100;
        for(int i=0; i<size; i++)
            queue.add(new Q(i));
        assertEquals(queue.size(), size);
    }

    @Test
    public void sameIdentifierSize()
    {
        FairQueue<Q> queue = new FairQueue<>(null);
        int size = 100;
        for(int i=0; i<size; i++)
            queue.add(new Q(0));
        assertEquals(queue.size(), size);
    }

    @Test
    public void emptyQueueIsEmpty()
    {
        FairQueue<Q> queue = new FairQueue<>(null);
        assertTrue(queue.isEmpty());
        assertEquals(0, queue.size());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void emptyQueuePullThrows()
    {
        FairQueue<Q> queue = new FairQueue<>(null);
        queue.pull();
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void emptyQueueGetThrows()
    {
        FairQueue<Q> queue = new FairQueue<>(null);
        queue.get(0);
    }

    @Test
    public void singleElementAddAndPull()
    {
        FairQueue<Q> queue = new FairQueue<>(null);
        Q item = new Q(1);
        queue.add(item);
        assertEquals(1, queue.size());
        Q pulled = queue.pull();
        assertEquals(item.getIdentifier(), pulled.getIdentifier());
        assertTrue(queue.isEmpty());
    }

    @Test
    public void sameUserOrderingAfterDifferentUser()
    {
        FairQueue<Q> queue = new FairQueue<>(null);
        Q differentUser = new Q(2);
        queue.add(differentUser);
        queue.add(new Q(1));
        queue.add(new Q(1));
        queue.add(new Q(1));
        assertEquals(4, queue.size());
        assertEquals(2L, queue.get(0).getIdentifier());
    }

    @Test
    public void differentUsersInterleaved()
    {
        FairQueue<Q> queue = new FairQueue<>(null);
        queue.add(new Q(1));
        queue.add(new Q(2));
        queue.add(new Q(1));
        queue.add(new Q(2));

        assertEquals(4, queue.size());
        assertEquals(1L, queue.get(0).getIdentifier());
        assertEquals(2L, queue.get(1).getIdentifier());
        assertEquals(1L, queue.get(2).getIdentifier());
        assertEquals(2L, queue.get(3).getIdentifier());
    }

    @Test
    public void sizeAfterAddAndRemove()
    {
        FairQueue<Q> queue = new FairQueue<>(null);
        int n = 10;
        int m = 4;
        for(int i=0; i<n; i++)
            queue.add(new Q(i));
        for(int i=0; i<m; i++)
            queue.remove(0);
        assertEquals(n - m, queue.size());
    }

    @Test
    public void linearQueuePreservesInsertionOrder()
    {
        LinearQueue<Q> linear = new LinearQueue<>(null);
        linear.add(new Q(1));
        linear.add(new Q(2));
        linear.add(new Q(1));
        linear.add(new Q(3));

        assertEquals(1L, linear.get(0).getIdentifier());
        assertEquals(2L, linear.get(1).getIdentifier());
        assertEquals(1L, linear.get(2).getIdentifier());
        assertEquals(3L, linear.get(3).getIdentifier());
    }

    @Test
    public void shuffleDoesNotLoseElements()
    {
        FairQueue<Q> queue = new FairQueue<>(null);
        int size = 20;
        for(int i=0; i<size; i++)
            queue.add(new Q(1));
        queue.shuffle(1);
        assertEquals(size, queue.size());
    }

    @Test
    public void removeAllForOneUser()
    {
        FairQueue<Q> queue = new FairQueue<>(null);
        queue.add(new Q(1));
        queue.add(new Q(2));
        queue.add(new Q(1));
        queue.add(new Q(2));
        int removed = queue.removeAll(1);
        assertEquals(2, removed);
        assertEquals(2, queue.size());
        for(int i=0; i<queue.size(); i++)
            assertEquals(2L, queue.get(i).getIdentifier());
    }

    @Test
    public void addAtCorrectIndex()
    {
        FairQueue<Q> queue = new FairQueue<>(null);
        queue.add(new Q(1));
        queue.add(new Q(2));
        queue.add(new Q(3));
        Q inserted = new Q(99);
        queue.addAt(1, inserted);
        assertEquals(4, queue.size());
        assertEquals(99L, queue.get(1).getIdentifier());
    }

    @Test
    public void getBoundaryIndices()
    {
        FairQueue<Q> queue = new FairQueue<>(null);
        queue.add(new Q(10));
        queue.add(new Q(20));
        queue.add(new Q(30));
        assertEquals(10L, queue.get(0).getIdentifier());
        assertEquals(30L, queue.get(queue.size() - 1).getIdentifier());
    }

    private class Q implements Queueable
    {
        private final long identifier;

        private Q(long identifier)
        {
            this.identifier = identifier;
        }

        @Override
        public long getIdentifier()
        {
            return identifier;
        }
    }
}
