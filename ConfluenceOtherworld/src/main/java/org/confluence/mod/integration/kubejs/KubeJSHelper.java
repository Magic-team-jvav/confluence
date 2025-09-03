package org.confluence.mod.integration.kubejs;

import net.neoforged.fml.ModList;

import java.lang.reflect.Field;

public class KubeJSHelper {
    public static final boolean IS_LOADED = ModList.get().isLoaded("kubejs");
    private static boolean error = false;
    private static Field kjs$itemSize;

    public static boolean shouldDrawStackSize(Object graphics) {
        if (IS_LOADED && !error) {
            try {
                if (kjs$itemSize == null) {
                    kjs$itemSize = graphics.getClass().getDeclaredField("kjs$itemSize");
                    kjs$itemSize.setAccessible(true);
                }
                return ((int[]) kjs$itemSize.get(graphics))[0] != 0;
            } catch (NoSuchFieldException | IllegalAccessException e) {
                error = true;
            }
        }
        return false;
    }
}
