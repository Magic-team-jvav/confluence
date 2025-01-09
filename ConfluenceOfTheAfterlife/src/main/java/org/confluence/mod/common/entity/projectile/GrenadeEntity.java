package org.confluence.mod.common.entity.projectile;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.init.ModEntities;
import org.confluence.mod.common.init.item.ConsumableItems;
import org.confluence.mod.util.ModUtils;

public class GrenadeEntity extends ThrowableItemProjectile {
    public static final float DIAMETER = 0.125F;
    public float rotateO = 0.0F;
    public float rotate = 0.0F;
    private int age = 0;

    public GrenadeEntity(EntityType<GrenadeEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public GrenadeEntity(double pX, double pY, double pZ, Level pLevel) {
        super(ModEntities.GRENADE.get(), pX, pY, pZ, pLevel);
    }

    public GrenadeEntity(LivingEntity pShooter) {
        super(ModEntities.GRENADE.get(), pShooter, pShooter.level());
    }

    @Override
    protected Item getDefaultItem() {
        return ConsumableItems.GRENADE.get();
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) {
            float s = (float) getDeltaMovement().length();
            float r = 2.0F * s / DIAMETER;
            if (rotate > Mth.TWO_PI) this.rotate -= Mth.TWO_PI;
            this.rotateO = rotate;
            this.rotate += r / Mth.PI;
        }
        if (age++ > 60) {
            explode();
        }
    }

    @Override
    protected double getDefaultGravity() {
        return 0.08;
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        explode();
    }

    private void explode() {
        if (!level().isClientSide) {
            level().explode(
                    this,
                    Explosion.getDefaultDamageSource(level(), this),
                    new ExplosionDamageCalculator() {
                        @Override
                        public float getEntityDamageAmount(Explosion explosion, Entity entity) {
                            return super.getEntityDamageAmount(explosion, entity) * 2.0F;
                        }
                    },
                    getX(),
                    getY(),
                    getZ(),
                    1.5F,
                    false,
                    Level.ExplosionInteraction.NONE,
                    ParticleTypes.EXPLOSION,
                    ParticleTypes.EXPLOSION_EMITTER,
                    SoundEvents.GENERIC_EXPLODE);
        }
        discard();
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        Vec3 motion = ModUtils.relativeScale(getDeltaMovement(), result.getDirection().getAxis(), -0.1);
        if (Math.abs(motion.y) < 0.03) motion = new Vec3(motion.x, 0.0, motion.z);
        setDeltaMovement(motion.scale(0.9));
    }
}
