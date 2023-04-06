package com.jagrosh.jmusicbot.settings;

import java.util.Arrays;
import java.util.Optional;

public enum QueueType {
    SIMPLE_QUEUE("simplequeue", "Simple Queue"),
    FAIR_QUEUE("fairqueue", "Fair Queue");

    private final String name;
    private final String friendlyName;

    QueueType(String name, String friendlyName) {
        this.name = name;
        this.friendlyName = friendlyName;
    }

    public static QueueType getFromParam(String name) {
        Optional<QueueType> queueTypeOptional = Arrays.stream(QueueType.values()).filter(queueType -> queueType.name.equals(name)).findFirst();
        if (queueTypeOptional.isPresent()) {
            return queueTypeOptional.get();
        } else {
            throw new IllegalArgumentException(String.format("Queue type with name \"%s\" does not exits.", name));
        }
    }

    public String getFriendlyName() {
        return friendlyName;
    }
}
