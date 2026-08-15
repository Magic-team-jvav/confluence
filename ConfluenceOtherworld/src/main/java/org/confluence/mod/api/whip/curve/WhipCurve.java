package org.confluence.mod.api.whip.curve;

import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * 根据归一化攻击进度生成鞭身局部控制点的公开轨迹接口。
 */
@FunctionalInterface
public interface WhipCurve {
    List<Vec3> controlPoints(double progress);
}
