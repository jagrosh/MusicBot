package com.jagrosh.jmusicbot.utils;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.concurrent.TimeUnit;

public class SplitPixlMiscUtil {
    public static void theVoidChannel(MessageReceivedEvent event) {
        if (event.getChannel().getIdLong() == 806453583511420949L) {
            event.getMessage().delete().queueAfter(15, TimeUnit.MINUTES, unused -> {}, unused -> {});
        }
    }
}
