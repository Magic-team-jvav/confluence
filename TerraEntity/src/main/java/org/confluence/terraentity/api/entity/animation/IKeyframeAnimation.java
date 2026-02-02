package org.confluence.terraentity.api.entity.animation;

/**
 * 一组关键帧确定一个值随时间的变换
 * @param <V> 值类型
 */
public interface IKeyframeAnimation<V> {

    V cal(double t);

    double getLength();

    double getEndTime();

}
