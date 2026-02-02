package org.confluence.terra_guns.common.init;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.confluence.terra_guns.TerraGuns;
import org.confluence.terra_guns.common.item.bullet.BaseBullet;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static org.confluence.terra_guns.common.init.TGItems.*;

public class TGTrailColors {
    protected static Map<String, Integer> colorMap = new HashMap<>();

    public static void init() {
        putColor(MUSKET_BULLET, 0xFFFD3E03);
        putColor(METEOR_SHOT, 0xFFFD034A);
        putColor(SILVER_BULLET, 0xFFFFAF8B);
        putColor(CRYSTAL_BULLET, 0x2FF04058);
        putColor(CURSED_BULLET, 0xFF60F802);
        putColor(CHLOROPHYTE_BULLET, 0xFF01EB0F);
        putColor(HIGH_VELOCITY_BULLET, 0xFFFFDD17);
        putColor(ICHOR_BULLET, 0xFFFFEA01);
        putColor(VENOM_BULLET, 0xFF974FA2);
        putColor(PARTY_BULLET, 0xFFF0009E);
        putColor(NANO_BULLET, 0xFF00A7F0);
        putColor(EXPLODING_BULLET, 0xFFF03E00);
        putColor(GOLDEN_BULLET, 0xFFB9A417);
        putColor(LUMINITE_BULLET, 0xFF53FFC3);
        putColor(TUNGSTEN_BULLET, 0xFFFD3E03);
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
            TerraGuns.LOGGER.error("Can't find trail color", e);
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
