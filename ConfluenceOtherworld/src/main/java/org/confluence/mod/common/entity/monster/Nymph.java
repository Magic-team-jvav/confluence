package org.confluence.mod.common.entity.monster;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.common.LibEffects;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.composite.SelectorNode;
import org.confluence.mod.common.entity.ai.bt.leaf.VanillaGoalAction;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

/// 伪装成迷失女孩，并在生命不满、发生移动或被玩家近距离看见后显露真身的宁芙。
public final class Nymph extends BaseMonster {
    private static final double REVEAL_DISTANCE_SQUARED = 12.5 * 12.5;
    private static final double MOVEMENT_REVEAL_DISTANCE_SQUARED = 1.0E-4;
    private static final String TRIGGERED_TAG = "Triggered";
    private static final EntityDataAccessor<Boolean> TRIGGERED = SynchedEntityData.defineId(Nymph.class, EntityDataSerializers.BOOLEAN);
    private static final RawAnimation SIT = RawAnimation.begin().thenLoop("sit");
    private static final RawAnimation DASH = RawAnimation.begin().thenLoop("dash");

    public Nymph(EntityType<? extends Nymph> type, Level level) {
        super(type, level);
        xpReward = 20;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(TRIGGERED, false);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (key == TRIGGERED) refreshDimensions();
    }

    @Override
    protected boolean mustSeePlayerTarget() {
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) {
            return;
        }
        LivingEntity target = getTarget();
        if (target == null || !target.isAlive() || !canAttack(target) || target instanceof Player player && (player.isCreative() || player.isSpectator())) {
            setTarget(null);
            setTriggered(false);
            updatePursuitSpeed(false);
            getNavigation().stop();
            setDeltaMovement(new Vec3(0.0, getDeltaMovement().y, 0.0));
            return;
        }
        if (!isTriggered()) {
            boolean injured = getHealth() < getMaxHealth();
            boolean moved = distanceToSqr(xo, yo, zo) > MOVEMENT_REVEAL_DISTANCE_SQUARED;
            boolean playerApproached = target != null && target.isAlive() && distanceToSqr(target) < REVEAL_DISTANCE_SQUARED && getSensing().hasLineOfSight(target);
            if (injured || moved || playerApproached) setTriggered(true);
            else {
                setDeltaMovement(Vec3.ZERO);
                updatePursuitSpeed(false);
                getNavigation().stop();
                return;
            }
        }
        if (target != null && !target.isAlive()) {
            getNavigation().stop();
        }
        updatePursuitSpeed(target != null && target.isAlive());
    }

    private void updatePursuitSpeed(boolean pursuing) {
        setSpecialState(CombatState.PURSUING, pursuing);
        setSprinting(pursuing);
    }

    public boolean isTriggered() {
        return entityData.get(TRIGGERED);
    }

    public void setTriggered(boolean triggered) {
        if (entityData.get(TRIGGERED) == triggered) {
            return;
        }
        entityData.set(TRIGGERED, triggered);
        refreshDimensions();
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean damaged = super.hurt(source, amount);
        if (damaged && !level().isClientSide) {
            setTriggered(true);
            Entity attacker = source.getEntity();
            if (attacker instanceof LivingEntity living && canAttack(living)) setTarget(living);
        }
        return damaged;
    }

    @Override
    public boolean canBeAffected(MobEffectInstance effect) {
        return (isTriggered() || !effect.is(LibEffects.CONFUSED)) && super.canBeAffected(effect);
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        EntityDimensions dimensions = super.getDimensions(pose);
        return !isTriggered()
                ? dimensions.scale(1.0F, 0.75F)
                : dimensions;
    }

    @Override
    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return !isTriggered()
                ? 1.05F
                : super.getStandingEyeHeight(pose, dimensions);
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return SelectorNode.of(
                        new VanillaGoalAction(new MeleeAttackGoal(Nymph.this, 0.6, true) {
                            @Override
                            public boolean canUse() {
                                return isTriggered() && super.canUse();
                            }

                            @Override
                            public boolean canContinueToUse() {
                                return isTriggered() && super.canContinueToUse();
                            }
                        }),
                        new VanillaGoalAction(new WaterAvoidingRandomStrollGoal(Nymph.this, 0.6) {
                            @Override
                            public boolean canUse() {
                                return isTriggered() && getTarget() == null && super.canUse();
                            }

                            @Override
                            public boolean canContinueToUse() {
                                return isTriggered() && getTarget() == null && super.canContinueToUse();
                            }
                        }),
                        new VanillaGoalAction(new LookAtPlayerGoal(Nymph.this, Player.class, 10.0F, 1.0F)));
            }
        };
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean(TRIGGERED_TAG, isTriggered());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setTriggered(tag.getBoolean(TRIGGERED_TAG));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "state", 5, state -> {
            return state.setAndContinue(isTriggered() ? DASH : SIT);
        }));
    }

    public enum CombatState {PURSUING}
}
