package org.confluence.mod.api.whip.curve;

import net.minecraft.world.phys.Vec3;

import java.util.List;

/// 本体提供的鞭子轨迹预设。
///
/// 这里保留 1.21 侧的原始局部关键点，只把 1.21 使用的 16 像素单位换算成世界单位。
/// 轨迹外层可以继续使用新的分段渲染 API，但挥动形状本身不能因为重构而改变。
public final class WhipCurves {
    public static final WhipCurve DEFAULT = new KeyframedWhipCurve(List.of(
            new WhipFrame(0.0F, List.of(Vec3.ZERO, Vec3.ZERO, Vec3.ZERO)),
            new WhipFrame(0.25F, List.of(Vec3.ZERO, new Vec3(-1.0 / 16.0, 3.0 / 16.0, 0.0), new Vec3(-4.0 / 16.0, 3.0 / 16.0, 0.0))),
            new WhipFrame(0.50F, List.of(Vec3.ZERO, new Vec3(-4.0 / 16.0, 0.0, 0.0), new Vec3(-14.0 / 16.0, 3.0 / 16.0, 0.0))),
            new WhipFrame(0.75F, List.of(Vec3.ZERO, new Vec3(-5.0 / 16.0, -4.0 / 16.0, 0.0), new Vec3(-16.0 / 16.0, -4.0 / 16.0, 0.0))),
            new WhipFrame(1.0F, List.of(Vec3.ZERO, Vec3.ZERO, Vec3.ZERO))
    ));

    /// 1.21“横扫之鞭”附魔触发时使用的宽幅挥动轨迹。
    public static final WhipCurve SWEEP = new KeyframedWhipCurve(List.of(
            new WhipFrame(0.0F, List.of(Vec3.ZERO, Vec3.ZERO, Vec3.ZERO)),
            new WhipFrame(0.1667F, List.of(Vec3.ZERO, new Vec3(-1.0 / 16.0, 0.0, 2.0 / 16.0), new Vec3(-3.0 / 16.0, 1.0 / 16.0, 4.0 / 16.0))),
            new WhipFrame(0.375F, List.of(Vec3.ZERO, new Vec3(-2.0 / 16.0, 1.0 / 16.0, 3.0 / 16.0), new Vec3(-9.0 / 16.0, 2.0 / 16.0, 4.0 / 16.0))),
            new WhipFrame(0.5417F, List.of(Vec3.ZERO, new Vec3(-5.0 / 16.0, 1.0 / 16.0, 1.0 / 16.0), new Vec3(-15.0 / 16.0, 1.0 / 16.0, 0.0))),
            new WhipFrame(0.7083F, List.of(Vec3.ZERO, new Vec3(-3.0 / 16.0, -1.0 / 16.0, -3.0 / 16.0), new Vec3(-9.0 / 16.0, 0.0, -4.0 / 16.0))),
            new WhipFrame(0.875F, List.of(Vec3.ZERO, new Vec3(-1.0 / 16.0, 0.0, -1.0 / 16.0), new Vec3(-3.0 / 16.0, 0.0, -4.0 / 16.0))),
            new WhipFrame(1.0F, List.of(Vec3.ZERO, Vec3.ZERO, Vec3.ZERO))
    ));

    private WhipCurves() {}
}
