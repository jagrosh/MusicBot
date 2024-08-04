/*
 *
 */
package com.jagrosh.jmusicbot.queue;
/*panja testing checking */
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Wolfgang Schwendtbauer
 * @param <T>
 */
public abstract class AbstractQueue<T extends Queueable>
{
    protected AbstractQueue(AbstractQueue<T> queue)
    {
        this.list = queue != null ? queue.getList() : new LinkedList<>();
    }
/*
* Author:- Neeraja
* dob:-01/01/01
* */
    protected final List<T> list;

    public abstract int add(T item);

    public void addAt(int index, T item)
    {
        if(index >= list.size())
            list.add(item);
        else
            list.add(index, item);
    }

    public int size() {
        return list.size();
    }

    public T pull() {
        return list.remove(0);
    }

    public boolean isEmpty()
    {
        return list.isEmpty();
    }

    public List<T> getList()
    {
        return list;
    }

    public T get(int index) {
        return list.get(index);
    }

    public T remove(int index)
    {
        return list.remove(index);
    }

    public int removeAll(long identifier)
    {
        int count = 0;
        for(int i=list.size()-1; i>=0; i--)
        {
            if(list.get(i).getIdentifier()==identifier)
            {
                list.remove(i);
                count++;
            }
        }
        return count;
    }

    public void clear()
    {
        list.clear();
    }

    public int shuffle(long identifier)
    {
        List<Integer> iset = new ArrayList<>();
        for(int i=0; i<list.size(); i++)
        {
            if(list.get(i).getIdentifier()==identifier)
                iset.add(i);
        }
        for(int j=0; j<iset.size(); j++)
        {
            int first = iset.get(j);
            int second = iset.get((int)(Math.random()*iset.size()));
            T temp = list.get(first);
            list.set(first, list.get(second));
            list.set(second, temp);
        }
        return iset.size();
    }

    public void skip(int number)
    {
        if (number > 0) {
            list.subList(0, number).clear();
        }
    }

    /**
     * Move an item to a different position in the list
     * @param from The position of the item
     * @param to The new position of the item
     * @return the moved item
     */
    public T moveItem(int from, int to)
    {
        T item = list.remove(from);
        list.add(to, item);
        return item;
    }
}
