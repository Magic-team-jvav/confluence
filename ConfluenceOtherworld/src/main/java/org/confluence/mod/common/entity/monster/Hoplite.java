package org.confluence.mod.common.entity.monster;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTStatus;
import org.confluence.mod.common.entity.ai.bt.composite.ConditionalSwitchNode;
import org.confluence.mod.common.entity.projectile.HopliteJavelin;
import org.confluence.mod.common.init.entity.ModEntities;

public final class Hoplite extends BaseWarriorMonster {
    private static final EntityDataAccessor<Boolean> THROWING = SynchedEntityData.defineId(Hoplite.class, EntityDataSerializers.BOOLEAN);

    public Hoplite(EntityType<? extends Hoplite> type, Level level) {
        super(type, level, 0.0, LandAnimationProfile.NONE, LandSoundProfile.ROUTINE, 1.0, true);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(THROWING, false);
    }

    public boolean isThrowing() {
        return entityData.get(THROWING);
    }

    @Override
    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 1.75F;
    }

    private boolean canThrow() {
        LivingEntity target = getTarget();
        return target != null && target.isAlive() && distanceToSqr(target) > 9.0
                && distanceToSqr(target) <= 576.0 && getSensing().hasLineOfSight(target);
    }

    @Override
    protected BTNode createLandBehavior() {
        return new ConditionalSwitchNode(this::canThrow, new BTNode() {
            private int windup;
            private int cooldown;

            @Override
            public BTStatus execute() {
                LivingEntity target = getTarget();
                if (target == null) return BTStatus.FAILURE;
                getNavigation().stop();
                getMoveControl().setWantedPosition(getX(), getY(), getZ(), 0.0);
                setSpeed(0.0F);
                faceCombatDirection(target.getEyePosition().subtract(getEyePosition()), 20.0F, 20.0F);
                if (cooldown > 0) {
                    cooldown--;
                } else if (++windup < 12) {
                    entityData.set(THROWING, true);
                } else {
                    entityData.set(THROWING, false);
                    windup = 0;
                    cooldown = 36;
                    HopliteJavelin javelin = ModEntities.HOPLITE_JAVELIN.get().create(level());
                    if (javelin != null) {
                        Vec3 origin = getEyePosition().add(0.0, -0.15, 0.0);
                        Vec3 offset = target.getBoundingBox().getCenter().subtract(origin);
                        double flightTicks = Math.max(1.0, offset.length() / 1.2);
                        Vec3 velocity = offset.scale(1.0 / flightTicks).add(0.0, HopliteJavelin.GRAVITY * (flightTicks + 1.0) * 0.5, 0.0);
                        float damage = LibUtils.isMaster(level(), blockPosition()) ? 108.0F : LibUtils.isAtLeastExpert(level(), blockPosition()) ? 72.0F : 36.0F;
                        damage *= (float) getAttributeValue(Attributes.ATTACK_DAMAGE) / 22.0F;
                        javelin.configure(Hoplite.this, origin, velocity, damage, 100);
                        if (level().addFreshEntity(javelin)) {
                            swing(InteractionHand.MAIN_HAND);
                            playSound(SoundEvents.TRIDENT_THROW, 1.0F, 1.0F);
                        } else javelin.discard();
                    }
                }
                return BTStatus.RUNNING;
            }

            @Override
            public void stop() {
                windup = 0;
                entityData.set(THROWING, false);
            }
        }, super.createLandBehavior());
    }
}
