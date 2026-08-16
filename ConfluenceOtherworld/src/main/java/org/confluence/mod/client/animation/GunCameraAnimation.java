package org.confluence.mod.client.animation;

import org.mesdag.portlib.event.client.PortViewportEvent;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;

/// 枪械第一人称相机骨骼动画。
///
/// <p>1.21 TerraGuns 的手枪资源会在部分动作里驱动名为 {@code camera} 的骨骼。
/// 这里不接管任何输入或发射逻辑，只把该骨骼当前旋转量同步到 Forge 的相机角度事件，
/// 让 1.20 合并侧能够保留相同的检视、拔枪和开火视角表现。</p>
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
