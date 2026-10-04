package org.confluence.mod.api.whip;

import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

// 分段按列表顺序叠加；手柄仍使用物品自己的 JSON 模型。
// lineColor 使用 ARGB，null 表示不绘制曲线线条。
public record WhipAppearance(List<WhipSegment> segments, @Nullable Integer lineColor) {
    public WhipAppearance {
        if (segments.isEmpty() && lineColor == null) {
            throw new IllegalArgumentException("Whip appearance must contain segments or a curve line");
        }
    }

    public static WhipAppearance segments(WhipSegment... segments) {
        return new WhipAppearance(Arrays.asList(segments), null);
    }

    public static WhipAppearance segmentsAndLine(int argb, WhipSegment... segments) {
        return new WhipAppearance(Arrays.asList(segments), argb);
    }

    public static WhipAppearance line(int argb) {
        return new WhipAppearance(List.of(), argb);
    }
}
