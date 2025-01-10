package org.confluence.mod.common.entity.projectile.bomb;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.init.ModEntities;
import org.confluence.mod.common.init.item.ConsumableItems;

public class BaseDynamiteEntity extends BaseBombEntity {
    public BaseDynamiteEntity(EntityType<? extends BaseDynamiteEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.delay = 100;
    }

    public BaseDynamiteEntity(EntityType<? extends BaseDynamiteEntity> pEntityType, LivingEntity pShooter) {
        super(pEntityType, pShooter);
        this.delay = 100;
    }

    public BaseDynamiteEntity(LivingEntity pShooter) {
        super(ModEntities.DYNAMITE.get(), pShooter);
        this.delay = 100;
    }

    @Override
    protected Item getDefaultItem() {
        return ConsumableItems.DYNAMITE.get();
    }

    @Override
    protected void explodeFunction() {
        level().explode(
                this, Explosion.getDefaultDamageSource(level(), this),
                new ExplosionDamageCalculator() {
                    @Override
                    public float getEntityDamageAmount(Explosion explosion, Entity entity) {
                        return super.getEntityDamageAmount(explosion, entity) * 1.5F;
                    }
                },
                getX(), getY(), getZ(), 4.5F, false,
                Level.ExplosionInteraction.TNT, ParticleTypes.EXPLOSION,
                ParticleTypes.EXPLOSION_EMITTER, SoundEvents.GENERIC_EXPLODE
        );
    }

    @Override
    protected void createEmitter() {}
}
