package org.confluence.mod.common.entity.projectile.mana;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
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

public class WaterStreamProjectile extends AbstractManaProjectile {
    private final Set<Entity> passThrough = new HashSet<>();
    private ParticleEmitter emitter;

    public WaterStreamProjectile(EntityType<WaterStreamProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public WaterStreamProjectile(LivingEntity living) {
        this(ModEntities.WATER_STREAM_PROJECTILE.get(), living.level());
        setOwner(living);
        setPos(living.getX(), living.getEyeY() - 0.1, living.getZ());
    }

    @Override
    public void baseTick() {
        super.baseTick();
        if (level().isClientSide) {
            if (emitter == null) {
                this.emitter = new ParticleEmitter(level(), position(), Confluence.asResource("water_stream"));
                emitter.attached = this;
                PSGameClient.LOADER.addEmitter(emitter, false);
            }
        } else {
            HitResult hitResult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
            if (hitResult.getType() == HitResult.Type.BLOCK) {
                discard();
            } else if (hitResult instanceof EntityHitResult entityHitResult) {
                Entity entity = entityHitResult.getEntity();
                if (passThrough.add(entity)) {
                    if (entity.hurt(getDamagesource(), 8F)) {
                        VectorUtils.knockBackA2B(this, entity, 3.5, 0.2);
                    }
                    if (passThrough.size() >= 5) {
                        discard();
                        return;
                    }
                }
            }
        }

        Vec3 vec3 = getDeltaMovement();
        double offX = getX() + vec3.x;
        double offY = getY() + vec3.y;
        double offZ = getZ() + vec3.z;
        setPos(offX, offY, offZ);
        setDeltaMovement(vec3.add(0.0, -0.24, 0.0));
    }
}
