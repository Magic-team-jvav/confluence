package org.confluence.terraentity.client.animation.api.animator;

import org.confluence.terraentity.api.entity.animation.IStateMachine;

/**
 * 骨骼硬编码动画控制器
 * @param <T> 实体类型
 * @param <B> 骨骼类型
 * @param <C> 动画控制器上下文类型
 * @param <S> 状态机状态类型
 */
public interface BoneAnimator<T, B, C, S, M extends IStateMachine<B,S>> {
    /**
     * 更新骨骼状态
     * @param stateMachine 状态机
     */
    void updateState(M stateMachine, T animatable, float partialTick, C context);
}