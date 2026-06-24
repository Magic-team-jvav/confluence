package org.confluence.mod.api.entity.animation;

public interface IStateMachine<B, S> {
    void apply(double partialTick, B bone);

    S getState();

    void setState(S boneStates);
}
