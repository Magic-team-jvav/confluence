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
        putColor(GunItems.MUSKET_BULLET, 0xFFFD3E03);
        putColor(GunItems.METEOR_SHOT, 0xFFFD034A);
        putColor(GunItems.SILVER_BULLET, 0xFFFFAF8B);
        putColor(GunItems.CRYSTAL_BULLET, 0x2FF04058);
        putColor(GunItems.CURSED_BULLET, 0xFF60F802);
        putColor(GunItems.CHLOROPHYTE_BULLET, 0xFF01EB0F);
        putColor(GunItems.HIGH_VELOCITY_BULLET, 0xFFFFDD17);
        putColor(GunItems.ICHOR_BULLET, 0xFFFFEA01);
        putColor(GunItems.VENOM_BULLET, 0xFF974FA2);
        putColor(GunItems.PARTY_BULLET, 0xFFF0009E);
        putColor(GunItems.NANO_BULLET, 0xFF00A7F0);
        putColor(GunItems.EXPLODING_BULLET, 0xFFF03E00);
        putColor(GunItems.GOLDEN_BULLET, 0xFFB9A417);
        putColor(GunItems.LUMINITE_BULLET, 0xFF53FFC3);
        putColor(GunItems.TUNGSTEN_BULLET, 0xFFFD3E03);
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
