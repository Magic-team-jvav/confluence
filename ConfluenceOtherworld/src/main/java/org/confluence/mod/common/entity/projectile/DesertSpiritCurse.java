package org.confluence.mod.common.entity.projectile;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public final class DesertSpiritCurse extends StraightMonsterProjectile {
    public DesertSpiritCurse(EntityType<? extends DesertSpiritCurse> type, Level level) {
        super(type, level);
        noPhysics = true;
    }

    @Override
    public void configure(Mob owner, LivingEntity target, float damage) {
        configure(owner, owner.getEyePosition(), target.getEyePosition().subtract(owner.getEyePosition()).normalize().scale(0.05), damage, 61);
    }

    @Override
    protected Vec3 modifyVelocity(Vec3 velocity) {
        if (!(getOwner() instanceof Mob owner)) return velocity;
        Player nearest = null;
        double distance = 1024.0;
        for (Player player : level().players()) {
            double candidateDistance = distanceToSqr(player);
            if (candidateDistance < distance && player.isAlive() && !player.isCreative() && !player.isSpectator() && owner.canAttack(player)) {
                nearest = player;
                distance = candidateDistance;
            }
        }
        if (nearest == null) return velocity;
        Vec3 desired = nearest.getEyePosition().subtract(position()).normalize().scale(Math.min(0.4, 0.05 + tickCount * 0.006));
        return velocity.lerp(desired, 0.12);
    }

    @Override
    public boolean canHitEntity(Entity entity) {
        return false;
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {}

    @Override
    public void tick() {
        super.tick();
        if (isRemoved()) return;
        if (level().isClientSide) {
            level().addParticle(ParticleTypes.DRAGON_BREATH, getX(), getY(), getZ(), 0.0, 0.01, 0.0);
        } else if (tickCount >= 60) {
            if (getOwner() instanceof Mob owner) {
                for (LivingEntity target : level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(1.5), owner::canAttack)) {
                    if (target.getBoundingBox().getCenter().distanceToSqr(position()) <= 2.25)
                        target.hurt(damageSources().mobProjectile(this, owner), getDamage());
                }
            }
            ((ServerLevel) level()).sendParticles(ParticleTypes.DRAGON_BREATH, getX(), getY(), getZ(), 24, 0.6, 0.6, 0.6, 0.05);
            playSound(SoundEvents.GENERIC_EXPLODE, 0.4F, 1.6F);
            discard();
        }
    }
}
