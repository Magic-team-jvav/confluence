package org.confluence.mod.integration.kubejs;

import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.ModList;
import net.neoforged.neoforgespi.language.IModFileInfo;

import java.lang.reflect.Field;
import java.util.function.BooleanSupplier;

public class KubeJSHelper {
    public static final boolean IS_LOADED = ModList.get().isLoaded("kubejs");
    private static boolean error = false;
    private static Field kjs$itemSize;
    public static final boolean IS_NEW_VERSION = ((BooleanSupplier) () -> {
        IModFileInfo kubejs = ModList.get().getModFileById("kubejs");
        if (kubejs != null) {
            String version = kubejs.versionString();
            if (version.length() > 7) {
                return Double.parseDouble(version.substring(5, 8)) > 7.2;
            }
        }
        return false;
    }).getAsBoolean();

    @OnlyIn(Dist.CLIENT)
    public static boolean shouldDrawStackSize(GuiGraphics graphics) {
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
