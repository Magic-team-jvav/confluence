package org.confluence.mod.common.entity.projectile.bomb;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.init.ModEntities;
import org.confluence.mod.common.init.item.ConsumableItems;
import org.confluence.mod.util.MultiplyExplosionDamageCalculator;

public class BaseDynamiteEntity extends BaseBombEntity {
    public static final float DIAMETER = 0.25F;

    public BaseDynamiteEntity(EntityType<? extends BaseDynamiteEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.delay = 100;
        this.diameter = DIAMETER;
    }

    public BaseDynamiteEntity(EntityType<? extends BaseDynamiteEntity> pEntityType, LivingEntity pShooter) {
        super(pEntityType, pShooter);
        this.delay = 100;
        this.diameter = 0.25F;
    }

    public BaseDynamiteEntity(LivingEntity pShooter) {
        super(ModEntities.DYNAMITE.get(), pShooter);
        this.delay = 100;
        this.diameter = 0.25F;
    }

    @Override
    protected Item getDefaultItem() {
        return ConsumableItems.DYNAMITE.get();
    }

    @Override
    protected void explodeFunction() {
        level().explode(
                this, Explosion.getDefaultDamageSource(level(), this),
                new MultiplyExplosionDamageCalculator(1.5F),
                getX(), getY(), getZ(), 4.5F, false,
                Level.ExplosionInteraction.TNT, ParticleTypes.EXPLOSION,
                ParticleTypes.EXPLOSION_EMITTER, SoundEvents.GENERIC_EXPLODE
        );
    }

    @Override
    protected void createEmitter() {}
}
