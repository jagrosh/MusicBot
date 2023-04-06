package com.jagrosh.jmusicbot.queue;

import com.jagrosh.jmusicbot.settings.QueueType;

import java.util.List;

public class SimpleQueue<T extends Queueable> extends Queue<T> {

    public SimpleQueue() {
    }

    public SimpleQueue(List<T> tList) {
        super(tList);
    }

    @Override
    public QueueType getQueueType() {
        return QueueType.SIMPLE_QUEUE;
    }
}
