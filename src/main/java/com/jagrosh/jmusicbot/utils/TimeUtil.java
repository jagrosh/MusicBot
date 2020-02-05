/*
 * Copyright 2020 John Grosh <john.a.grosh@gmail.com>.
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
package com.jagrosh.jmusicbot.utils;

import java.util.regex.Pattern;

public class TimeUtil
{

    public static String formatTime(long duration)
    {
        if(duration == Long.MAX_VALUE)
            return "LIVE";
        long seconds = Math.round(duration/1000.0);
        long hours = seconds/(60*60);
        seconds %= 60*60;
        long minutes = seconds/60;
        seconds %= 60;
        return (hours>0 ? hours+":" : "") + (minutes<10 ? "0"+minutes : minutes) + ":" + (seconds<10 ? "0"+seconds : seconds);
    }

    /**
     *
     * @return Time in milliseconds
     */
    public static SeekTime parseTime(String args)
    {
        Boolean seek_relative = null; // seek forward or backward
        char charRelative = args.charAt(0);
        if (args.charAt(0) == '+' || args.charAt(0) == '-')
        {
            args = args.substring(1);
            seek_relative = charRelative == '+';

        }

        long seconds;
        long minutes = 0;
        long hours = 0;
        if (Pattern.matches("^(\\d\\d):([0-5]\\d):([0-5]\\d)$", args))
        {
            hours = Integer.parseInt(args.substring(0, 2));
            minutes = Integer.parseInt(args.substring(3, 5));
            seconds = Integer.parseInt(args.substring(6));
        }
        else if (Pattern.matches("^([0-5]\\d):([0-5]\\d)$", args))
        {
            minutes = Integer.parseInt(args.substring(0, 2));
            seconds = Integer.parseInt(args.substring(3, 5));
        }
        else if (Pattern.matches("^([0-5]\\d)$", args))
        {
            seconds = Integer.parseInt(args.substring(0, 2));
        }
        else return null;


        return new SeekTime(seek_relative, hours * 3600000 + minutes * 60000 + seconds * 1000);
    }


    public static class SeekTime
    {
        public final long milliseconds;
        public final Boolean relative;

        public SeekTime(Boolean relative, long milliseconds)
        {
            this.relative = relative;
            this.milliseconds = milliseconds;
        }
    }
}
