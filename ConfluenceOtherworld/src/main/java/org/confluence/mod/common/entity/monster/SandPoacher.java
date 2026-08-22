package org.confluence.mod.common.entity.monster;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.composite.SelectorNode;
import org.confluence.mod.common.entity.ai.bt.composite.SequenceNode;
import org.confluence.mod.common.entity.ai.bt.condition.HasTargetCondition;
import org.confluence.mod.common.entity.ai.bt.leaf.MeleeAttackAction;
import org.confluence.mod.common.entity.ai.bt.leaf.MoveToTargetAction;
import org.confluence.mod.common.entity.ai.bt.leaf.RandomStrollAction;
import org.confluence.mod.common.entity.ai.bt.leaf.WaitAction;
import org.confluence.mod.common.init.ModSoundEvents;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

/// 能攀爬墙面并高速贴近目标的沙贼。
///
/// <p>攀爬标记由服务端根据水平碰撞更新并同步，移动属性则与 1.21 实体的实际数值对齐。
/// 这保留了蜘蛛式地形通过能力，同时继续使用本项目统一的行为树处理追击和近战。</p>
public class SandPoacher extends BaseMonster {
    private static final EntityDataAccessor<Byte> CLIMBING = SynchedEntityData.defineId(SandPoacher.class, EntityDataSerializers.BYTE);
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("move.walk");

    public SandPoacher(EntityType<? extends SandPoacher> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return BaseMonster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 166.0)
                .add(Attributes.ATTACK_DAMAGE, 34.0)
                .add(Attributes.ARMOR, 24.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.FOLLOW_RANGE, 64.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.55);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(CLIMBING, (byte) 0);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        // 沙贼原本继承蜘蛛，因此除玩家外也会主动攻击铁傀儡。
        targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, false));
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide) {
            setClimbing(horizontalCollision);
        }
    }

    @Override
    public boolean onClimbable() {
        return isClimbing();
    }

    @Override
    public boolean canBeAffected(MobEffectInstance effect) {
        // 仅恢复蜘蛛的毒素免疫，其他效果继续遵循普通敌怪规则。
        return effect.getEffect() != MobEffects.POISON
                && super.canBeAffected(effect);
    }

    public boolean isClimbing() {
        return (entityData.get(CLIMBING) & 1) != 0;
    }

    private void setClimbing(boolean climbing) {
        byte flags = entityData.get(CLIMBING);
        entityData.set(CLIMBING, climbing
                ? (byte) (flags | 1) : (byte) (flags & -2));
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return SelectorNode.of(
                        SequenceNode.of(new HasTargetCondition(SandPoacher.this),
                                new MoveToTargetAction(SandPoacher.this, 0.7, 2.0),
                                new MeleeAttackAction(SandPoacher.this, 2.0),
                                new WaitAction(15)),
                        SequenceNode.of(new WaitAction(20 + random.nextInt(40)),
                                new RandomStrollAction(SandPoacher.this, 0.4, 8)));
            }
        };
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSoundEvents.ANTLION_FREE.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSoundEvents.ROUTINE_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.ROUTINE_DEATH.get();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "movement", 4, state -> state.isMoving() ? state.setAndContinue(WALK) : PlayState.STOP));
    }
}
