package org.confluence.mod.util.entity.ai.keyframe.interpolator;

import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.confluence.mod.common.api.entity.animation.IInterpolator;
import org.confluence.mod.util.entity.ai.keyframe.Keyframe;

import java.util.List;

/**
 * 样条插值器
 */
public class KeyframeSplineInterpolator implements IInterpolator {

    PolynomialSplineFunction spline;

    @Override
    public void init(List<Keyframe> keyframes, int position) {
        SplineInterpolator splineInterpolator = new SplineInterpolator();
        double[] x = new double[keyframes.size()];
        double[] y = new double[keyframes.size()];
        for (int i = 0; i < keyframes.size(); i++) {
            x[i] = keyframes.get(i).time;
            y[i] = keyframes.get(i).value;
        }
        spline = splineInterpolator.interpolate(x, y);
    }

    @Override
    public double cal(double t) {
        return spline.value(t);
    }

}
