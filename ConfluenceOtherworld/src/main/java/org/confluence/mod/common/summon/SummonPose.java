package org.confluence.mod.common.summon;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

/**
 * 保存召唤物在某一游戏刻的位置与朝向。
 *
 * <p>路径规划、网络同步和客户端插值共用该不可变数据，避免分别计算朝向而产生抖动或错位。</p>
 */
public record SummonPose(Vec3 position, float yaw, float pitch, float roll) {
    public SummonPose {
        if (position == null) {
            throw new IllegalArgumentException("Summon pose position must not be null");
        }
        if (!Float.isFinite(yaw) || !Float.isFinite(pitch) || !Float.isFinite(roll)) {
            throw new IllegalArgumentException("Summon pose rotation must be finite");
        }
    }

    public SummonPose interpolate(SummonPose target, float progress) {
        float clamped = Mth.clamp(progress, 0.0F, 1.0F);
        return new SummonPose(position.lerp(target.position, clamped), Mth.rotLerp(clamped, yaw, target.yaw),
                Mth.rotLerp(clamped, pitch, target.pitch), Mth.rotLerp(clamped, roll, target.roll));
    }
}
