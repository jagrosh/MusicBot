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
     * @param args timestamp formatted as: [+ | -] &lt;HH:MM:SS | MM:SS | SS&gt;
     * @return Time in milliseconds, negative if seeking backwards relatively
     */
    public static SeekTime parseTime(String args)
    {
        if (args.length() == 0 || args.length() > 8) return null;
        String timestamp = args;
        boolean relative = false; // seek forward or backward
        boolean isSeekingBackwards = false;
        char first = timestamp.charAt(0);
        if (first == '+' || first == '-')
        {
            relative = true;
            isSeekingBackwards = first == '-';
            timestamp = timestamp.substring(1);
        }

        String[] timestampSplitArray = timestamp.split(":");
        int unitTotal = timestampSplitArray.length;
        if (unitTotal > 3) return null;

        int seconds;
        int minutes;
        int hours;
        int[] timeUnitArray = new int[3]; // Hours, minutes, seconds
        int timeUnitIndex = 3 - unitTotal;

        for (String timeUnit : timestampSplitArray)
        {
            if (timeUnit.length() > 2 || timeUnit.length() == 0) return null;
            try
            {
                if (timeUnit.substring(0, 1).equals("+")) return null;
                timeUnitArray[timeUnitIndex] = Integer.parseUnsignedInt(timeUnit);
                timeUnitIndex++;
            } catch (NumberFormatException e)
            {
                return null;
            }
        }

        hours = timeUnitArray[0];
        minutes = timeUnitArray[1];
        seconds = timeUnitArray[2];

        long milliseconds = hours * 3600000 + minutes * 60000 + seconds * 1000;
        if (relative && isSeekingBackwards) milliseconds = -milliseconds;

        return new SeekTime(milliseconds, relative);
    }


    public static class SeekTime
    {
        public final long milliseconds;
        public final boolean relative;

        public SeekTime(long milliseconds, boolean relative)
        {
            this.milliseconds = milliseconds;
            this.relative = relative;
        }
    }
}
