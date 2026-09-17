package org.confluence.mod.common.entity.monster;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.data.map.CreatureDefinition;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.composite.SelectorNode;
import org.confluence.mod.common.entity.ai.bt.composite.SequenceNode;
import org.confluence.mod.common.entity.ai.bt.condition.HasTargetCondition;
import org.confluence.mod.common.entity.ai.bt.leaf.*;
import org.confluence.mod.common.entity.ai.goal.EnemyOpenDoorGoal;
import org.confluence.mod.common.gameevent.BloodMoonGameEvent;
import org.confluence.mod.common.init.ModSoundEvents;
import org.mesdag.portlib.wrapper.world.entity.ai.attributes.PortAttributeModifier;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

/// 通用陆行近战怪物，负责追击、近战、越障跃击和空闲漫游。
///
/// 少数泰拉瑞亚怪物在发现目标后会额外加速。该差异由构造参数声明，
/// 公共实现统一管理瞬时属性修饰符和疾跑状态，防止每种僵尸都复制一套
/// 容易发生永久叠加的属性代码。
public class BaseWarriorMonster extends BaseMonster {
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("move.walk");
    private static final RawAnimation RUN = RawAnimation.begin().thenLoop("move.run");
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("misc.idle");
    private static final RawAnimation ATTACK = RawAnimation.begin().thenLoop("attack.strike");
    private final double pursuitSpeedBonus;
    private final AttributeModifier pursuitSpeedModifier;
    private final double meleeSpeed;
    private final boolean ignoreLightPathCost;
    private final LandAnimationProfile animationProfile;
    private final LandSoundProfile soundProfile;
    private final DoorBehavior doorBehavior;
    private boolean doorNavigationEnabled;

    public BaseWarriorMonster(EntityType<? extends BaseWarriorMonster> type, Level level) {
        this(type, level, 0.0, LandAnimationProfile.NONE, LandSoundProfile.ROUTINE);
    }

    /// 创建具有目标追击加速的陆行怪物。
    ///
    /// @param pursuitSpeedBonus 发现有效目标后附加的移动速度；零表示不启用
    public BaseWarriorMonster(EntityType<? extends BaseWarriorMonster> type, Level level, double pursuitSpeedBonus) {
        this(type, level, pursuitSpeedBonus, LandAnimationProfile.NONE, LandSoundProfile.ROUTINE);
    }

    /// 创建使用指定基础移动动画的陆行怪物。
    ///
    /// 基础类不能默认播放走路和待机动画，因为冲锋怪、龙虾等子类使用的是另一套
    /// 动画键。由注册实体明确选择资源实际支持的档案，既保留通用控制器，也不会在
    /// 客户端持续请求不存在的动画。
    ///
    /// @param pursuitSpeedBonus 发现有效目标后附加的移动速度；零表示不启用
    /// @param animationProfile  该实体资源支持的基础移动动画
    public BaseWarriorMonster(EntityType<? extends BaseWarriorMonster> type, Level level, double pursuitSpeedBonus, LandAnimationProfile animationProfile) {
        this(type, level, pursuitSpeedBonus, animationProfile, LandSoundProfile.ROUTINE);
    }

    /// 创建具有指定移动动画与音效表现的通用陆行怪物。
    ///
    /// 动画和音效档案只描述同一套近战行为的表现差异。实体注册处选择档案后，
    /// 不需要为仅有资源差异的变种创建空壳子类。
    ///
    /// @param pursuitSpeedBonus 发现有效目标后附加的移动速度；零表示不启用
    /// @param animationProfile  实体资源支持的基础移动动画
    /// @param soundProfile      实体使用的环境、受伤与死亡音效组合
    public BaseWarriorMonster(EntityType<? extends BaseWarriorMonster> type, Level level, double pursuitSpeedBonus, LandAnimationProfile animationProfile, LandSoundProfile soundProfile) {
        this(type, level, pursuitSpeedBonus, animationProfile, soundProfile, 1.0, false);
    }

    public BaseWarriorMonster(EntityType<? extends BaseWarriorMonster> type, Level level, double pursuitSpeedBonus, LandAnimationProfile animationProfile, LandSoundProfile soundProfile, double meleeSpeed, boolean ignoreLightPathCost) {
        this(type, level, pursuitSpeedBonus, animationProfile, soundProfile, meleeSpeed, ignoreLightPathCost, DoorBehavior.NONE);
    }

