package com.jagrosh.jmusicbot.queue;

import com.jagrosh.jmusicbot.settings.QueueType;

import java.util.ArrayList;
import java.util.List;

public abstract class Queue<T extends Queueable> {
    final List<T> list = new ArrayList<>();

    public Queue() {
    }

    public Queue(List<T> tList) {
        tList.forEach(this::add);
    }

    public int add(T item) {
        this.list.add(item);
        return this.list.size() - 1;
    }

    public void addAt(int index, T item) {
        if (index >= this.list.size()) {
            this.list.add(item);
        } else {
            this.list.add(index, item);
        }
    }

    /**
     * @return number of Queueables in Queue.
     */
    public int size() {
        return this.list.size();
    }

    /**
     * Remove first Queueable and return it.
     *
     * @return next item in Queue.
     */
    public T pop() {
        return this.list.remove(0);
    }

    /**
     * @return true when no items in Queue, otherwise false.
     */
    public boolean isEmpty() {
        return this.list.isEmpty();
    }

    /**
     * Returns entire Queue object.
     *
     * @return entire Queue object.
     */
    public List<T> getList() {
        return this.list;
    }

    /**
     * Returns Queueable at index in Queue.
     *
     * @param index index of Queueable to be retrieved.
     * @return Queueable at index.
     */
    public T get(int index) {
        return this.list.get(index);
    }

    /**
     * Queueable at index is removed form queue.
     *
     * @param index the index in Queue of the Queueable to be removed.
     */
    public void remove(int index) {
        this.list.remove(index);
    }

    /**
     * Remove all items queued by caller.
     *
     * @param identifier ID of caller.
     * @return number of Queueables removed.
     */
    public int removeAll(long identifier) {
        int count = 0;
        for (int i = this.list.size() - 1; i >= 0; i--) {
            if (this.list.get(i).getCallerIdentifier() == identifier) {
                this.list.remove(i);
                count++;
            }
        }
        return count;
    }

    /**
     * This command is used by the handler when stopped, shut down or found alone in channel.
     */
    public void clear() {
        this.list.clear();
    }

    /**
     * Shuffle queueables belonging to caller.
     *
     * @param identifier ID of calling user.
     * @return number of shuffled Queueables.
     */
    public int shuffle(long identifier) {
        ArrayList<Integer> indexSet = new ArrayList<>();
        for (int i = 0; i < this.list.size(); i++) {
            if (this.list.get(i).getCallerIdentifier() == identifier) indexSet.add(i);
        }

        for (int j = 0; j < indexSet.size(); j++) {
            int originalPosition = indexSet.get(j);
            int newRandomPosition = indexSet.get((int) (Math.random() * indexSet.size()));
            T temp = this.list.get(originalPosition);
            this.list.set(originalPosition, this.list.get(newRandomPosition));
            this.list.set(newRandomPosition, temp);
        }
        return indexSet.size();
    }

    public void skip(int number) {
        if (number > 0) {
            this.list.subList(0, number).clear();
        }
    }

    /**
     * Move an item to a different position in the list
     *
     * @param from The position of the item
     * @param to   The new position of the item
     * @return the moved item
     */
    public T moveItem(int from, int to) {
        T item = this.list.remove(from);
        this.list.add(to, item);
        return item;
    }

    public abstract QueueType getQueueType();
}
