package org.confluence.mod.common.entity.projectile.mana;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.VectorUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModEntities;
import org.mesdag.particlestorm.PSGameClient;
import org.mesdag.particlestorm.particle.ParticleEmitter;

import java.util.HashSet;
import java.util.Set;

public class WaterBoltProjectile extends AbstractManaProjectile {
    private final Set<Entity> passThrough = new HashSet<>();
    private int collideCount = 0;
    private ParticleEmitter emitter;

    public WaterBoltProjectile(EntityType<WaterBoltProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public WaterBoltProjectile(LivingEntity living) {
        this(ModEntities.WATER_BOLT_PROJECTILE.get(), living.level());
        setOwner(living);
        setPos(living.getX(), living.getEyeY() - 0.1, living.getZ());
    }

    @Override
    public void baseTick() {
        if (tickCount > 600) {
            discard();
            return;
        }
        super.baseTick();

        Vec3 vec3 = getDeltaMovement();
        move(MoverType.SELF, vec3);
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
        setDeltaMovement(motion);

        if (level().isClientSide) {
            if (emitter == null) {
                this.emitter = new ParticleEmitter(level(), position(), Confluence.asResource("water_stream"));
                emitter.attached = this;
                PSGameClient.LOADER.addEmitter(emitter, false);
            }
        } else {
            HitResult hitResult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
            if (hitResult instanceof EntityHitResult entityHitResult) {
                Entity entity = entityHitResult.getEntity();
                if (passThrough.add(entity)) {
                    if (entity.hurt(getDamagesource(), 14.5F)) {
                        VectorUtils.knockBackA2B(this, entity, 1.5, 0.2);
                    }
                    if (passThrough.size() >= 10) {
                        discard();
                    }
                }
            }
        }
    }
}
