package org.confluence.mod.util.entity.ai.keyframe.interpolator;

import org.confluence.mod.common.api.entity.animation.IInterpolator;
import org.confluence.mod.util.entity.ai.keyframe.Keyframe;

import java.util.List;

import static org.confluence.mod.util.entity.ai.keyframe.FrameUtil.linearInterpolateBetween;

/**
 * 线性插值
 */
public class KeyframeLinearInterpolator implements IInterpolator {

    Keyframe kf0;
    Keyframe kf1;

    @Override
    public void init(List<Keyframe> keyframes, int position) {
        kf0 = this.getFirst(keyframes, position);
        kf1 = this.getSecond(keyframes, position);
    }

    @Override
    public double cal(double t) {
        return linearInterpolateBetween(kf0, kf1, t);
    }
}
