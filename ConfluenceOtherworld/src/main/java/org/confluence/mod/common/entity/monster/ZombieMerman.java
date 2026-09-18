package org.confluence.mod.common.entity.monster;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public final class ZombieMerman extends BaseWarriorMonster {
    public ZombieMerman(EntityType<? extends ZombieMerman> type, Level level) {
        super(type, level, 0.0, LandAnimationProfile.NONE, LandSoundProfile.ZOMBIE, 1.0, true, DoorBehavior.BLOOD_MOON);
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    @Override
    public double getAttributeValue(Attribute attribute) {
        double value = super.getAttributeValue(attribute);
        if (attribute != Attributes.MOVEMENT_SPEED || isInWater()) return value;
        double maximum = stateAttributeValue(MovementState.WOUNDED, attribute, value);
        return value + (maximum - value) * (1.0 - getHealth() / getMaxHealth());
    }

    @Override
    public void travel(Vec3 input) {
        if (isEffectiveAi() && isInWater() && getTarget() != null && getTarget().isAlive()) {
            Vec3 offset = getTarget().position().subtract(position());
            double speed = stateParameters(MovementState.SWIMMING).behavior().moveSpeed();
            Vec3 desired = offset.normalize().scale(speed);
            if (!getSensing().hasLineOfSight(getTarget())) {
                Vec3 next = getNavigation().getPath() == null || getNavigation().getPath().isDone() ? null : getNavigation().getPath().getNextEntityPos(this);
                if (next != null) desired = next.subtract(position()).normalize().scale(speed);
            }
            setDeltaMovement(getDeltaMovement().lerp(desired, 0.15));
            if (horizontalCollision && offset.y > 0.0)
                setDeltaMovement(getDeltaMovement().x, 0.6, getDeltaMovement().z);
            move(MoverType.SELF, getDeltaMovement());
            setDeltaMovement(getDeltaMovement().scale(0.9));
        } else {
            super.travel(input);
        }
    }

    public enum MovementState {SWIMMING, WOUNDED}
}
