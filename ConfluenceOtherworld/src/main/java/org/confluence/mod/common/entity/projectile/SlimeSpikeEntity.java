package org.confluence.mod.common.entity.projectile;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

public class SlimeSpikeEntity extends AbstractHurtingProjectile {
    private float damage = 5.0f;

    public SlimeSpikeEntity(EntityType<? extends SlimeSpikeEntity> type, Level level) {
        super(type, level);
    }

    public static SlimeSpikeEntity create(Level level, LivingEntity shooter,
                                          EntityType<? extends SlimeSpikeEntity> type,
                                          double dirX, double dirY, double dirZ,
                                          float velocity, float damage) {
        SlimeSpikeEntity spike = new SlimeSpikeEntity(type, level);
        spike.setOwner(shooter);
        spike.setPos(shooter.getX(), shooter.getY() + shooter.getEyeHeight() * 0.5, shooter.getZ());
        spike.shoot(dirX, dirY, dirZ, velocity, 0);
        spike.damage = damage;
        return spike;
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (!level().isClientSide && result.getEntity() instanceof LivingEntity target) {
            target.hurt(damageSources().mobProjectile(this,
                    getOwner() instanceof LivingEntity o ? o : null), damage);
        }
        discard();
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        discard();
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) {
            level().addParticle(ParticleTypes.CRIT, getX(), getY(), getZ(), 0, 0, 0);
        }
        if (tickCount > 80) {
            discard();
        }
    }

    @Override
    protected boolean shouldBurn() {
        return false;
    }

    @Override
    public boolean isPickable() {
        return false;
    }
}
