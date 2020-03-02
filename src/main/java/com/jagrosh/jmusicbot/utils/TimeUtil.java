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
        if (args.length() == 0) return null;
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

        String[] timestampSplitArray = timestamp.split(":+");
        if(timestampSplitArray.length > 3 )
            return null;
        double[] timeUnitArray = new double[3]; // hours, minutes, seconds
        for(int index = 0; index < timestampSplitArray.length; index++)
        {
            String unit = timestampSplitArray[index];
            if (unit.startsWith("+")) return null;
            unit = unit.replace(",", ".");
            try
            {
                timeUnitArray[index + 3 - timestampSplitArray.length] = Double.parseDouble(unit);
            }
            catch (NumberFormatException e)
            {
                return null;
            }
        }
        long milliseconds = Math.round(timeUnitArray[0] * 3600000 + timeUnitArray[1] * 60000 + timeUnitArray[2] * 1000);
        milliseconds *= isSeekingBackwards ? -1 : 1;

        return new SeekTime(milliseconds, relative);
    }


    public static class SeekTime
    {
        public final long milliseconds;
        public final boolean relative;

        private SeekTime(long milliseconds, boolean relative)
        {
            this.milliseconds = milliseconds;
            this.relative = relative;
        }
    }
}
