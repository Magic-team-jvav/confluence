package org.confluence.mod.common.entity.ai.keyframe.baker;

import org.confluence.mod.common.api.entity.animation.IKeyframeBaker;
import org.confluence.mod.common.entity.ai.keyframe.Keyframe;

import java.util.ArrayList;
import java.util.List;

public class LinearBaker implements IKeyframeBaker {
    double t1;
    double t2;
    double v1;
    double v2;
    List<Keyframe> bakedKeyframes;

    public LinearBaker() {
    }

    @Override
    public void init(List<Keyframe> keyframes, int position){
        this.bakedKeyframes = new ArrayList<>(keyframes);
        if(position < 1 || position > keyframes.size() - 1) return;


        Keyframe kf1 = keyframes.get(Math.max(0, position - 1));
        Keyframe kf2 = keyframes.get(Math.min(keyframes.size() - 1, position));

        t1 = kf1.time;
        t2 = kf2.time;

        v1 = kf1.value;
        v2 = kf2.value;
    }
    @Override
    public double calculate(double time) {
        return v1 + (v2 - v1) * (time - t1) / (t2 - t1);
    }

    @Override
    public List<Keyframe> getBakedKeyframes() {
        return bakedKeyframes;
    }
}
