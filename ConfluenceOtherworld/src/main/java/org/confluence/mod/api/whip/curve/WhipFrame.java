package org.confluence.mod.api.whip.curve;

import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Objects;

/// 挥鞭时间轴上的一帧归一化局部控制点。
///
/// @param progress      处于 {@code [0, 1]} 的动画进度
/// @param controlPoints 以玩家手部锚点为原点的局部控制点
public record WhipFrame(float progress, List<Vec3> controlPoints) {
    public WhipFrame {
        if (!Float.isFinite(progress) || progress < 0.0F || progress > 1.0F) {
            throw new IllegalArgumentException("Whip frame progress must be in [0, 1]");
        }
        controlPoints = List.copyOf(Objects.requireNonNull(controlPoints, "controlPoints"));
        if (controlPoints.size() < 2) {
            throw new IllegalArgumentException("Whip frame requires at least two control points");
        }
        if (controlPoints.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("Whip frame control points cannot contain null");
        }
    }
}
