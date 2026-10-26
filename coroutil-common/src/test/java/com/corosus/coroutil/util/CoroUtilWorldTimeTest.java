package com.corosus.coroutil.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CoroUtilWorldTimeTest {

    @Test
    void testIsNightCalculations() {
        // Daytime tick
        assertFalse(CoroUtilWorldTime.isNight(6000), "Tick 6000 should be day");

        // Nighttime tick
        assertTrue(CoroUtilWorldTime.isNight(13000), "Tick 13000 should be night");

        // First tick of night
        assertTrue(CoroUtilWorldTime.isNight(12542), "Tick 12542 should be night");

        // Modulo day length check (next day)
        assertTrue(CoroUtilWorldTime.isNight(24000 + 13000), "Tick 37000 should also be night");
    }

    @Test
    void testIsNightPadded() {
        assertFalse(CoroUtilWorldTime.isNightPadded(12542, 5), "Tick 12542 should not pass padded check with padding=5");
        assertTrue(CoroUtilWorldTime.isNightPadded(12542 + 10, 5), "Tick 12552 should pass padded check");
    }
}
