package org.confluence.terraentity.client.animation.api.state;

import org.confluence.terraentity.api.entity.animation.IStateMachine;

// 状态接口
public interface BoneState<T, B, C, S, M extends IStateMachine<B, S>> {

    boolean shouldTransition(M state,T animatable, C context);

    void transitionState(M state, T animatable, float partialTick, B bone, C context);

    void updateTransition(M state, T animatable, float partialTick, B bone, C context);

    default void handle(M state, T animatable, float partialTick, B bone, C context){
        if (shouldTransition(state, animatable, context)) {
            transitionState(state, animatable, partialTick, bone, context);
        } else {
            updateTransition(state, animatable, partialTick, bone, context);
        }
    }
}