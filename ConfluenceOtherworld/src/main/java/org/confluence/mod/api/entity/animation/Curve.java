package org.confluence.mod.api.entity.animation;

import net.minecraft.world.phys.Vec3;

public interface Curve {
    /// 按曲线公式计算
    ///
    /// @param t 时间参数
    Vec3 cal(double t);

    /// 匀速曲线
    ///
    /// @param t 时间参数
    Vec3 calUniformed(double t);
}
