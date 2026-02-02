package org.confluence.terraentity.client.animation.api.animator;

import org.confluence.terraentity.api.entity.animation.IStateMachine;
import org.confluence.terraentity.client.animation.api.state.BoneState;

import java.util.Map;

public abstract class AbstractAnimator<T, B, C, S, M extends IStateMachine<B, S>, A extends BoneState<T, B, C,S, M>> implements BoneAnimator<T, B, C,S, M> {

    protected Map<S, A> states;

    protected B bone;
    public AbstractAnimator(B bone) {
        this.bone = bone;
        init();
    }

    /**
     * 初始化状态机和states map
     */
    protected abstract void init();

    public B getBone() {
        return bone;
    }

    /**
     * 添加状态
     * @param state 骨骼状态枚举
     * @param boneState 骨骼状态
     */
    protected void addState(S state, A boneState) {
        this.states.put(state, boneState);
    }


    @Override
    public void updateState(M state, T animatable, float partialTick,  C context) {
        A currentState = this.states.get(state.getState());
        if (currentState != null) {
            currentState.handle(state, animatable, partialTick, bone, context);
        }else{
            state.setState(defaultState());
        }
    }

    protected abstract S defaultState();
    
}
