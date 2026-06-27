package org.confluence.mod.util.entity.ai.keyframe.baker;

import org.confluence.mod.common.api.entity.animation.IInterpolator;
import org.confluence.mod.util.entity.ai.keyframe.Keyframe;

import java.util.ArrayList;
import java.util.List;

/**
 * 分段贝塞尔曲线烘焙器
 */
public class PiecewiseBezierBaker extends AbstractKeyframeBaker {

    public List<Keyframe> bakedKeyframes = new ArrayList<>();

    public PiecewiseBezierBaker(IInterpolator interpolator) {
        super(interpolator);
    }

    @Override
    public void init(List<Keyframe> keyframes, int position){
        if(position < 1 || position > keyframes.size() - 1) return;
        Keyframe kf0 = keyframes.get(Math.max(0, position - 2));
        Keyframe kf1 = keyframes.get(Math.max(0, position - 1));
        Keyframe kf2 = keyframes.get(Math.min(keyframes.size() - 1, position));
        Keyframe kf3 = keyframes.get(Math.min(keyframes.size() - 1, position + 1));
        bakedKeyframes.add(kf1);

        double t0 = kf0.time;
        double t1 = kf1.time;
        double t2 = kf2.time;
        double t3 = kf3.time;

        double v0 = kf0.value;
        double v1 = kf1.value;
        double v2 = kf2.value;
        double v3 = kf3.value;

        // 斜率
        double k1 = kf1.k1;
        double k2 = kf2.k0;
        // 控制点
        double f1 = Math.sqrt(k1 * k1 + 1);
        double c1x = t1 + kf1.tension0 * 1 / f1;
        double c1y = v1 + kf1.tension0 * k1 / f1;

        double f2 = Math.sqrt(k2 * k2 + 1);
        double c2x = t2 - kf2.tension1 * 1 / f2;
        double c2y = v2 - kf2.tension1 * k2 / f2;

        int count = 10;
        double dt = 1.0 / count;

        double t = dt;

        for(int i=0;i<count-1;i++){
            double u = 1 - t;
            double tt = t * t;
            double uu = u * u;
            double uuu = uu * u;
            double ttt = tt * t;

            double x = uuu * t1 + 3 * uu * t * c1x + 3 * u * tt * c2x + ttt * t2;
            double y = uuu * v1 + 3 * uu * t * c1y + 3 * u * tt * c2y + ttt * v2;
            t += dt;

            bakedKeyframes.add(new Keyframe(x, y));
        }
        bakedKeyframes.add(kf2);
        interpolator.init(bakedKeyframes, position);
    }

    @Override
    public List<Keyframe> getBakedKeyframes() {
        return bakedKeyframes;
    }


}
