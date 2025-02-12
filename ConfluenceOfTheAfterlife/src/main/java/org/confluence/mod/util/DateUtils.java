package org.confluence.mod.util;

import com.nlf.calendar.Lunar;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.confluence.mod.common.init.item.ConsumableItems;
import org.confluence.mod.common.init.item.FoodItems;
import org.confluence.mod.common.init.item.ModItems;

import java.util.Calendar;

public final class DateUtils {
    public static boolean isXinNian(Lunar lunar) {
        return lunar.getMonth() == 1 && lunar.getDay() <= 15;
    }

    public static boolean isDuanWu(Lunar lunar) {
        return lunar.getMonth() == 5 && lunar.getDay() == 5;
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

    public static Item getHolidayGift() {
        Lunar lunar = new Lunar();
        if (isXinNian(lunar)) return ConsumableItems.RED_ENVELOPE.get();
        if (isDuanWu(lunar)) return FoodItems.ZONGZI.get();
        if (isZhongQiu(lunar)) return FoodItems.EGG_YOLK_MOONCAKES.get();
        Calendar calendar = Calendar.getInstance();
        if (isHalloween(calendar)) return ConsumableItems.GOODIE_BAG.get();
        if (isChristmas(calendar)) return ConsumableItems.CHRISTMAS_GIFT.get();
        return Items.AIR;
    }

    public static Item getHeartItem() {
        Calendar calendar = Calendar.getInstance();
        if (isHalloween(calendar)) return ModItems.CANDY_APPLE.get();
        if (isChristmas(calendar)) return ModItems.CANDY_CANE.get();
        return ModItems.HEART.get();
    }

    public static Item getStarItem() {
        Calendar calendar = Calendar.getInstance();
        if (isHalloween(calendar)) return ModItems.SOUL_CAKE.get();
        if (isChristmas(calendar)) return ModItems.SUGAR_PLUM.get();
        return ModItems.STAR.get();
    }
}
