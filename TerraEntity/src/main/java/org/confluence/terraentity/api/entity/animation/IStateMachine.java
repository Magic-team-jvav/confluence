package org.confluence.terraentity.api.entity.animation;

public interface IStateMachine<B,S> {

    void apply(double partialTick, B bone);

    S getState();

    void setState(S boneStates);

}
