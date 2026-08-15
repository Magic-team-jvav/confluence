package org.confluence.mod.common.entity.monster;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.data.entity.CreatureDefinition;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.composite.SelectorNode;
import org.confluence.mod.common.entity.ai.bt.composite.SequenceNode;
import org.confluence.mod.common.entity.ai.bt.condition.HasTargetCondition;
import org.confluence.mod.common.entity.ai.bt.leaf.FlyWanderAction;
import org.confluence.mod.common.entity.ai.bt.leaf.SteeringDashAction;
import org.confluence.mod.common.init.ModSoundEvents;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

/**
 * 仅通过参数区分冲撞节奏的简单飞行敌怪。
 *
 * <p>滴滴怪、飞鱼和游荡眼球鱼的行为结构相同，区别仅是惯性、最高速度、转向锥和
 * 掠过后的滑行时间，因此保留为同一个实体类并使用不可变冲撞参数。真正具有额外状态或
 * 特殊攻击的生物仍应使用独立子类，不能继续向本类堆叠类型判断。</p>
 */
public class SimpleFlyMonster extends BaseFlyingMonster {
    private static final RawAnimation FLY = RawAnimation.begin().thenLoop("fly");

    private final DashProfile dashProfile;
    private final double wanderSpeed;
    private final boolean playFlyAnimation;
    private final SoundProfile soundProfile;

    public SimpleFlyMonster(
            EntityType<? extends SimpleFlyMonster> type,
            Level level,
            double chargeSpeed,
            double wanderSpeed) {
        this(type, level, DashProfile.standard(chargeSpeed), wanderSpeed, false,
                SoundProfile.ROUTINE);
    }

    public SimpleFlyMonster(
            EntityType<? extends SimpleFlyMonster> type,
            Level level,
            DashProfile dashProfile,
            double wanderSpeed,
            boolean playFlyAnimation,
            SoundProfile soundProfile) {
        super(type, level);
        this.dashProfile = dashProfile;
        this.wanderSpeed = wanderSpeed;
        this.playFlyAnimation = playFlyAnimation;
        this.soundProfile = soundProfile;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return BaseFlyingMonster.createFlyingAttributes();
    }

    @Override
    protected BTRoot createBT() {
        SimpleFlyMonster self = this;
        CreatureDefinition.BehaviorOverrides behavior = creatureDefinition().behavior();
        DashProfile profile = dashProfile.withMaxSpeed(
                behavior.chargeSpeedOr(dashProfile.maxSpeed()));
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
                        new FlyWanderAction(
                                self,
                                behavior.wanderSpeedOr(wanderSpeed),
                                behavior.wanderRadiusOr(8)));
            }
        };
    }

    /**
     * 普通转向飞行怪使用未扩张的实体包围盒。只有 1.21 明确声明了特殊范围
     * 或检测周期的实体才覆盖这三个方法，避免在注册点追加难以辨认的布尔值和数字参数。
     */
    @Override
    protected double contactAttackInflation() {
        return 0.0;
    }

    @Override
    protected int contactDetectionInterval() {
        return 10;
    }

    @Override
    protected int contactAttackInterval() {
        return 20;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        if (playFlyAnimation) {
            controllers.add(new AnimationController<>(
                    this, "Fly", 0, state -> state.setAndContinue(FLY)));
        }
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return soundProfile == SoundProfile.DRIPPLER
                ? ModSoundEvents.DRIPPLER_HURT.get()
                : super.getHurtSound(source);
    }

    @Override
    protected SoundEvent getDeathSound() {
        return soundProfile == SoundProfile.DRIPPLER
                ? ModSoundEvents.DRIPPLER_DEATH.get()
                : super.getDeathSound();
    }

    /**
     * 一组只描述转向冲撞物理的数据。
     */
    public record DashProfile(
            double friction,
            double maxSpeed,
            double acceleration,
            double turnSpeedDegrees,
            double triggerAngleDegrees,
            double steeringAngleDegrees,
            int coastTicks) {

        public DashProfile {
            if (friction < 0.0 || friction > 1.0) {
                throw new IllegalArgumentException("Dash friction must be within [0, 1]");
            }
            if (maxSpeed <= 0.0 || acceleration <= 0.0) {
                throw new IllegalArgumentException(
                        "Dash speed and acceleration must be positive");
            }
            if (turnSpeedDegrees <= 0.0
                    || triggerAngleDegrees <= 0.0
                    || steeringAngleDegrees <= 0.0
                    || coastTicks < 0) {
                throw new IllegalArgumentException(
                        "Dash angles must be positive and coast time cannot be negative");
            }
        }

        public static DashProfile standard(double maxSpeed) {
            return new DashProfile(
                    0.95, maxSpeed, 0.02, 10.0, 10.0, 45.0, 15);
        }

        DashProfile withMaxSpeed(double value) {
            return value == maxSpeed
                    ? this
                    : new DashProfile(
                    friction,
                    value,
                    acceleration,
                    turnSpeedDegrees,
                    triggerAngleDegrees,
                    steeringAngleDegrees,
                    coastTicks);
        }
    }

    public enum SoundProfile {
        ROUTINE,
        DRIPPLER
    }
}
