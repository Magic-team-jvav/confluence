package nowebsite.makertechno.the_trackers.core.track.algorithm;

import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class RelativeProjector {
    public static @NotNull Vec3 eyeAlignedProjection(@NotNull Vec3 relativeVec, @NotNull Vec3 playerEyeSight) {
        // 计算玩家视角的水平角度
        double playerYaw = Math.atan2(playerEyeSight.z, playerEyeSight.x);
        // 将相对向量旋转到玩家视角对齐的坐标系
        double rotatedX = relativeVec.x * Math.cos(-playerYaw) - relativeVec.z * Math.sin(-playerYaw);
        double rotatedZ = relativeVec.x * Math.sin(-playerYaw) + relativeVec.z * Math.cos(-playerYaw);
        return new Vec3(rotatedX, relativeVec.y, rotatedZ);
    }

    // 计算玩家和生物位置的投影并计算角度
    public static float[] calculateAngle(Vec3 playerPos, Vec3 playerEyeSight, @NotNull Vec3 entityPos, @NotNull ProjectToAngleAndDistance projector) {
        // 计算玩家位置的投影
        Vec2 base = projector.project(eyeAlignedProjection(playerEyeSight, playerEyeSight));
        // 计算生物位置的投影
        Vec2 target = projector.project(eyeAlignedProjection(entityPos.subtract(playerPos), playerEyeSight));

        // 计算连线角度
        Vec2 result = target.add(base.negated());

        // 计算距离
        double distance = Math.sqrt(Math.pow(target.x - base.x, 2) + Math.pow(target.y - base.y, 2));
        return new float[]{(float) (Math.atan2(result.y, result.x) + Math.PI), (float) distance};
    }
}
