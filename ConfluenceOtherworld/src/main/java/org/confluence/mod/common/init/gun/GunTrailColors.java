package org.confluence.mod.common.init.gun;

import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.common.init.item.GunItems;
import org.confluence.mod.common.item.BaseBullet;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class GunTrailColors {
    protected static Map<String, Integer> colorMap = new HashMap<>();
    private static final Logger LOGGER = LogUtils.getLogger();

    public static void init() {
        // 颜色取自对应物品贴图；透明度保持不透明，由轨迹和头部渲染器自行处理淡出。
        putColor(GunItems.MUSKET_BULLET, 0xFF9A8E87);
        putColor(GunItems.METEOR_SHOT, 0xFFD65A4B);
        putColor(GunItems.SILVER_BULLET, 0xFFB4CDD8);
        putColor(GunItems.CRYSTAL_BULLET, 0xFF9E68FF);
        putColor(GunItems.CURSED_BULLET, 0xFFD8F53E);
        putColor(GunItems.CHLOROPHYTE_BULLET, 0xFF65D64D);
        putColor(GunItems.HIGH_VELOCITY_BULLET, 0xFFEAC76A);
        putColor(GunItems.ICHOR_BULLET, 0xFFF4B951);
        putColor(GunItems.VENOM_BULLET, 0xFFB277E0);
        putColor(GunItems.PARTY_BULLET, 0xFF58D98B);
        putColor(GunItems.NANO_BULLET, 0xFF14D5F0);
        putColor(GunItems.EXPLODING_BULLET, 0xFFE34C42);
        putColor(GunItems.GOLDEN_BULLET, 0xFFF0C86B);
        putColor(GunItems.LUMINITE_BULLET, 0xFF5CE6C2);
        putColor(GunItems.TUNGSTEN_BULLET, 0xFF85AA73);
    }

    public static void putColor(String item) {
        putColor(item, 0xFFFD3E03);
    }

    public static void putColor(String item, int red, int green, int blue, int alpha) {
        putColor(item, FastColor.ARGB32.color(red, green, blue, alpha));
    }

    public static void putColor(Supplier<? extends Item> item, int color) {
        putColor(item.get(), color);
    }

    public static void putColor(Item item, int color) {
        String path = BuiltInRegistries.ITEM.getKey(item).getPath();
        colorMap.put(path, color);
    }

    public static void putColor(String item, int color) {
        colorMap.put(item, color);
    }

    public static int getColor(ItemStack itemStack) {
        try {
            return getColor((BaseBullet) itemStack.getItem());
        } catch (NullPointerException e) {
            LOGGER.error("Can't find trail color", e);
        }
        return 0;
    }

    public static int getColor(BaseBullet item) {
        String path = BuiltInRegistries.ITEM.getKey(item).getPath();
        String selectID = item.colorID() == null ? path : item.colorID();
        return getColor(selectID);
    }

    public static int getColor(String item) {
        return colorMap.getOrDefault(item, 0xFFFD3E03);
    }
}
