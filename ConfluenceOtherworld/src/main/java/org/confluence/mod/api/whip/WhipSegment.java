package org.confluence.mod.api.whip;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

// FIXED_SPACING 的 value 是像素间距，FIXED_COUNT 的 value 是分段数。
// tipModel 可为 null；每个 WhipSegment 都是一层，可在 WhipAppearance 中任意组合。
public record WhipSegment(ResourceLocation model, Mode mode, int value,
                          @Nullable ResourceLocation tipModel) {
    public WhipSegment {
        model = Objects.requireNonNull(model, "Whip segment model must not be null");
        mode = Objects.requireNonNull(mode, "Whip segment mode must not be null");
        if (value <= 0) {
            throw new IllegalArgumentException("Whip segment spacing or count must be positive");
        }
    }

    public static WhipSegment fixedSpacing(ResourceLocation model, int spacingPixels) {
        return new WhipSegment(model, Mode.FIXED_SPACING, spacingPixels, null);
    }

    public static WhipSegment fixedSpacing(ResourceLocation model, int spacingPixels, ResourceLocation tipModel) {
        return new WhipSegment(model, Mode.FIXED_SPACING, spacingPixels, Objects.requireNonNull(tipModel, "Whip tip model must not be null"));
    }

    public static WhipSegment fixedCount(ResourceLocation model, int segmentCount) {
        return new WhipSegment(model, Mode.FIXED_COUNT, segmentCount, null);
    }

    public static WhipSegment fixedCount(ResourceLocation model, int segmentCount, ResourceLocation tipModel) {
        return new WhipSegment(model, Mode.FIXED_COUNT, segmentCount, Objects.requireNonNull(tipModel, "Whip tip model must not be null"));
    }

    public enum Mode {
        FIXED_SPACING,
        FIXED_COUNT
    }
}
