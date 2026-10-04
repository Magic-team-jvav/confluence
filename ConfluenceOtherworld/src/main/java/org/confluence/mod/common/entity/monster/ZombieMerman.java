package org.confluence.mod.common.entity.monster;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.fluids.FluidType;

public final class ZombieMerman extends BaseWarriorMonster {
    public ZombieMerman(EntityType<? extends ZombieMerman> type, Level level) {
        super(type, level, 0.0, LandAnimationProfile.NONE, LandSoundProfile.ZOMBIE, 1.0, true, DoorBehavior.BLOOD_MOON);
    }

    /// 1.21.1 的 `LivingEntity#canBreatheUnderwater()` 是 final（`LivingEntity.java:382`），
    /// 官方替代钩子是 `canDrownInFluidType`（见 `BaseAquaticMonster` 的同名改动）。
    @Override
    public boolean canDrownInFluidType(FluidType type) {
        return type != NeoForgeMod.WATER_TYPE.value() && super.canDrownInFluidType(type);
    }

    @Override
    public double getAttributeValue(Holder<Attribute> attribute) {
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
