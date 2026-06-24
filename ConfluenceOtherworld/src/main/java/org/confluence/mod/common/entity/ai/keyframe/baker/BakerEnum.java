package org.confluence.mod.common.entity.ai.keyframe.baker;

import com.mojang.serialization.Codec;
import org.confluence.mod.common.api.entity.animation.IKeyframeBaker;
import org.confluence.mod.common.data.codec.TECodecs;
import org.confluence.mod.common.entity.ai.keyframe.interpolator.KeyframeLinearInterpolator;
import org.confluence.mod.common.entity.ai.keyframe.interpolator.KeyframeSplineInterpolator;

import java.util.function.Supplier;

public enum BakerEnum {
    LINER(()->new LinearBaker()),
    PIECEWISE_BEZIER_LINEAR_INTERPOLATOR(()->new PiecewiseBezierBaker(new KeyframeLinearInterpolator())),
    PIECEWISE_BEZIER_SPLINE2(()->new PiecewiseBezierBaker(new KeyframeSplineInterpolator()));

//    public static Codec<BakerEnum> CODEC = Codec.STRING.xmap(
//            name -> BakerEnum.valueOf(name.toUpperCase()),
//            baker -> baker.name().toLowerCase(Locale.ROOT)
//    );

    public static Codec<BakerEnum> CODEC = TECodecs.createEnumCodec(BakerEnum.class);

    final Supplier<IKeyframeBaker> baker;
    BakerEnum(Supplier<IKeyframeBaker> baker) {
        this.baker = baker;
    }
    public IKeyframeBaker getBaker() {
        return baker.get();
    }

}
