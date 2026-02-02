package org.confluence.mod.util;

import com.google.common.base.Joiner;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.confluence.mod.api.lunar.Lunar;
import org.confluence.mod.common.init.item.ConsumableItems;
import org.confluence.mod.common.init.item.FoodItems;
import org.confluence.mod.common.init.item.ModItems;

import java.util.Calendar;
import java.util.Date;

public final class DateUtils {
    private static long lastCacheTime = 0;
    private static final Calendar calendar = Calendar.getInstance();
    private static Lunar lunar;

    private static String yi = "无";
    private static String ji = "无";

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
            Joiner joiner = Joiner.on("，");
            yi = joiner.join(lunar.getDayYi());
            ji = joiner.join(lunar.getDayJi());
        }
    }

    public static String getYi() {
        return yi;
    }

    public static String getJi() {
        return ji;
    }

    public static boolean isXinNian(Lunar lunar) {
        return lunar.getMonth() == 1 && lunar.getDay() <= 15;
    }

    public static boolean isYuanXiao(Lunar lunar) {
        return lunar.getMonth() == 1 && lunar.getDay() == 15;
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
        if (isXinNian(lunar)) {
            return ConsumableItems.RED_ENVELOPE.get();
        }
        if (isDuanWu(lunar)) {
            return random.nextBoolean() ? FoodItems.ZONGZI.get() : FoodItems.MEAT_STUFFED_ZONGZI.get();
        }
        if (isZhongQiu(lunar)) {
            return FoodItems.EGG_YOLK_MOONCAKES.get();
        }
        Calendar calendar = getCalendar();
        if (isHalloween(calendar)) {
            return ConsumableItems.GOODIE_BAG.get();
        }
        if (isChristmas(calendar)) {
            return ConsumableItems.CHRISTMAS_GIFT.get();
        }
        return Items.AIR;
    }

    public static Item getHeartItem() {
        Calendar calendar = getCalendar();
        if (isHalloween(calendar)) {
            return ModItems.CANDY_APPLE.get();
        }
        if (isChristmas(calendar)) {
            return ModItems.CANDY_CANE.get();
        }
        return ModItems.HEART.get();
    }

    public static Item getStarItem() {
        Calendar calendar = getCalendar();
        if (isHalloween(calendar)) {
            return ModItems.SOUL_CAKE.get();
        }
        if (isChristmas(calendar)) {
            return ModItems.SUGAR_PLUM.get();
        }
        return ModItems.STAR.get();
    }
}
