package org.confluence.mod.api.whip.curve;

import net.minecraft.world.phys.Vec3;

import java.util.List;

/// 本体提供的鞭子轨迹预设。
///
/// 普通挥鞭保留去程，下抽后沿下方弧线完整收回。
public final class WhipCurves {
    public static final float SNAP_PROGRESS = 0.60F;
    public static final float SWEEP_SOUND_PROGRESS = 0.3F;
    private static final double RETURN_START = 0.68;
    private static final WhipCurve OUTBOUND = new KeyframedWhipCurve(List.of(
            new WhipFrame(0.0F, List.of(Vec3.ZERO, Vec3.ZERO, Vec3.ZERO)),
            new WhipFrame(0.25F, List.of(Vec3.ZERO, new Vec3(-1.0 / 16.0, 0.0, 0.0), new Vec3(-4.0 / 16.0, 3.0 / 16.0, 0.0))),
            new WhipFrame(0.50F, List.of(Vec3.ZERO, new Vec3(-4.0 / 16.0, 0.0, 0.0), new Vec3(-14.0 / 16.0, 3.0 / 16.0, 0.0))),
            // 水平伸出保持原速，只让鞭梢先蓄势、快速下抽，再缓冲收势。
            new WhipFrame(0.58F, List.of(Vec3.ZERO, new Vec3(-4.32 / 16.0, 0.0, 0.0), new Vec3(-14.64 / 16.0, 2.6 / 16.0, 0.0))),
            new WhipFrame(0.68F, List.of(Vec3.ZERO, new Vec3(-4.72 / 16.0, 0.0, 0.0), new Vec3(-15.44 / 16.0, -3.5 / 16.0, 0.0))),
            new WhipFrame(0.74F, List.of(Vec3.ZERO, new Vec3(-4.96 / 16.0, 0.0, 0.0), new Vec3(-15.92 / 16.0, -4.0 / 16.0, 0.0))),
            new WhipFrame(0.75F, List.of(Vec3.ZERO, new Vec3(-5.0 / 16.0, 0.0, 0.0), new Vec3(-16.0 / 16.0, -4.0 / 16.0, 0.0))),
            new WhipFrame(1.0F, List.of(Vec3.ZERO, Vec3.ZERO, Vec3.ZERO))
    ));
    public static final WhipCurve DEFAULT = new RetractingWhipCurve(OUTBOUND, RETURN_START);

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
