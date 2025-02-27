package org.confluence.mod.integration.sodium;

import net.caffeinemc.mods.sodium.client.render.frapi.mesh.MutableQuadViewImpl;

import java.lang.reflect.Method;

public class SodiumHelper {
    private static Method setColor;
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
}
