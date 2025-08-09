package org.confluence.mod.util;

import com.nlf.calendar.Lunar;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.confluence.lib.util.LibDateUtils;
import org.confluence.mod.common.init.item.ConsumableItems;
import org.confluence.mod.common.init.item.FoodItems;
import org.confluence.mod.common.init.item.ModItems;

import java.util.Calendar;
import java.util.Date;

public final class DateUtils {
    public static final @Deprecated int _00$00 = LibDateUtils._00$00;
    public static final @Deprecated int _04$30 = LibDateUtils._04$30;
    public static final @Deprecated int _06$00 = LibDateUtils._06$00;
    public static final @Deprecated int _19$30 = LibDateUtils._19$30;

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

    @Deprecated
    public static int getDayTime(Level level) {
        return LibDateUtils.getDayTime(level);
    }

    @Deprecated
    public static int getDayTime(long dayTime) {
        return LibDateUtils.getDayTime(dayTime);
    }

    @Deprecated
    public static int getDayTime(int hour, int minute) {
        return LibDateUtils.getDayTime(hour, minute);
    }

    @Deprecated
    public static boolean isWithinDayTime(int start, int end, int dayTime) {
        return LibDateUtils.isWithinDayTime(start, end, dayTime);
    }

    @Deprecated
    public static boolean isWithinDayTime(int startHour, int startMinute, int endHour, int endMinute, int dayTime) {
        return LibDateUtils.isWithinDayTime(startHour, startMinute, endHour, endMinute, dayTime);
    }

    @Deprecated
    public static boolean isDay(int dayTime) {
        return LibDateUtils.isDay(dayTime);
    }

    @Deprecated
    public static boolean isNight(int dayTime) {
        return LibDateUtils.isNight(dayTime);
    }
}
