package org.confluence.mod.api.entity.animation;

import org.confluence.mod.common.entity.animation.BoneStateMachine;

/// 手臂动画控制信息传输接口
public interface IUseItemAnimatable<S> {
    /// 是否在拉弩。对于不需要使用弩的实体应该返回false
    boolean isChargingCrossbow();

    /// 获取拉弩的持续时间
    int getChargingTicks();

    /// 获取左手的动画状态机
    BoneStateMachine<S> getLeftArmBoneStateMachine();

    /// 获取右手的动画状态机
    BoneStateMachine<S> getRightArmBoneStateMachine();

    /// 当处于躺下时，渲染躺下状态
    boolean isLieDown();
}
