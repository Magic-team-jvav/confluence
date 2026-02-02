package org.confluence.terraentity.api.entity.animation;

import org.confluence.terraentity.entity.ai.keyframe.Keyframe;

import java.util.List;

/**
 * 关键帧烘焙器接口
 * <p>当需要很多关键帧点的时候，但是只想通过少量的关键帧来自动插值，就需要烘焙器来补帧</p>
 */
public interface IKeyframeBaker {

    /**
     * 初始化烘焙器
     * @param keyframes 关键帧列表
     * @param position 起始位置
     */
    void init(List<Keyframe> keyframes, int position);

    /**
     * 计算指定时间点的属性值
     * @param time 时间点
     * @return 属性值
     */
    double calculate(double time);

    List<Keyframe> getBakedKeyframes();

}
