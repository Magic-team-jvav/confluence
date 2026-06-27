package org.confluence.mod.api.entity.animation;

import org.confluence.mod.util.entity.ai.keyframe.Keyframe;

import java.util.List;

/// ## 插值器接口
public interface IInterpolator {
    void init(List<Keyframe> keyframes, int position);

    double cal(double t);

    default Keyframe getFirst(List<Keyframe> keyframes, int position) {
        return keyframes.get(position - 1);
    }

    default Keyframe getSecond(List<Keyframe> keyframes, int position) {
        return keyframes.get(position);
    }
}
