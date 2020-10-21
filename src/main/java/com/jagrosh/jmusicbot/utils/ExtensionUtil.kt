package com.jagrosh.jmusicbot.utils

import com.jagrosh.jdautilities.command.CommandEvent

fun CommandEvent.queueMessageToChannel(message: String) {
    channel.sendMessage(message).queue()
}