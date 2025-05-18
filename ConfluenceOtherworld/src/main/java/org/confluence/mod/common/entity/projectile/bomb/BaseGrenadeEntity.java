package org.confluence.mod.common.entity.projectile.bomb;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import org.confluence.lib.util.MultiplyExplosionDamageCalculator;
import org.confluence.mod.common.init.ModEntities;

public class BaseGrenadeEntity extends BaseBombEntity {
    public static final float DIAMETER = 0.125F;

    public BaseGrenadeEntity(EntityType<? extends BaseGrenadeEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.diameter = DIAMETER;
    }

    public BaseGrenadeEntity(EntityType<? extends BaseGrenadeEntity> entityType, LivingEntity pShooter) {
        super(entityType, pShooter);
        this.diameter = DIAMETER;
    }

    public BaseGrenadeEntity(LivingEntity shooter) {
        this(ModEntities.GRENADE.get(), shooter);
    }

    @Override
    public double getDefaultGravity() {
        return 0.08;
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        if (!level().isClientSide) {
            explodeFunction();
            discard();
        }
    }

    @Override
    protected void explodeFunction() {
        level().explode(
                this, Explosion.getDefaultDamageSource(level(), this),
                getExplosionDamageCalculator(),
                getX(), getY(), getZ(), 1.5F, false,
                Level.ExplosionInteraction.NONE, ParticleTypes.EXPLOSION,
                ParticleTypes.EXPLOSION_EMITTER, SoundEvents.GENERIC_EXPLODE
        );
    }

    protected ExplosionDamageCalculator getExplosionDamageCalculator() {
        return new MultiplyExplosionDamageCalculator(2F);
    }

    @Override
    protected void createEmitter() {}
}
