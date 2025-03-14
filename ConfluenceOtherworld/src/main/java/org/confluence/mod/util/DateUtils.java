package org.confluence.mod.util;

import com.ibm.icu.util.Calendar;
import com.ibm.icu.util.ChineseCalendar;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.confluence.mod.common.init.item.ConsumableItems;
import org.confluence.mod.common.init.item.FoodItems;
import org.confluence.mod.common.init.item.ModItems;

import java.time.format.DateTimeParseException;

public final class DateUtils {
    private static long lastCacheTime = 0;
    private static final Calendar calendar = Calendar.getInstance();
    private static final ChineseCalendar chineseCalendar = new ChineseCalendar();

    private static void updateTime() {
        if (System.currentTimeMillis() - lastCacheTime > 24 * 60 * 60 * 1000) {
            lastCacheTime = System.currentTimeMillis();
            calendar.setTimeInMillis(lastCacheTime);
            chineseCalendar.setTimeInMillis(lastCacheTime);
        }
    }

    public static Calendar getCalendar() {
        updateTime();
        return calendar;
    }

    public static ChineseCalendar getChineseCalendar() {
        updateTime();
        return chineseCalendar;
    }

    public static boolean isXinNian() {
        updateTime();
        return chineseCalendar.get(Calendar.MONTH) == Calendar.FEBRUARY && chineseCalendar.get(Calendar.DATE) <= 15;
    }

    public static boolean isDuanWu() {
        updateTime();
        return chineseCalendar.get(Calendar.MONTH) == Calendar.JUNE && chineseCalendar.get(Calendar.DATE) == 5;
    }

    public static boolean isZhongQiu() {
        updateTime();
        return chineseCalendar.get(Calendar.MONTH) == Calendar.AUGUST && chineseCalendar.get(Calendar.DATE) == 15;
    }

    public static boolean isHalloween() {
        updateTime();
        int month = calendar.get(Calendar.MONTH);
        int date = calendar.get(Calendar.DATE);
        return (month == Calendar.OCTOBER && date >= 10) || (month == Calendar.NOVEMBER && date == 1);
    }

    public static boolean isChristmas() {
        updateTime();
        return calendar.get(Calendar.MONTH) == Calendar.DECEMBER && calendar.get(Calendar.DATE) >= 15;
    }

    public static Item getHolidayGift() {
        if (isXinNian()) return ConsumableItems.RED_ENVELOPE.get();
        if (isDuanWu()) return FoodItems.ZONGZI.get();
        if (isZhongQiu()) return FoodItems.EGG_YOLK_MOONCAKES.get();
        if (isHalloween()) return ConsumableItems.GOODIE_BAG.get();
        if (isChristmas()) return ConsumableItems.CHRISTMAS_GIFT.get();
        return Items.AIR;
    }

    public static Item getHeartItem() {
        if (isHalloween()) return ModItems.CANDY_APPLE.get();
        if (isChristmas()) return ModItems.CANDY_CANE.get();
        return ModItems.HEART.get();
    }

    public static Item getStarItem() {
        if (isHalloween()) return ModItems.SOUL_CAKE.get();
        if (isChristmas()) return ModItems.SUGAR_PLUM.get();
        return ModItems.STAR.get();
    }

    /**
     * 映射到游戏内的dayTime
     */
    public static int getDayTime(int hour, int minute) {
        if (hour < 0 || hour > 23) throw new DateTimeParseException("hour bounds is [0, 23], currently is " + hour, "", 0);
        if (minute < 0 || minute > 59) throw new DateTimeParseException("minute bounds is [0, 59], currently is " + minute, "", 0);
        int i = (hour - 6) * 1000;
        int j = (int) (minute / 0.06F);
        if (i < 0) i += 24000;
        return i + j;
    }

    /**
     * @param start 开始的dayTime
     * @param end   结束的dayTime
     * @param time  判断的dayTime
     * @return start <= time <= end
     */
    public static boolean isWithinDayTime(int start, int end, long time) {
        time %= 24000L;
        if (start > end) {
            return time >= start || time <= end;
        }
        return time >= start && time <= end;
    }

    public static boolean isWithinDayTime(int startHour, int startMinute, int endHour, int endMinute, long time) {
        return isWithinDayTime(getDayTime(startHour, startMinute), getDayTime(endHour, endMinute), time);
    }
}