    public BaseWarriorMonster(EntityType<? extends BaseWarriorMonster> type, Level level, double pursuitSpeedBonus, LandAnimationProfile animationProfile, LandSoundProfile soundProfile, double meleeSpeed, boolean ignoreLightPathCost, DoorBehavior doorBehavior) {
        super(type, level);
        if (pursuitSpeedBonus < 0.0 || meleeSpeed <= 0.0)
            throw new IllegalArgumentException("Pursuit speed bonus must be non-negative, and melee speed must be positive");
        this.pursuitSpeedBonus = pursuitSpeedBonus;
        this.pursuitSpeedModifier = new PortAttributeModifier(Confluence.asResource("warrior_pursuit_speed"), pursuitSpeedBonus, PortAttributeModifier.Operation.ADD_VALUE).unwrap();
        this.animationProfile = animationProfile;
        this.soundProfile = soundProfile;
        this.meleeSpeed = meleeSpeed;
        this.ignoreLightPathCost = ignoreLightPathCost;
        this.doorBehavior = doorBehavior;
        if (doorBehavior != DoorBehavior.NONE && navigation instanceof GroundPathNavigation) {
            updateDoorBehavior();
            goalSelector.addGoal(-1, new EnemyOpenDoorGoal(this) {
                @Override
                public boolean canUse() {
                    return doorNavigationEnabled && super.canUse();
                }

                @Override
                public boolean canContinueToUse() {
                    return doorNavigationEnabled && super.canContinueToUse();
                }
            });
        }
    }

