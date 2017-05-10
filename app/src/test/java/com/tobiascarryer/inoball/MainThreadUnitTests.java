package com.tobiascarryer.inoball;

import org.junit.Test;

import static org.junit.Assert.*;

public class MainThreadUnitTests {
    @Test
    public void canCovertMillisecondsToNanoseconds() throws Exception {
        MainThread thread = new MainThread(null, null);
        assertEquals(7109, thread.nanosecondsToMilliseconds(7109905000L));
        assertEquals(0, thread.nanosecondsToMilliseconds(999999));
        assertEquals(1, thread.nanosecondsToMilliseconds(1000000));
    }
}