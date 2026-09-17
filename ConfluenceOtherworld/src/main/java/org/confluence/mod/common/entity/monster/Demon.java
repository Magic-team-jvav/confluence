package org.confluence.mod.common.entity.monster;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.composite.SelectorNode;
import org.confluence.mod.common.entity.ai.bt.composite.SequenceNode;
import org.confluence.mod.common.entity.ai.bt.condition.HasTargetCondition;
import org.confluence.mod.common.entity.ai.bt.leaf.FlyWanderAction;
import org.confluence.mod.common.entity.ai.bt.leaf.FlyingVolleyCombatAction;
import org.confluence.mod.common.entity.ai.bt.leaf.SteeringDashAction;
import org.confluence.mod.common.entity.projectile.HostileDemonScytheProjectile;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.common.init.entity.ModEntities;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

/// 地狱恶魔的四连镰刀攻击与客户端动作表现。
///
/// 恶魔先进行一百五十刻接近，再于第 175、183、191 和 199 tick
/// 发射四枚镰刀。挥手状态持续 30 tick，与投掷动画长度一致；受伤动作优先于投掷，
/// 二者结束后回到悬浮待机。
public class Demon extends ReboundingFlyingMonster {
    private static final RawAnimation HURT = RawAnimation.begin().thenPlay("hurt");
    private static final RawAnimation ATTACK_THROW = RawAnimation.begin().thenPlay("attack.throw");
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("misc.idle");
    private FlyingVolleyCombatAction volleyCombat;

    public Demon(EntityType<? extends BaseFlyingMonster> type, Level level) {
        super(type, level);
    }

    @Override
    public float getWalkTargetValue(BlockPos pos, LevelReader level) {
        return 0.0F;
    }

    @Override
    protected Vec3 reboundVelocity(Vec3 requested, Vec3 allowed) {
        Vec3 rebound = requested;
        if (allowed.x != requested.x) {
            rebound = new Vec3(-requested.x * 0.8, rebound.y + 0.2, rebound.z);
        }
        if (allowed.z != requested.z) {
            rebound = new Vec3(rebound.x, rebound.y + 0.2, -requested.z * 0.8);
        }
        return rebound;
    }

    @Override
    protected BTRoot createBT() {
        FlyingVolleyCombatAction combat = new FlyingVolleyCombatAction(
                this,
                new SteeringDashAction(
                        this,
                        0.95,
                        0.5,
                        0.02,
                        10.0,
                        90.0,
                        30.0,
                        30),
                this::createVolleyProjectile,
                150,
                volleyTicks());
        volleyCombat = combat;
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return SelectorNode.of(SequenceNode.of(new HasTargetCondition(Demon.this), combat), new FlyWanderAction(Demon.this, 0.2, 8));
            }
        };
    }

    protected boolean isApproachingVolleyTarget() {
        LivingEntity target = getTarget();
        return target != null && target.isAlive() && volleyCombat != null && volleyCombat.isApproaching();
    }

    protected int[] volleyTicks() {
        return new int[]{175, 183, 191, 199};
    }

    protected Projectile createVolleyProjectile(LivingEntity target) {
        HostileDemonScytheProjectile projectile = new HostileDemonScytheProjectile(ModEntities.HOSTILE_DEMON_SCYTHE.get(), level());
        projectile.configure(this, target, (float) getAttributeValue(Attributes.ATTACK_DAMAGE));
        swing(InteractionHand.MAIN_HAND);
        playSound(ModSoundEvents.WAVING.get());
        return projectile;
    }

    @Override
    public int getCurrentSwingDuration() {
        return 30;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(
                this,
                "Fly/Hurt/Throw",
                3,
                state -> {
                    if (hurtTime > 0) {
                        return state.setAndContinue(HURT);
                    }
                    if (swingTime > 0) {
                        return state.setAndContinue(ATTACK_THROW);
                    }
                    return state.setAndContinue(IDLE);
                }));
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return !source.is(DamageTypeTags.IS_FIRE) && super.hurt(source, amount);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSoundEvents.DEMON_FREE.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSoundEvents.DEMON_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.DEMON_DEATH.get();
    }
}
