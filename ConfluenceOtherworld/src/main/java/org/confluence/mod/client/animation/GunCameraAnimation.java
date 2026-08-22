package org.confluence.mod.client.animation;

import org.mesdag.portlib.event.client.PortViewportEvent;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;

public final class GunCameraAnimation {
    private static final float EPSILON = 0.0001F;

    private static float pitch;
    private static float yaw;
    private static float roll;

    private GunCameraAnimation() {}

    public static void capture(CoreGeoBone cameraBone) {
        if (cameraBone == null) {
            clear();
            return;
        }
        pitch = (float) Math.toDegrees(cameraBone.getRotX());
        yaw = (float) Math.toDegrees(cameraBone.getRotY());
        roll = (float) Math.toDegrees(cameraBone.getRotZ());
    }

    public static void clear() {
        pitch = 0.0F;
        yaw = 0.0F;
        roll = 0.0F;
    }

    public static void apply(PortViewportEvent.ComputeCameraAngles event) {
        if (Math.abs(pitch) < EPSILON && Math.abs(yaw) < EPSILON && Math.abs(roll) < EPSILON) {
            return;
        }
        if (event.getCamera().isDetached()) {
            return;
        }
        event.setPitch(event.getPitch() + pitch);
        event.setYaw(event.getYaw() + yaw);
        event.setRoll(event.getRoll() + roll);
    }
}
