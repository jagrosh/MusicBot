
/*
 * Copyright 2016 jagrosh.
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
package spectramusic.entities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import javafx.util.Pair;

/**
 *
 * @author John Grosh (jagrosh)
 * 
 * The ClumpedQueue holds Objects in an order based on the order of entry and 4
 * also on the associated "ID" item. Each clump can only contain a predefined
 * number of objects from a certain ID. When this threshold is reached, all
 * items from that ID are placed in the next clump.
 */
public class ClumpedQueue<X,Y> extends LinkedList {
    private final int clumpSize;
    private final ArrayList<Clump> clumps;
    
    public ClumpedQueue(int clumpSize)
    {
        this.clumpSize = clumpSize;
        clumps = new ArrayList<>();
    }
    
    /*
        Adds an item to the first available clump, creating a new clump
        if necessary
    
        @returns the index of the new addition
    */
    public int add(X key, Y value)
    {
        int size = 0;
        for(Clump clump : clumps)
        {
            size+=clump.size();
            if(clump.add(key, value))
                return size;
        }
        Clump newClump = new Clump();
        newClump.add(key, value);
        clumps.add(newClump);
        return size;
    }
    
    public void addAll(X key, Collection<Y> values)
    {
        values.stream().forEach(value -> add(key, value));
    }

    @Override
    public Pair<X,Y> removeFirst()
    {
        if(clumps.isEmpty())
            return null;
        Pair<X,Y> toReturn = clumps.get(0).removeFirst();
        if(clumps.get(0).isEmpty())
            clumps.remove(0);
        return toReturn;
    }

    @Override
    public Pair<X,Y> remove(int index)
    {
        if(clumps.isEmpty() || index>=size())
            return null;
        Pair<X,Y> toReturn = null;
        Clump foundClump = null;
        for(Clump clump : clumps)
            if(clump.size()<=index)
                index-=clump.size();
            else
            {
                toReturn = clump.remove(index);
                foundClump = clump;
            }
        if(foundClump!=null && foundClump.isEmpty())
            clumps.remove(foundClump);
        return toReturn;
    }

    @Override
    public int size()
    {
        int sum = 0;
        return clumps.stream().map((clump) -> clump.size()).reduce(sum, Integer::sum);
    }

    @Override
    public boolean isEmpty()
    {
        return clumps.isEmpty();
    }

    @Override
    public Pair<X,Y> get(int index)
    {
        if(index<0 || index>=size())
            return null;
        for(Clump clump : clumps)
            if(clump.size()<=index)
                index-=clump.size();
            else
                return clump.get(index);
        return null;
    }
    
    @Override
    public void clear()
    {
        clumps.clear();
    }
    
    private class Clump {
        ArrayList<Pair<X,Y>> items = new ArrayList<>();
        HashMap<X,Integer> count = new HashMap<>();
        
        public boolean add(X key, Y value)
        {
            if(count.getOrDefault(key, 0)<clumpSize)
            {
                count.put(key, count.getOrDefault(key, 0)+1);
                items.add(new Pair<>(key,value));
                return true;
            }
            return false;
        }
        
        public Pair<X,Y> removeFirst()
        {
            return items.isEmpty() ? null : items.remove(0);
        }
        
        public Pair<X,Y> remove(int index)
        {
            return items.remove(index);
        }
        
        public boolean isEmpty()
        {
            return items.isEmpty();
        }
        
        public int size()
        {
            return items.size();
        }
        
        public Pair<X,Y> get(int index)
        {
            return items.get(index);
        }
    }
}