    private void updateDoorBehavior() {
        boolean enabled = doorBehavior == DoorBehavior.OPEN
                || doorBehavior == DoorBehavior.BLOOD_MOON && BloodMoonGameEvent.INSTANCE.started();
        if (doorNavigationEnabled == enabled) return;
        doorNavigationEnabled = enabled;
        configurePlayerTargetLineOfSight(!enabled);
        if (navigation instanceof GroundPathNavigation groundNavigation) {
            groundNavigation.stop();
            groundNavigation.setCanOpenDoors(enabled);
        }
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return createLandBehavior();
            }
        };
    }

    protected BTNode createLandBehavior() {
        CreatureDefinition.BehaviorOverrides behavior = creatureDefinition().behavior();
        JumpProfile jump = jumpProfile();
        BTNode melee = createMeleeNode(this, behavior);
        BTNode idle = createIdleNode(this, behavior);
        if (jump != null) {
            return SelectorNode.of(
                    SequenceNode.of(new HasTargetCondition(this), new JumpAttackAction(this, jump.maximumDistance(), jump.speedMultiplier(), jump.cooldownTicks(), jump.windupTicks())),
                    new JumpOverBlockAction(this, 1.0), melee, idle);
        }
        return SelectorNode.of(new JumpOverBlockAction(this, 1.0), melee, idle);
    }

    @Override
    public float getWalkTargetValue(BlockPos pos, LevelReader level) {
        return ignoreLightPathCost ? 0.0F : super.getWalkTargetValue(pos, level);
    }

    protected JumpProfile jumpProfile() {
        return null;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        if (animationProfile == LandAnimationProfile.NONE) {
            return;
        }
        controllers.add(new AnimationController<>(this, "Walk/Idle", 5, state -> {
            state.setControllerSpeed(movementAnimationSpeed(state.getLimbSwingAmount()));
            if (state.isMoving() || animationProfile == LandAnimationProfile.WALK_ONLY && swinging) {
                boolean usesRun = animationProfile == LandAnimationProfile.WALK_RUN
                        || animationProfile == LandAnimationProfile.WALK_RUN_IDLE_ATTACK;
                return state.setAndContinue(usesRun && isSprinting() ? RUN : WALK);
            }
            if (animationProfile != LandAnimationProfile.WALK_ONLY
                    && animationProfile != LandAnimationProfile.WALK_RUN) {
                return state.setAndContinue(IDLE);
            }
            return PlayState.STOP;
        }));
        if (animationProfile == LandAnimationProfile.WALK_RUN_IDLE_ATTACK) {
            controllers.add(new AnimationController<>(this, "Attack", 0, state -> swinging ? state.setAndContinue(ATTACK) : PlayState.STOP));
        }
    }

    protected float movementAnimationSpeed(float limbSwingAmount) {
        return (float) (getAttributeValue(Attributes.MOVEMENT_SPEED) / 0.25);
    }

    @Override
    public void tick() {
        if (!level().isClientSide && doorBehavior == DoorBehavior.BLOOD_MOON) updateDoorBehavior();
        super.tick();
        if (level().isClientSide || pursuitSpeedBonus == 0.0) {
            return;
        }

        boolean pursuing = getTarget() != null && getTarget().isAlive();
        var movementSpeed = getAttribute(Attributes.MOVEMENT_SPEED);
        if (movementSpeed == null) {
            return;
        }
        AttributeModifier modifier = movementSpeed.getModifier(pursuitSpeedModifier.getId());
        if (pursuing && modifier == null) {
            movementSpeed.addTransientModifier(pursuitSpeedModifier);
        } else if (!pursuing && modifier != null) {
            movementSpeed.removeModifier(pursuitSpeedModifier.getId());
        }
        setSprinting(pursuing);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return switch (soundProfile) {
            case ZOMBIE -> ModSoundEvents.TR_ZOMBIE_FREE.get();
            case FACE_MONSTER -> ModSoundEvents.FACE_HOOT.get();
            default -> super.getAmbientSound();
        };
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return soundProfile == LandSoundProfile.POSSESSED_ARMOR
                ? ModSoundEvents.METAL_HURT.get()
                : super.getHurtSound(source);
    }

    @Override
    protected SoundEvent getDeathSound() {
        return switch (soundProfile) {
            case ZOMBIE, FACE_MONSTER -> ModSoundEvents.TR_ZOMBIE_DEATH.get();
            case POSSESSED_ARMOR -> ModSoundEvents.SOUL_DEATH.get();
            default -> super.getDeathSound();
        };
    }

    private static BTNode createMeleeNode(BaseWarriorMonster self, CreatureDefinition.BehaviorOverrides behavior) {
        if (behavior.meleeRange() > 0.0) {
            return SequenceNode.of(new HasTargetCondition(self), new MoveToTargetAction(self, behavior.moveSpeedOr(self.meleeSpeed), behavior.meleeRange()), new MeleeAttackAction(self, behavior.meleeRange()));
        }
        return new VanillaGoalAction(self.createMeleeGoal(behavior.moveSpeedOr(self.meleeSpeed)));
    }

    protected MeleeAttackGoal createMeleeGoal(double speed) {
        return new MeleeAttackGoal(this, speed, true);
    }

    private static BTNode createIdleNode(BaseWarriorMonster self, CreatureDefinition.BehaviorOverrides behavior) {
        if (behavior.wanderSpeed() > 0.0 || behavior.wanderRadius() > 0 || behavior.idleTicks() > 0) {
            return SequenceNode.of(new WaitAction(behavior.idleTicksOr(20) + self.random.nextInt(40)), new RandomStrollAction(self, behavior.wanderSpeedOr(1.0), behavior.wanderRadiusOr(10)));
        }
        return SelectorNode.of(
                new VanillaGoalAction(new WaterAvoidingRandomStrollGoal(self, 1.0)),
                new VanillaGoalAction(new LookAtPlayerGoal(self, Player.class, 6.0F)),
                new VanillaGoalAction(new RandomLookAroundGoal(self)));
    }

    /// 简单陆行怪的跃击参数。
    public record JumpProfile(double maximumDistance, double speedMultiplier, int cooldownTicks,
                              int windupTicks) {
        public JumpProfile {
            if (maximumDistance <= 0.0) {
                throw new IllegalArgumentException("Jump profile maximum distance must be positive");
            }
            if (speedMultiplier <= 0.0 || cooldownTicks < 0 || windupTicks < 0) {
                throw new IllegalArgumentException("Jump profile speed must be positive and timing must be non-negative");
            }
        }
    }

    /// 通用陆行资源可用的基础动画组合。
    ///
    /// {@link #NONE} 留给具有专用控制器或非标准动画键的子类；
    /// {@link #WALK_ONLY} 在静止时停止控制器；{@link #WALK_IDLE} 在移动与待机
    /// 之间平滑切换；{@link #WALK_RUN_IDLE_ATTACK} 额外提供追击奔跑和独立攻击层。
    public enum LandAnimationProfile {
        NONE,
        WALK_ONLY,
        /// 仅包含走路和奔跑资源；静止及攻击姿势由模型默认状态处理。
        WALK_RUN,
        WALK_IDLE,
        WALK_RUN_IDLE_ATTACK
    }

    /// 共用陆行行为的变种音效档案。
    ///
    /// 这里只保存有限的表现组合；基础档案继续使用 {@link BaseMonster}
    /// 的常规受伤与死亡音效。新增仅有音效差异的变种时，在注册处选择对应档案即可。
    public enum LandSoundProfile {
        ROUTINE,
        ZOMBIE,
        FACE_MONSTER,
        POSSESSED_ARMOR
    }

    /// 普通战士实体对门的明确处理方式。
    public enum DoorBehavior {
        NONE,
        OPEN,
        BLOOD_MOON
    }
}
