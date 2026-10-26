package com.corosus.coroutil.util;

public class CoroUtilWorldTime {

    /**
     * Standard Minecraft day length in ticks
     *
     * @return 24000
     */
    public static int getDayLength() {
        return 24000;
    }

    /**
     * Determines whether the given world game time represents night.
     *
     * - night time first tick = 12542
     * - day time first tick = 23460
     *
     * @param gameTime Minecraft game time in ticks
     * @return true if night time
     */
    public static boolean isNight(long gameTime) {
        long timeMod = gameTime % getDayLength();
        return timeMod >= getNightFirstTick() && timeMod <= getDayFirstTick();
    }

    public static boolean isNightPadded(long gameTime) {
        return isNightPadded(gameTime, 5);
    }

    public static boolean isNightPadded(long gameTime, int padding) {
        long timeMod = gameTime % getDayLength();
        return timeMod >= (getNightFirstTick() + padding) && timeMod <= (getDayFirstTick() - padding);
    }

    public static int getNightFirstTick() {
        return 12542;
    }

    public static int getDayFirstTick() {
        return 23460;
    }

}
