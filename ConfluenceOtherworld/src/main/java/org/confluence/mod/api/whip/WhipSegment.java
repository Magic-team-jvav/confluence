package org.confluence.mod.api.whip;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

/// 鞭子曲线上的一组模型分段。
///
/// <p>固定间距模式中的数值使用像素，十六像素等于一个方块；曲线伸长时，已有分段的
/// 间距不变，只会逐渐增加分段数量。固定数量模式中的数值就是分段总数，曲线伸长时
/// 数量不变，各段之间的距离会被重新平均。两种模式都可以为最后一个位置指定独立的
/// 鞭梢模型。</p>
///
/// @param model    普通分段使用的物品 JSON 模型位置
/// @param mode     分段沿曲线的排列方式
/// @param value    像素间距或固定分段数，具体含义由 {@code mode} 决定
/// @param tipModel 可选的鞭梢物品 JSON 模型位置
public record WhipSegment(ResourceLocation model, Mode mode, int value,
                          @Nullable ResourceLocation tipModel) {
    public WhipSegment {
        model = Objects.requireNonNull(model, "Whip segment model must not be null");
        mode = Objects.requireNonNull(mode, "Whip segment mode must not be null");
        if (value <= 0) {
            throw new IllegalArgumentException(
                    "Whip segment spacing or count must be positive");
        }
    }

    public static WhipSegment fixedSpacing(
            ResourceLocation model,
            int spacingPixels
    ) {
        return new WhipSegment(model, Mode.FIXED_SPACING, spacingPixels, null);
    }

    public static WhipSegment fixedSpacing(
            ResourceLocation model,
            int spacingPixels,
            ResourceLocation tipModel
    ) {
        return new WhipSegment(
                model,
                Mode.FIXED_SPACING,
                spacingPixels,
                Objects.requireNonNull(
                        tipModel, "Whip tip model must not be null"));
    }

    public static WhipSegment fixedCount(
            ResourceLocation model,
            int segmentCount
    ) {
        return new WhipSegment(model, Mode.FIXED_COUNT, segmentCount, null);
    }

    public static WhipSegment fixedCount(
            ResourceLocation model,
            int segmentCount,
            ResourceLocation tipModel
    ) {
        return new WhipSegment(
                model,
                Mode.FIXED_COUNT,
                segmentCount,
                Objects.requireNonNull(
                        tipModel, "Whip tip model must not be null"));
    }

    public Optional<ResourceLocation> optionalTipModel() {
        return Optional.ofNullable(tipModel);
    }

    public enum Mode {
        FIXED_SPACING,
        FIXED_COUNT
    }
}
