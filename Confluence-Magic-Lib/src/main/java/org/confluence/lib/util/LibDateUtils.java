package org.confluence.lib.util;

import net.minecraft.Util;
import net.minecraft.world.level.Level;

import java.time.format.DateTimeParseException;

public final class LibDateUtils {
    private static final short[] TIMES = Util.make(new short[24 * 60], times -> {
        for (int h = 0; h < 24; h++) {
            int t = h * 60;
            int i = (h - 6) * 1000;
            if (i < 0) i += 24000;
            for (int m = 0; m < 60; m++) {
                times[t + m] = (short) (i + (int) (m / 0.06F));
            }
        }
    });
    public static final int _00$00 = getDayTime(0, 0);
    public static final int _04$30 = getDayTime(4, 30);
    public static final int _06$00 = getDayTime(6, 0);
    public static final int _19$30 = getDayTime(19, 30);

    public static int getDayTime(Level level) {
        return getDayTime(level.getDayTime());
    }

    public static int getDayTime(long dayTime) {
        return (int) (dayTime % 24000L);
    }

    /// 映射到游戏内的dayTime
    public static int getDayTime(int hour, int minute) {
        if (hour < 0 || hour > 23) throw new DateTimeParseException("hour bounds is [0, 23], currently is " + hour, "", 0);
        if (minute < 0 || minute > 59) throw new DateTimeParseException("minute bounds is [0, 59], currently is " + minute, "", 0);
        return TIMES[hour * 60 + minute];
    }

    /// @param start   开始的dayTime
    /// @param end     结束的dayTime
    /// @param dayTime 判断的dayTime
    /// @return start <= dayTime <= end
    public static boolean isWithinDayTime(int start, int end, int dayTime) {
        if (start > end) {
            return dayTime >= start || dayTime <= end;
        }
        return dayTime >= start && dayTime <= end;
    }

    public static boolean isWithinDayTime(int start, int end, Level level) {
        return isWithinDayTime(start, end, getDayTime(level));
    }

    public static boolean isWithinDayTime(int startHour, int startMinute, int endHour, int endMinute, int dayTime) {
        return isWithinDayTime(getDayTime(startHour, startMinute), getDayTime(endHour, endMinute), dayTime);
    }

    public static boolean isWithinDayTime(int startHour, int startMinute, int endHour, int endMinute, Level level) {
        return isWithinDayTime(startHour, startMinute, endHour, endMinute, getDayTime(level));
    }

    public static boolean isDay(int dayTime) {
        return isWithinDayTime(_04$30, _19$30, dayTime);
    }

    public static boolean isDay(Level level) {
        return isDay(getDayTime(level));
    }

    public static boolean isNight(int dayTime) {
        return isWithinDayTime(_19$30, _04$30, dayTime);
    }

    public static boolean isNight(Level level) {
        return isNight(getDayTime(level));
    }
}
