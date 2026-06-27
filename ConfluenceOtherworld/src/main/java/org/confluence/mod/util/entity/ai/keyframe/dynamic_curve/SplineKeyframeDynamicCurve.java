package org.confluence.mod.util.entity.ai.keyframe.dynamic_curve;

import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.api.entity.animation.IInterpolator;
import org.confluence.mod.common.api.entity.animation.IKeyframeAnimation;
import org.confluence.mod.util.entity.ai.keyframe.interpolator.InterpolatorEnum;

import java.util.ArrayList;
import java.util.List;

import static org.confluence.mod.util.entity.ai.keyframe.FrameUtil.getInterpolatedPoints;

/**
 * 将多组离散的关键帧<b> 顺序插值 </b>，再将插值的离散点之间进行插值，生成一个动态曲线。
 * @param <T> 三维关键帧动画
 */
public class SplineKeyframeDynamicCurve<T extends IKeyframeAnimation<Vec3>> {
    List<T> keyframeAnimations;

    IInterpolator interpolator;

    public double length;

    /**
     * 构造函数
     * @param keyframeAnimations 离散的关键帧动画列表
     */
    public SplineKeyframeDynamicCurve(List<T> keyframeAnimations) {
        this.keyframeAnimations = keyframeAnimations;
        interpolator = InterpolatorEnum.SPLINES.getInterpolator();
        keyframeAnimations.stream().forEach(kfa -> {
            if(kfa.getLength() > length){
                length = kfa.getLength();
            }
        });
    }

    /**
     * 计算离散点位置插值的所有位置
     * @param time 时间
     * @param numSteps 每两个点之间插值点数量
     * @return 插值后所有点
     */
    public List<Vec3> calculate(double time, int numSteps) {
        List<Vec3> points = new ArrayList<>();
        for (T kfa : keyframeAnimations){
            Vec3 pos = kfa.cal(time);
            points.add(pos);
        }
        List<Vec3> interpolatedPoints = getInterpolatedPoints(points, numSteps);
        return interpolatedPoints;
    }


}
