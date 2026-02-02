package org.confluence.terraentity.api.entity.blur;

import org.confluence.terraentity.entity.blur.MotionBlurManager;

/**
 * 动作模糊的实体
 * @param <C> 模糊渲染器需要的数据类型
 */
public interface IMotionBlurHolder<C extends IMotionBlurContext> {

    /**
     * 是否启用动作模糊，当为true时添加队列
     */
    boolean isMotionBlurEnabled();

    void setMotionBlurEnabled(boolean enabled);

    MotionBlurManager<C> getMotionBlurManager();

}
