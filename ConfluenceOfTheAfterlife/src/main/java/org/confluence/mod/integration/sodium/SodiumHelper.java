package org.confluence.mod.integration.sodium;

import net.caffeinemc.mods.sodium.client.model.light.data.QuadLightData;
import net.caffeinemc.mods.sodium.client.render.frapi.mesh.MutableQuadViewImpl;
import net.caffeinemc.mods.sodium.client.render.frapi.render.AbstractBlockRenderContext;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

public class SodiumHelper {
    private static Method setColor;
    private static Field quadLightData;
    private static boolean error;

    public static void quad$color(Object quad, int index, int color) {
        if (error) return;
        try {
            if (setColor == null) {
                setColor = MutableQuadViewImpl.class.getDeclaredMethod("color", int.class, int.class);
                setColor.setAccessible(true);
            }
            setColor.invoke(quad, index, color);
        } catch (Exception e) {
            error = true;
        }
    }

    public static void blockRenderer$br(Object renderer, boolean illuminant) {
        if (error) return;
        try {
            if (quadLightData == null) {
                quadLightData = AbstractBlockRenderContext.class.getDeclaredField("quadLightData");
                quadLightData.setAccessible(true);
            }
            if (illuminant) {
                Arrays.fill(((QuadLightData) quadLightData.get(renderer)).br, 1.0F);
            }
        } catch (Exception e) {
            error = true;
        }
    }
}
