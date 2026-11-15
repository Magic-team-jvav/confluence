package org.confluence.mod.common.entity.monster;

import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import org.confluence.mod.common.init.ModSoundEvents;

/// 能从水面跃向岸边目标的巨骨舌鱼。
///
/// 水中巡游和近战仍复用食人鱼族行为；这里只保留巨骨舌鱼独有的岸边索敌与跃水攻击，
/// 避免让所有水生近战怪都能追踪陆地玩家。
public final class Arapaima extends Piranha {
    private static final double SHORE_TARGET_RANGE = 6.0;
    private static final double SHORE_TARGET_HEIGHT = 3.0;
    private static final int LEAP_COOLDOWN = 20;
    private int leapCooldown;

    public Arapaima(EntityType<? extends Arapaima> type, Level level) {
        super(type, level);
    }

    @Override
    protected boolean isValidAquaticTarget(LivingEntity target) {
        return target.isInWaterOrBubble() || isReachableFromShore(target);
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide || isNoAi()) return;
        if (leapCooldown > 0) {
            leapCooldown--;
            return;
        }
        LivingEntity target = getTarget();
        if (target == null || target.isInWaterOrBubble() || !isReachableFromShore(target))
            return;
        if (!isInWaterOrBubble() || level().getFluidState(blockPosition().above()).is(FluidTags.WATER))
            return;
        Vec3 horizontal = target.position().subtract(position()).multiply(1.0, 0.0, 1.0);
        if (horizontal.lengthSqr() < 1.0E-7) return;
        Vec3 movement = getDeltaMovement();
        Vec3 pursuit = horizontal.normalize().scale(getAttributeValue(Attributes.MOVEMENT_SPEED));
        Vec3 leap = new Vec3(movement.x + pursuit.x, getJumpPower(), movement.z + pursuit.z);
        faceCombatDirection(leap, 180.0F, 180.0F);
        setDeltaMovement(leap);
        hasImpulse = true;
        leapCooldown = LEAP_COOLDOWN;
    }

    private boolean isReachableFromShore(LivingEntity target) {
        if (!isInWaterOrBubble() && (onGround() || leapCooldown <= 0) || !hasLineOfSight(target))
            return false;
        double x = target.getX() - getX();
        double z = target.getZ() - getZ();
        double y = target.getY() - getY();
        return x * x + z * z <= SHORE_TARGET_RANGE * SHORE_TARGET_RANGE
                && y >= -1.0 && y <= SHORE_TARGET_HEIGHT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSoundEvents.ROUTINE_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.ROUTINE_DEATH.get();
    }

}
