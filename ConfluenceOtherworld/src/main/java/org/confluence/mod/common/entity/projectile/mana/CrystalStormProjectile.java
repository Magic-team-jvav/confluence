package org.confluence.mod.common.entity.projectile.mana;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModEntities;
import org.mesdag.particlestorm.PSGameClient;
import org.mesdag.particlestorm.particle.ParticleEmitter;

public class CrystalStormProjectile extends AbstractManaProjectile {
    private ParticleEmitter trail;

    public CrystalStormProjectile(EntityType<CrystalStormProjectile> entityType, Level level) {
        super(entityType, level);
        setNoGravity(true);
    }

    public CrystalStormProjectile(LivingEntity living) {
        this(ModEntities.CRYSTAL_STORM_PROJECTILE.get(), living.level());
    }

    @Override
    public void tick() {
        setDeltaMovement(getDeltaMovement().scale(0.96));
        super.tick();
        if (tickCount > 60) discard();
    }

    @Override
    public void baseTick() {
        super.baseTick();

        if (level().isClientSide) {
            if (trail == null || trail.isRemoved()) {
                this.trail = new ParticleEmitter(level(), position(), Confluence.asResource("crystal_storm_projectile_trail"));
                trail.attachEntity(this);
                PSGameClient.LOADER.addEmitter(trail, false);
            }
        }

        Vec3 vec3 = getDeltaMovement();
        move(MoverType.SELF, vec3);
        Vec3 motion = getDeltaMovement();
        if (!vec3.equals(motion)) {
            if (motion.x != vec3.x) motion = new Vec3(-vec3.x, vec3.y, vec3.z);
            if (motion.y != vec3.y) motion = new Vec3(vec3.x, -vec3.y, vec3.z);
            if (motion.z != vec3.z) motion = new Vec3(vec3.x, vec3.y, -vec3.z);
        }
        setDeltaMovement(motion.scale(0.96));
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        doHurtAndKnockback(result.getEntity(), 0.5, 0.2);
        discard();
    }
}
