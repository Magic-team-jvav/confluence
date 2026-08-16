package org.confluence.mod.api.whip.curve;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/// 使用线性插值连接相邻时间帧的确定性轨迹。
///
/// <p>时间插值只负责让相同序号的控制点平滑移动；鞭身空间曲线由
/// {@link WhipCurveSampler} 统一生成。两层插值分离后，服务端碰撞与客户端渲染可以
/// 使用完全相同的输入和算法。时间推进沿用 1.21 侧的线性关键帧行为，避免代码重写
/// 改变原有挥动节奏。</p>
public final class KeyframedWhipCurve implements WhipCurve {
    private final List<WhipFrame> frames;

    public KeyframedWhipCurve(List<WhipFrame> frames) {
        Objects.requireNonNull(frames, "frames");
        if (frames.size() < 2) {
            throw new IllegalArgumentException(
                    "Whip curve requires at least two frames"
            );
        }

        ArrayList<WhipFrame> sorted = new ArrayList<>(frames);
        sorted.sort(Comparator.comparingDouble(WhipFrame::progress));
        int pointCount = sorted.get(0).controlPoints().size();
        float previousProgress = -1.0F;
        for (WhipFrame frame : sorted) {
            if (frame.controlPoints().size() != pointCount) {
                throw new IllegalArgumentException(
                        "Every whip frame must contain the same number of control points"
                );
            }
            if (frame.progress() <= previousProgress) {
                throw new IllegalArgumentException(
                        "Whip frame progress values must be unique"
                );
            }
            previousProgress = frame.progress();
        }
        if (sorted.get(0).progress() != 0.0F
                || sorted.get(sorted.size() - 1).progress() != 1.0F) {
            throw new IllegalArgumentException(
                    "Whip curve must start at 0 and end at 1"
            );
        }
        this.frames = List.copyOf(sorted);
    }

    @Override
    public List<Vec3> controlPoints(double progress) {
        double clamped = Mth.clamp(progress, 0.0, 1.0);
        if (clamped <= 0.0) {
            return frames.get(0).controlPoints();
        }
        if (clamped >= 1.0) {
            return frames.get(frames.size() - 1).controlPoints();
        }

        WhipFrame right = frames.get(1);
        WhipFrame left = frames.get(0);
        for (int index = 1; index < frames.size(); index++) {
            right = frames.get(index);
            left = frames.get(index - 1);
            if (clamped <= right.progress()) {
                break;
            }
        }

        double local = (clamped - left.progress())
                / (right.progress() - left.progress());
        ArrayList<Vec3> result =
                new ArrayList<>(left.controlPoints().size());
        for (int index = 0; index < left.controlPoints().size(); index++) {
            result.add(left.controlPoints().get(index).lerp(
                    right.controlPoints().get(index),
                    local
            ));
        }
        return List.copyOf(result);
    }

    public List<WhipFrame> frames() {
        return frames;
    }
}
