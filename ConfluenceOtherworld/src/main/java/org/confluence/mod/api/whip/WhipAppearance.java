package org.confluence.mod.api.whip;

import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.OptionalInt;

/// 一种鞭子的完整曲线外观。
///
/// <p>分段按列表顺序绘制，因此同一根鞭子可以叠加任意数量、任意模式的模型分段。
/// 手柄不属于这里：玩家手中的鞭子物品仍使用自身普通的物品 JSON 模型。曲线颜色使用
/// ARGB 格式；未设置时不绘制额外线条。</p>
public record WhipAppearance(List<WhipSegment> segments, @Nullable Integer lineColor) {
    public WhipAppearance {
        segments = List.copyOf(
                Objects.requireNonNull(
                        segments, "Whip appearance segments must not be null"));
        if (segments.isEmpty() && lineColor == null) {
            throw new IllegalArgumentException(
                    "Whip appearance must contain segments or a curve line");
        }
    }

    public static WhipAppearance segments(WhipSegment... segments) {
        return new WhipAppearance(Arrays.asList(segments), null);
    }

    public static WhipAppearance segmentsAndLine(
            int argb,
            WhipSegment... segments
    ) {
        return new WhipAppearance(Arrays.asList(segments), argb);
    }

    public static WhipAppearance line(int argb) {
        return new WhipAppearance(List.of(), argb);
    }

    public OptionalInt optionalLineColor() {
        return lineColor == null
                ? OptionalInt.empty()
                : OptionalInt.of(lineColor);
    }
}
