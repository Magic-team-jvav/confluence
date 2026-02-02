package org.confluence.terraentity.entity.animation;

import org.confluence.terraentity.api.entity.animation.IStateMachine;

public abstract class AbstractStateMachine<B,S> implements IStateMachine<B,S> {

    protected S state;
    /**
     * 只需在客户端实例化
     * @param initialState 初始状态
     */
    public AbstractStateMachine(S initialState) {
        this.state = initialState;
    }

    @Override
    public S getState() {
        return state;
    }

    @Override
    public void setState(S boneStates) {
        this.state = boneStates;
    }

}
