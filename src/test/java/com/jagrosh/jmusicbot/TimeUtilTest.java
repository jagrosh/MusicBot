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
package com.jagrosh.jmusicbot;

import com.jagrosh.jmusicbot.utils.TimeUtil;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Whew., Inc.
 */
public class TimeUtilTest {
    @Test
    public void singleDigit() {
        TimeUtil.SeekTime seek = TimeUtil.parseTime("5");
        Assert.assertNotNull(seek);
        Assert.assertEquals(5000, seek.milliseconds);
    }

    @Test
    public void multipleDigits() {
        TimeUtil.SeekTime seek = TimeUtil.parseTime("99:9:999");
        Assert.assertNotNull(seek);
        Assert.assertEquals(357939000, seek.milliseconds);

        seek = TimeUtil.parseTime("99h9m999s");
        Assert.assertNotNull(seek);
        Assert.assertEquals(357939000, seek.milliseconds);
    }

    @Test
    public void decimalDigits() {
        TimeUtil.SeekTime seek = TimeUtil.parseTime("99.5:9.0:999.777");
        Assert.assertNotNull(seek);
        Assert.assertEquals(359739777, seek.milliseconds);
    }

    @Test
    public void seeking() {
        TimeUtil.SeekTime seek = TimeUtil.parseTime("5");
        Assert.assertNotNull(seek);
        Assert.assertFalse(seek.relative);
        Assert.assertEquals(5000, seek.milliseconds);
    }

    @Test
    public void relativeSeekingForward() {
        TimeUtil.SeekTime seek = TimeUtil.parseTime("+5");
        Assert.assertNotNull(seek);
        Assert.assertTrue(seek.relative);
        Assert.assertEquals(5000, seek.milliseconds);
    }

    @Test
    public void relativeSeekingBackward() {
        TimeUtil.SeekTime seek = TimeUtil.parseTime("-5");
        Assert.assertNotNull(seek);
        Assert.assertTrue(seek.relative);
        Assert.assertEquals(-5000, seek.milliseconds);
    }

    @Test
    public void parseTimeArgumentLength() {
        TimeUtil.SeekTime seek = TimeUtil.parseTime("");
        Assert.assertNull(seek);
    }

    @Test
    public void timestampTotalUnits() {
        TimeUtil.SeekTime seek = TimeUtil.parseTime("1:1:1:1");
        Assert.assertNull(seek);

        seek = TimeUtil.parseTime("1h2m3m4s5s");
        Assert.assertNotNull(seek);
        Assert.assertEquals(3909000, seek.milliseconds);
    }

    @Test
    public void relativeSymbol() {
        TimeUtil.SeekTime seek = TimeUtil.parseTime("+-1:-+1:+-1");
        Assert.assertNull(seek);
    }

    @Test
    public void timestampNumberFormat() {
        TimeUtil.SeekTime seek = TimeUtil.parseTime("1:1:a");
        Assert.assertNull(seek);

        seek = TimeUtil.parseTime("1a2s");
        Assert.assertNotNull(seek);
        Assert.assertEquals(3000, seek.milliseconds);
    }
}
