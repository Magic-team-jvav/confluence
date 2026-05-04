package org.confluence.mod.common.entity.projectile.mana;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.ModEntities;
import org.mesdag.particlestorm.PSGameClient;
import org.mesdag.particlestorm.particle.ParticleEmitter;

public class CursedFlamesProjectile extends AbstractManaProjectile {
    private int collideCount = 0;
    private int penetrateCount = 0;
    private ParticleEmitter emitter;

    public CursedFlamesProjectile(EntityType<CursedFlamesProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public CursedFlamesProjectile(LivingEntity living) {
        super(ModEntities.CURSED_FLAMES_PROJECTILE.get(), living.level());
    }

    @Override
    public void baseTick() {
        if (!level().getFluidState(blockPosition()).isEmpty()) {
            discard();
            return;
        }
        super.baseTick();

        Vec3 vec3 = getDeltaMovement();
        move(MoverType.SELF, vec3.add(0.0, -getGravity(), 0.0));
        Vec3 motion = getDeltaMovement();
        if (!vec3.equals(motion)) {
            if (motion.x != vec3.x) motion = new Vec3(-vec3.x, vec3.y, vec3.z);
            if (motion.y != vec3.y) motion = new Vec3(vec3.x, -vec3.y, vec3.z);
            if (motion.z != vec3.z) motion = new Vec3(vec3.x, vec3.y, -vec3.z);
            if (this.collideCount++ >= 5) {
                discard();
                return;
            }
        }
        setDeltaMovement(motion.scale(0.99).add(0.0, -getGravity(), 0.0));

        if (level().isClientSide && (emitter == null || emitter.isRemoved())) {
            this.emitter = new ParticleEmitter(level(), position(), Confluence.asResource("cursed_flames"));
            emitter.attachEntity(this);
            PSGameClient.LOADER.addEmitter(emitter, false);
        }

        if (tickCount > 1200) discard();
    }

    @Override
    protected double getDefaultGravity() {
        return 0.04;
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity entity = result.getEntity();
        if (entity instanceof LivingEntity living) {
            living.addEffect(new MobEffectInstance(ModEffects.CURSED_INFERNO, 140));
        }
        doHurtAndKnockback(entity, 0.6, 0.2);
        if (this.penetrateCount++ >= 1) {
            discard();
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.collideCount = compound.getInt("CollideCount");
        this.penetrateCount = compound.getInt("PenetrateCount");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("CollideCount", collideCount);
        compound.putInt("PenetrateCount", penetrateCount);
    }
}
