package org.confluence.mod.common.entity.monster;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathType;
import org.confluence.mod.common.data.map.CreatureDefinition;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.composite.SelectorNode;
import org.confluence.mod.common.entity.ai.bt.composite.SequenceNode;
import org.confluence.mod.common.entity.ai.bt.condition.HasTargetCondition;
import org.confluence.mod.common.entity.ai.bt.leaf.LookForwardWanderFlyAction;
import org.confluence.mod.common.entity.ai.bt.leaf.SteeringDashAction;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;

/// 仅通过参数区分冲撞节奏的简单飞行敌怪。
///
/// 使用本类的生物共享惯性转向、冲刺、掠过滑行和近身后撤的结构，仅由
/// 不可变冲撞参数区分节奏。真正具有额外状态或
/// 特殊攻击的生物仍应使用独立子类，不能继续向本类堆叠类型判断。
public class SimpleFlyMonster extends BaseFlyingMonster {
    private static final RawAnimation FLY = RawAnimation.begin().thenLoop("fly");

    private final DashProfile dashProfile;
    private final double wanderSpeed;
    private final boolean playFlyAnimation;

    public SimpleFlyMonster(EntityType<? extends SimpleFlyMonster> type, Level level, double chargeSpeed, double wanderSpeed) {
        this(type, level, DashProfile.standard(chargeSpeed), wanderSpeed, false);
    }

    public SimpleFlyMonster(EntityType<? extends SimpleFlyMonster> type, Level level, DashProfile dashProfile, double wanderSpeed, boolean playFlyAnimation) {
        super(type, level);
        if (wanderSpeed <= 0.0) {
            throw new IllegalArgumentException("Wander speed must be positive");
        }
        this.dashProfile = dashProfile;
        this.wanderSpeed = wanderSpeed;
        this.playFlyAnimation = playFlyAnimation;
        setDiscardFriction(true);
        setPathfindingMalus(PathType.WATER, 0.0F);
    }

    @Override
    protected boolean mustSeePlayerTarget() {
        return true;
    }

    @Override
    protected BTRoot createBT() {
        SimpleFlyMonster self = this;
        CreatureDefinition.BehaviorOverrides behavior = creatureDefinition().behavior();
        DashProfile profile = dashProfile.withMaxSpeed(behavior.chargeSpeedOr(dashProfile.maxSpeed()));
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return SelectorNode.of(
                        SequenceNode.of(
                                new HasTargetCondition(self),
                                new SteeringDashAction(
                                        self,
                                        profile.friction(),
                                        profile.maxSpeed(),
                                        profile.acceleration(),
                                        profile.turnSpeedDegrees(),
                                        profile.triggerAngleDegrees(),
                                        profile.steeringAngleDegrees(),
                                        profile.coastTicks())),
                        new LookForwardWanderFlyAction(self, behavior.wanderSpeedOr(wanderSpeed), 0.0F));
            }
        };
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        if (playFlyAnimation) {
            controllers.add(new AnimationController<>(this, "Fly", 0, state -> state.setAndContinue(FLY)));
        }
    }

    /// 一组只描述转向冲撞物理的数据。
    public record DashProfile(double friction, double maxSpeed, double acceleration,
                              double turnSpeedDegrees, double triggerAngleDegrees,
                              double steeringAngleDegrees, int coastTicks) {

        public DashProfile {
            if (friction < 0.0 || friction > 1.0) {
                throw new IllegalArgumentException("Dash friction must be within [0, 1]");
            }
            if (maxSpeed <= 0.0 || acceleration <= 0.0) {
                throw new IllegalArgumentException("Dash speed and acceleration must be positive");
            }
            if (turnSpeedDegrees <= 0.0
                    || triggerAngleDegrees <= 0.0 || triggerAngleDegrees > 180.0
                    || steeringAngleDegrees <= 0.0 || steeringAngleDegrees > 180.0
                    || coastTicks < 0) {
                throw new IllegalArgumentException("Dash angles must be finite and valid; coast time cannot be negative");
            }
        }

        public static DashProfile standard(double maxSpeed) {
            return new DashProfile(0.95, maxSpeed, 0.01, 10.0, 10.0, 10.0, 15);
        }

        DashProfile withMaxSpeed(double value) {
            return value == maxSpeed
                    ? this
                    : new DashProfile(friction, value, acceleration, turnSpeedDegrees, triggerAngleDegrees, steeringAngleDegrees, coastTicks);
        }
    }

}
