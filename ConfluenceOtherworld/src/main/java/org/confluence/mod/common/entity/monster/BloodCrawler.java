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

/// 血爬虫的服务端行为实现。
///
/// <p>血爬虫沿用普通近战怪物的行为树，但会把水平碰撞状态同步为攀爬标记，
/// 从而像蜘蛛一样越过垂直表面。攀爬判断以服务端为准，客户端只读取同步结果，
/// 避免多人游戏中各端根据局部碰撞状态产生不同的移动表现。</p>
///
/// <p>环境声、受伤声和死亡声都使用血爬虫自己的声音组，不能退回通用怪物声音；
/// 这些声音同样是该生物身份的一部分。</p>
public class BloodCrawler extends BaseMonster {
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("move.walk");
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("misc.idle");
    private static final RawAnimation ATTACK = RawAnimation.begin().thenPlay("attack.strike");
    private static final EntityDataAccessor<Byte> CLIMBING = SynchedEntityData.defineId(BloodCrawler.class, EntityDataSerializers.BYTE);

    public BloodCrawler(EntityType<? extends BloodCrawler> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return BaseMonster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 31.0)
                .add(Attributes.ATTACK_DAMAGE, 15.0)
                .add(Attributes.ARMOR, 8.0)
                .add(Attributes.MOVEMENT_SPEED, 0.38)
                .add(Attributes.FOLLOW_RANGE, 32.0)
                .add(Attributes.SPAWN_REINFORCEMENTS_CHANCE, 0.01)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.8);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        // 1.21 继承蜘蛛时会主动攻击铁傀儡；重写后必须显式保留该目标族。
        targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, false));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(CLIMBING, (byte) 0);
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
        // 血爬虫沿用蜘蛛的毒素免疫，其余效果仍交给原版通用规则判断。
        return effect.getEffect() != MobEffects.POISON
                && super.canBeAffected(effect);
    }

    private boolean isClimbing() {
        return (entityData.get(CLIMBING) & 1) != 0;
    }

    private void setClimbing(boolean climbing) {
        byte flags = entityData.get(CLIMBING);
        entityData.set(CLIMBING, climbing ? (byte) (flags | 1) : (byte) (flags & -2));
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSoundEvents.BLOOD_CRAWLER_FREE.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSoundEvents.BLOOD_CRAWLER_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.BLOOD_CRAWLER_DEATH.get();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        // 移动与攻击分层播放，使攻击时仍能保留蜘蛛腿部的行走节奏。
        controllers.add(new AnimationController<>(
                this,
                "Movement",
                5,
                state -> state.setAndContinue(
                        state.isMoving() ? WALK : IDLE)));
        controllers.add(new AnimationController<>(
                this,
                "Attack",
                0,
                state -> swinging
                        ? state.setAndContinue(ATTACK)
                        : PlayState.STOP));
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return SelectorNode.of(
                        SequenceNode.of(new HasTargetCondition(BloodCrawler.this),
                                new MoveToTargetAction(BloodCrawler.this, 0.8, 2.0),
                                new MeleeAttackAction(BloodCrawler.this, 2.0),
                                new WaitAction(15)),
                        SequenceNode.of(new WaitAction(20 + random.nextInt(40)),
                                new RandomStrollAction(BloodCrawler.this, 0.5, 8)));
            }
        };
    }
}
