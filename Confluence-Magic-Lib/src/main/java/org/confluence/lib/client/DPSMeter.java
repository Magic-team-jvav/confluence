package org.confluence.lib.client;

public final class DPSMeter {
    private static boolean dpsStarted;
    private static long dpsLastHit;
    private static float dpsDamage;
    private static long dpsStart;
    private static long dpsEnd;

    public static void addDPS(float dmg, long currentTime) {
        if (dpsStarted) {
            dpsLastHit = currentTime;
            dpsDamage += dmg;
            dpsEnd = currentTime;
        } else {
            dpsStarted = true;
            dpsStart = currentTime;
            dpsEnd = currentTime;
            dpsLastHit = currentTime;
            dpsDamage = dmg;
        }
    }

    public static void checkDPSTime(long currentTime) {
        if (dpsStarted && currentTime - dpsLastHit >= 30) {
            dpsStarted = false;
        }
    }

    public static float getDPS(long currentTime) {
        float num = (dpsEnd - dpsStart) / 20.0F;
        if (num >= 3.0F) {
            dpsStart = currentTime - 20;
            dpsDamage = dpsDamage / num;
            num = (dpsEnd - dpsStart) / 20.0F;
        }
        if (num < 1.0F) num = 1.0F;
        return dpsDamage / num;
    }
}
