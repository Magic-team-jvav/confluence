package org.confluence.mod.integration;

import net.caffeinemc.mods.sodium.client.render.frapi.mesh.MutableQuadViewImpl;
import net.caffeinemc.mods.sodium.client.render.frapi.mesh.QuadViewImpl;
import net.minecraft.core.Direction;
import org.confluence.mod.common.data.saved.BrushData;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;

public class SodiumHelper {
    private static Method nominalFace;
    private static Method getColor;
    private static Method setColor;
    private static boolean error;

    public static @Nullable Direction quad$nominalFace(Object quad) {
        if (error) return null;
        try {
            if (nominalFace == null) {
                nominalFace = QuadViewImpl.class.getDeclaredMethod("nominalFace");
                nominalFace.setAccessible(true);
            }
            return (Direction) nominalFace.invoke(quad);
        } catch (Exception e) {
            error = true;
            return null;
        }
    }

    public static int quad$color(Object quad, int index) {
        if (error) return BrushData.EMPTY_COLOR;
        try {
            if (getColor == null) {
                getColor = QuadViewImpl.class.getDeclaredMethod("color", int.class);
                getColor.setAccessible(true);
            }
            return (int) getColor.invoke(quad, index);
        } catch (Exception e) {
            error = true;
            return BrushData.EMPTY_COLOR;
        }
    }

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
