package com.jagrosh.jmusicbot.commands.fun;

import com.jagrosh.jmusicbot.commands.FunCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class BaseCatCmd extends FunCommand {
    public static final int QUIET_MILLIS = 5000;

    protected Map<String, Long> lastExecutionMillisByChannelMap = new LinkedHashMap<>();

    Logger startupLog = LoggerFactory.getLogger("Startup");
}
