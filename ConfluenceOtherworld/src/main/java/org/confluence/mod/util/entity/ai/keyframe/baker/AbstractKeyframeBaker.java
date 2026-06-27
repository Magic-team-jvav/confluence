package org.confluence.mod.util.entity.ai.keyframe.baker;

import org.confluence.mod.common.api.entity.animation.IInterpolator;
import org.confluence.mod.common.api.entity.animation.IKeyframeBaker;

/**
 * 使用插值器烘焙关键帧的关键帧烘焙器
 */
public abstract class AbstractKeyframeBaker implements IKeyframeBaker {
    protected IInterpolator interpolator;
    public AbstractKeyframeBaker(IInterpolator interpolator) {
        this.interpolator = interpolator;
    }

    @Override
    public double calculate(double time) {
        if(interpolator == null) return 0;
        return interpolator.cal(time);
    }
}
