package com.jagrosh.jmusicbot;


import com.jagrosh.jmusicbot.utils.TimeUtil;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author Whew., Inc.
 */
public class TimeUtilTest
{
    @Test
    public void singleDigit() {
        TimeUtil.SeekTime seek = TimeUtil.parseTime("5");
        assertNotNull(seek);
        assertEquals(5000, seek.milliseconds);
    }

    @Test
    public void multipleDigits() {
        TimeUtil.SeekTime seek = TimeUtil.parseTime("99:9:999");
        assertNotNull(seek);
        assertEquals(357939000, seek.milliseconds);
    }

    @Test
    public void decimalDigits() {
        TimeUtil.SeekTime seek = TimeUtil.parseTime("99.5:9.0:999.777");
        assertNotNull(seek);
        assertEquals(359739777, seek.milliseconds);
    }

    @Test
    public void relativeSeekingForward() {
        TimeUtil.SeekTime seek = TimeUtil.parseTime("+5");
        assertNotNull(seek);
        assertTrue(seek.relative);
        assertEquals(5000, seek.milliseconds);
    }

    @Test
    public void relativeSeekingBackward() {
        TimeUtil.SeekTime seek = TimeUtil.parseTime("-5");
        assertNotNull(seek);
        assertTrue(seek.relative);
        assertEquals(-5000, seek.milliseconds);
    }
}
