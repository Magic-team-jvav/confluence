package org.confluence.mod.common.entity.projectile.bomb;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import org.confluence.lib.util.MultiplyExplosionDamageCalculator;
import org.confluence.mod.common.init.ModEntities;

public class BaseDynamiteEntity extends BaseBombEntity {
    public static final float DIAMETER = 0.25F;

    public BaseDynamiteEntity(EntityType<? extends BaseDynamiteEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.delay = 100;
        this.diameter = DIAMETER;
        this.blastPower = 15.0F;
    }

    public BaseDynamiteEntity(EntityType<? extends BaseDynamiteEntity> pEntityType, LivingEntity pShooter) {
        super(pEntityType, pShooter);
        this.delay = 100;
        this.diameter = 0.25F;
        this.blastPower = 15.0F;
    }

    public BaseDynamiteEntity(LivingEntity pShooter) {
        super(ModEntities.DYNAMITE.get(), pShooter);
        this.delay = 100;
        this.diameter = 0.25F;
        this.blastPower = 15.0F;
    }

    @Override
    protected void explodeFunction() {
        level().explode(
                this, Explosion.getDefaultDamageSource(level(), this),
                new MultiplyExplosionDamageCalculator(0.2F),
                getX(), getY(), getZ(), blastPower, false,
                Level.ExplosionInteraction.TNT, ParticleTypes.EXPLOSION,
                ParticleTypes.EXPLOSION_EMITTER, SoundEvents.GENERIC_EXPLODE
        );
    }

    @Override
    protected void createEmitter() {}
}
