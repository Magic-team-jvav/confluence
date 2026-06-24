package org.confluence.mod.common.entity.animation;

/// 骷髅王之手多骨骼状态机
///
/// @param <S> 状态类型
public class MultiBoneStateMachine<S> extends AbstractStateMachine<MultiBone, S> {
    public BoneStateMachine<S> hand;
    public BoneStateMachine<S> arm1;
    public BoneStateMachine<S> arm2;

    public MultiBoneStateMachine(S initialState) {
        super(initialState);
        hand = new BoneStateMachine<>(initialState);
        arm1 = new BoneStateMachine<>(initialState);
        arm2 = new BoneStateMachine<>(initialState);
    }

    @Override
    public void apply(double partialTick, MultiBone bone) {
        hand.apply(partialTick, bone.hand);
        arm1.apply(partialTick, bone.arm1);
        arm2.apply(partialTick, bone.arm2);
    }

    @Override
    public void setState(S boneStates) {
        if (this.state != boneStates) {
            hand.setState(boneStates);
            arm1.setState(boneStates);
            arm2.setState(boneStates);
        }
        this.state = boneStates;
    }
}
