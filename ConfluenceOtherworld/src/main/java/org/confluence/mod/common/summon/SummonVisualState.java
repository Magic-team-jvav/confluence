package org.confluence.mod.common.summon;

/// 服务端同步给客户端的通用表现状态。
public record SummonVisualState(
        boolean followingOwner,
        SummonAnimation animation,
        int animationTicks,
        int animationDuration,
        float animationDegrees,
        float scale,
        float scaleY) {
    public static final SummonVisualState DEFAULT = new SummonVisualState(false, SummonAnimation.NONE, 0, 0, 0.0F, 1.0F, 1.0F);

    public SummonVisualState {
        if (animationTicks < 0 || animationDuration < 0) {
            throw new IllegalArgumentException("Summon animation time must not be negative");
        }
        if (!Float.isFinite(animationDegrees) || !Float.isFinite(scale) || !Float.isFinite(scaleY) || scale <= 0.0F || scaleY <= 0.0F) {
            throw new IllegalArgumentException("Summon visual values must be finite and scales must be positive");
        }
    }
}
