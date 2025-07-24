package org.confluence.mod.util;

import com.nlf.calendar.Lunar;
import net.minecraft.Util;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.init.item.ConsumableItems;
import org.confluence.mod.common.init.item.FoodItems;
import org.confluence.mod.common.init.item.ModItems;

import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.Date;

public final class DateUtils {
    private static final short[] TIMES = Util.make(new short[24 * 60], times -> {
        for (int h = -120; h < 360; h += 20) {
            int t = h + h + h + 360;
            int i = h * 50 + 24000;
            for (int m = 0; m < 60; m++) {
                times[t + m] = (short) (i + (int) (m * 16.666668F));
            }
        }
    });
    public static final int _00$00 = getDayTime(0, 0);
    public static final int _04$30 = getDayTime(4, 30);
    public static final int _06$00 = getDayTime(6, 0);
    public static final int _19$30 = getDayTime(19, 30);

    private static long lastCacheTime = 0;
    private static final Calendar calendar = Calendar.getInstance();
    private static Lunar lunar;

    public static Calendar getCalendar() {
        updateTime();
        return calendar;
    }

    public static Lunar getLunar() {
        updateTime();
        return lunar;
    }

    private static void updateTime() {
        if (System.currentTimeMillis() - lastCacheTime > 24 * 60 * 60 * 1000) {
            lastCacheTime = System.currentTimeMillis();
            lunar = null;
        }
        if (lunar == null) {
            calendar.setTimeInMillis(lastCacheTime);
            lunar = new Lunar(new Date(lastCacheTime));
        }
    }

    public static boolean isXinNian(Lunar lunar) {
        return lunar.getMonth() == 1 && lunar.getDay() <= 15;
    }

    public static boolean isQingMing(Lunar lunar) {
        return lunar.getMonth() == 3 && (lunar.getDay() >= 7 && lunar.getDay() <= 9);
    }

    public static boolean isDuanWu(Lunar lunar) {
        return lunar.getMonth() == 5 && lunar.getDay() >= 5 && lunar.getDay() <= 7;
    }

    public static boolean isZhongQiu(Lunar lunar) {
        return lunar.getMonth() == 8 && lunar.getDay() == 15;
    }

    public static boolean isHalloween(Calendar calendar) {
        int month = calendar.get(Calendar.MONTH);
        int date = calendar.get(Calendar.DATE);
        return (month == Calendar.OCTOBER && date >= 10) || (month == Calendar.NOVEMBER && date == 1);
    }

    public static boolean isChristmas(Calendar calendar) {
        return calendar.get(Calendar.MONTH) == Calendar.DECEMBER && calendar.get(Calendar.DATE) >= 15;
    }

    public static Item getHolidayGift(RandomSource random) {
        Lunar lunar = getLunar();
        if (isXinNian(lunar)) return ConsumableItems.RED_ENVELOPE.get();
        if (isDuanWu(lunar)) return random.nextBoolean() ? FoodItems.ZONGZI.get() : FoodItems.MEAT_STUFFED_ZONGZI.get();
        if (isZhongQiu(lunar)) return FoodItems.EGG_YOLK_MOONCAKES.get();
        Calendar calendar = getCalendar();
        if (isHalloween(calendar)) return ConsumableItems.GOODIE_BAG.get();
        if (isChristmas(calendar)) return ConsumableItems.CHRISTMAS_GIFT.get();
        return Items.AIR;
    }

    public static Item getHeartItem() {
        Calendar calendar = getCalendar();
        if (isHalloween(calendar)) return ModItems.CANDY_APPLE.get();
        if (isChristmas(calendar)) return ModItems.CANDY_CANE.get();
        return ModItems.HEART.get();
    }

    public static Item getStarItem() {
        Calendar calendar = getCalendar();
        if (isHalloween(calendar)) return ModItems.SOUL_CAKE.get();
        if (isChristmas(calendar)) return ModItems.SUGAR_PLUM.get();
        return ModItems.STAR.get();
    }

    public static int getDayTime(Level level) {
        return getDayTime(level.getDayTime());
    }

    public static int getDayTime(long dayTime) {
        return (int) (dayTime % 24000L);
    }

    /**
     * 映射到游戏内的dayTime
     */
    public static int getDayTime(int hour, int minute) {
        if (hour < 0 || hour > 23) throw new DateTimeParseException("hour bounds is [0, 23], currently is " + hour, "", 0);
        if (minute < 0 || minute > 59) throw new DateTimeParseException("minute bounds is [0, 59], currently is " + minute, "", 0);
        return TIMES[hour * 60 + minute];
    }

    /**
     * @param start   开始的dayTime
     * @param end     结束的dayTime
     * @param dayTime 判断的dayTime
     * @return start <= dayTime <= end
     */
    public static boolean isWithinDayTime(int start, int end, int dayTime) {
        if (start > end) {
            return dayTime >= start || dayTime <= end;
        }
        return dayTime >= start && dayTime <= end;
    }

    public static boolean isWithinDayTime(int startHour, int startMinute, int endHour, int endMinute, int dayTime) {
        return isWithinDayTime(getDayTime(startHour, startMinute), getDayTime(endHour, endMinute), dayTime);
    }

    public static boolean isDay(int dayTime) {
        return isWithinDayTime(_04$30, _19$30, dayTime);
    }

    public static boolean isNight(int dayTime) {
        return isWithinDayTime(_19$30, _04$30, dayTime);
    }
}
