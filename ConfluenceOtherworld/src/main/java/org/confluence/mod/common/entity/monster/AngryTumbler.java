package org.confluence.mod.common.entity.monster;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.data.saved.ConfluenceData;
import org.confluence.mod.common.entity.ai.goal.AcceleratingMeleeAttackGoal;

public final class AngryTumbler extends BaseWarriorMonster {
    public AngryTumbler(EntityType<? extends AngryTumbler> type, Level level) {
        super(type, level, 0.0, LandAnimationProfile.NONE, LandSoundProfile.ROUTINE, 1.0, true);
    }

    @Override
    protected MeleeAttackGoal createMeleeGoal(double speed) {
        var parameters = stateParameters(MovementState.PURSUING);
        return new AcceleratingMeleeAttackGoal(this, speed, parameters.behavior().chargeSpeedOr(speed * 2.0), parameters.durationOr(25));
    }

    @Override
    public double getAttributeValue(Attribute attribute) {
        double value = super.getAttributeValue(attribute);
        if (attribute == Attributes.MOVEMENT_SPEED && level() instanceof ServerLevel level && getTarget() != null) {
            ConfluenceData data = ConfluenceData.get(level);
            Vec3 direction = getTarget().position().subtract(position()).multiply(1.0, 0.0, 1.0).normalize();
            double wind = direction.x * data.getWindSpeedX() + direction.z * data.getWindSpeedZ();
            value *= Mth.clamp(1.0 + wind, 0.3, 2.5);
        }
        return value;
    }

    public enum MovementState {PURSUING}
}
