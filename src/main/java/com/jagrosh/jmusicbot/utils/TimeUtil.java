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

public class TimeUtil {
    private static final int MILLISECONDS_IN_SECONDS = 1000;
    private static final int SECONDS_IN_MINUTE = 60;
    private static final int SECONDS_IN_HOUR = SECONDS_IN_MINUTE * 60;
    private static final int SECONDS_IN_DAY = SECONDS_IN_HOUR * 24;

    private TimeUtil() {}

    public static String formatTime(long duration) {
        if(duration == Long.MAX_VALUE) {
            return "LIVE";
        }
        long seconds = Math.round(duration / (double) MILLISECONDS_IN_SECONDS);
        long hours = seconds / SECONDS_IN_HOUR;
        seconds %= SECONDS_IN_HOUR;
        long minutes = seconds / SECONDS_IN_MINUTE;
        seconds %= SECONDS_IN_MINUTE;
        return (hours > 0 ? hours + ":" : "")
            + (minutes < 10 ? "0" + minutes : minutes) + ":"
            + (seconds < 10 ? "0" + seconds : seconds);
    }

    /**
     * Parses a seek time string into milliseconds and determines if it's relative.
     * Supports "colon time" (HH:MM:SS) or "unit time" (1h20m)
     *
     * @param args time string
     * @return SeekTime object, or null if the string could not be parsed
     */
    public static SeekTime parseTime(String args) {
        if(args.isEmpty()) {
            return null;
        }
        String timestamp = args;
        boolean relative = false; // seek forward or backward
        boolean isSeekingBackwards = false;
        char first = timestamp.charAt(0);
        if(first == '+' || first == '-') {
            relative = true;
            isSeekingBackwards = first == '-';
            timestamp = timestamp.substring(1);
        }

        long milliseconds = parseColonTime(timestamp);
        if(milliseconds == -1) {
            milliseconds = parseUnitTime(timestamp);
        }
        if(milliseconds == -1) {
            return null;
        }

        milliseconds *= isSeekingBackwards ? -1 : 1;

        return new SeekTime(milliseconds, relative);
    }

    /**
     * @param timestamp timestamp formatted as: [+ | -] &lt;HH:MM:SS | MM:SS | SS&gt;
     * @return Time in milliseconds
     */
    public static long parseColonTime(String timestamp) {
        String[] timestampSplitArray = timestamp.split(":+");
        if(timestampSplitArray.length > 3) {
            return -1;
        }
        double[] timeUnitArray = new double[3]; // hours, minutes, seconds
        for(int index = 0; index < timestampSplitArray.length; index++) {
            String unit = timestampSplitArray[index];
            if(unit.startsWith("+") || unit.startsWith("-")) {
                return -1;
            }
            unit = unit.replace(",", ".");
            try {
                timeUnitArray[index + 3 - timestampSplitArray.length] = Double.parseDouble(unit);
            }
            catch(NumberFormatException e) {
                return -1;
            }
        }
        return Math.round(
            MILLISECONDS_IN_SECONDS * (
                timeUnitArray[0] * SECONDS_IN_HOUR
                    + timeUnitArray[1] * SECONDS_IN_MINUTE
                    + timeUnitArray[2]
            )
        );
    }

    /**
     * @param timeString time string formatted as a unit time, e.g. 20m10, 1d5h20m14s or 1h and 20m
     * @return Time in milliseconds
     */
    public static long parseUnitTime(String timeString) {
        timeString = timeString.replaceAll("(?i)(\\s|,|and)", "")
            .replaceAll("(?is)(-?\\d+|[a-z]+)", "$1 ")
            .trim();
        String[] vals = timeString.split("\\s+");
        int time = 0;
        for(int j = 0; j < vals.length; j += 2) {
            try {
                int num = Integer.parseInt(vals[j]);

                if(vals.length > j + 1) {
                    if(vals[j + 1].toLowerCase().startsWith("m")) {
                        num *= SECONDS_IN_MINUTE;
                    }
                    else if(vals[j + 1].toLowerCase().startsWith("h")) {
                        num *= SECONDS_IN_HOUR;
                    }
                    else if(vals[j + 1].toLowerCase().startsWith("d")) {
                        num *= SECONDS_IN_DAY;
                    }
                }

                time += num * MILLISECONDS_IN_SECONDS;
            }
            catch(NumberFormatException ex) {
                return -1;
            }
        }
        return time;
    }

    public static class SeekTime {
        public final long milliseconds;
        public final boolean relative;

        private SeekTime(long milliseconds, boolean relative) {
            this.milliseconds = milliseconds;
            this.relative = relative;
        }
    }
}
