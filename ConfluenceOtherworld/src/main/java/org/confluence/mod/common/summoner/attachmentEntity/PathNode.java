package org.confluence.mod.common.summoner.attachmentEntity;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public record PathNode(Vec3 pos, float yaw, float pitch, float roll) {

    public PathNode(Vec3 pos, Vec3 direction) {
        this(pos, yawFromDirection(direction), pitchFromDirection(direction), 0);
    }

    public static float yawFromDirection(Vec3 direction) {
        return (float) Math.toDegrees(Math.atan2(-direction.x, direction.z));
    }

    public static float pitchFromDirection(Vec3 direction) {
        if (direction.lengthSqr() < 1.0E-12) {
            return 0;
        }
        Vec3 normalized = direction.normalize();
        return (float) Math.toDegrees(Math.asin(-normalized.y));
    }

    /** 欧拉角转四元数，与渲染侧 {@code Axis.YN(yaw)·XP(pitch)·ZP(roll)} 的约定一致 */
    public Quaternionf toQuaternion() {
        return new Quaternionf()
                .rotateY((float) Math.toRadians(-yaw))
                .rotateX((float) Math.toRadians(pitch))
                .rotateZ((float) Math.toRadians(roll));
    }

    /**
     * 位置线性插值，朝向先在四元数空间求测地线（slerp）再转回欧拉角。
     * <p>
     * 逐轴 {@code Mth.rotLerp} 只是 yaw/pitch/roll 三条曲线各自线性，合成的旋转在 SO(3) 里
     * 会走一条偏离最短路径的弯道：两个采样的姿态差越大，中间帧就越明显地绕轴扭动（剑身类
     * 细长模型尤其明显），在俯仰接近 ±90° 时还会退化。slerp 沿两姿态间的最短弧旋转、角速度
     * 均匀，渲染侧再用同一套欧拉约定还原，因此看到的姿态就是这条最短弧。
     * </p>
     */
    public PathNode lerp(PathNode to, float partialTick) {
        Vec3 pos = this.pos().lerp(to.pos(), partialTick);
        Quaternionf from = toQuaternion();
        Quaternionf target = to.toQuaternion();
        // 同一旋转的四元数有正负两种表示，先统一到同一侧，slerp 才会走最短弧
        if (from.x * target.x + from.y * target.y + from.z * target.z + from.w * target.w < 0.0F) {
            target.set(-target.x, -target.y, -target.z, -target.w);
        }
        return fromQuaternion(pos, from.slerp(target, partialTick));
    }

    /** 四元数转回欧拉角：forward 定 yaw/pitch，roll 由 roll=0 时的局部 Y 轴与当前局部 Y 轴的夹角给出 */
    private static PathNode fromQuaternion(Vec3 pos, Quaternionf rotation) {
        Vector3f forward = rotation.transform(new Vector3f(0, 0, 1));
        Vector3f up = rotation.transform(new Vector3f(0, 1, 0));
        float yaw = (float) Math.toDegrees(Math.atan2(-forward.x, forward.z));
        float pitch = (float) Math.toDegrees(Math.asin(Mth.clamp(-forward.y, -1.0F, 1.0F)));
        double yawRad = Math.toRadians(yaw);
        double pitchRad = Math.toRadians(pitch);
        Vector3f localY = new Vector3f((float) (-Math.sin(yawRad) * Math.sin(pitchRad)), (float) Math.cos(pitchRad), (float) (Math.cos(yawRad) * Math.sin(pitchRad)));
        Vector3f cross = localY.cross(up, new Vector3f());
        float roll = (float) Math.toDegrees(Math.atan2(cross.dot(forward), localY.dot(up)));
        return new PathNode(pos, yaw, pitch, roll);
    }

    public PathNode modifyPos(Vec3 pos) {
        return new PathNode(pos, yaw, pitch, roll);
    }

    public PathNode modifyEuler(float yaw, float pitch, float roll) {
        return new PathNode(pos, yaw, pitch, roll);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof PathNode other) {
            return other.pos().equals(this.pos()) && other.yaw() == this.yaw() && other.pitch() == this.pitch() && other.roll() == this.roll();
        }
        return false;
    }
}
