package org.confluence.mod.util.entity.ai.keyframe.interpolator;

import com.mojang.serialization.Codec;
import org.confluence.mod.common.api.entity.animation.IInterpolator;
import org.confluence.mod.common.data.codec.TECodecs;

import java.util.function.Supplier;

/**
 * 插值器枚举
 */
public enum InterpolatorEnum {
    /**
     * 线性插值器
     */
    LINEAR(KeyframeLinearInterpolator::new),
    /**
     * 样条曲线插值器
     */
    SPLINES(KeyframeSplineInterpolator::new)

    ;

    public static Codec<InterpolatorEnum> CODEC = TECodecs.createEnumCodec(InterpolatorEnum.class);

    private Supplier<IInterpolator> interpolator;

    InterpolatorEnum(Supplier<IInterpolator> interpolator) {
        this.interpolator = interpolator;
    }

    public IInterpolator getInterpolator() {
        return interpolator.get();
    }
}
