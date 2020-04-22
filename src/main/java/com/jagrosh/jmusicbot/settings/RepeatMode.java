package com.jagrosh.jmusicbot.settings;

public enum RepeatMode {
    off, single, all;

    static RepeatMode fromString(String string) {
        if (string.equalsIgnoreCase(RepeatMode.single.toString()))
            return RepeatMode.single;
        if (string.equalsIgnoreCase(RepeatMode.all.toString()))
            return RepeatMode.all;
        return RepeatMode.off;
    }

    static RepeatMode fromObject(Object object) {
        // Check against previous boolean setting and default to the same behavior as "true" if set
        if (object instanceof Boolean && (Boolean) object)
            return RepeatMode.all;
        // New setting, uses String
        if (object instanceof String)
            return fromString((String) object);
        return RepeatMode.off;
    }
}
