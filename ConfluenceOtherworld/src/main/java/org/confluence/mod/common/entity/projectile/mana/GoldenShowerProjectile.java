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

// todo 专家模式下，黄金雨对毁灭者及其探测怪仅造成 75% 伤害。
public class GoldenShowerProjectile extends AbstractManaProjectile {
    private final Set<Entity> passThrough = new HashSet<>();
    private ParticleEmitter emitter;

    public GoldenShowerProjectile(EntityType<GoldenShowerProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public GoldenShowerProjectile(LivingEntity living) {
        this(ModEntities.GOLDEN_SHOWER_PROJECTILE.get(), living.level());
        setOwner(living);
        setPos(living.getX(), living.getEyeY() - 0.1, living.getZ());
    }

    @Override
    public void baseTick() {
        super.baseTick();
        if (level().isClientSide) {
            if (emitter == null || emitter.isRemoved()) {
                this.emitter = new ParticleEmitter(level(), position(), Confluence.asResource("golden_shower"));
                emitter.attachEntity(this);
                PSGameClient.LOADER.addEmitter(emitter, false);
            }
        } else {
            HitResult hitResult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
            if (hitResult.getType() == HitResult.Type.BLOCK) {
                discard();
            } else if (hitResult instanceof EntityHitResult entityHitResult) {
                Entity entity = entityHitResult.getEntity();
                if (passThrough.add(entity)) {
                    if (entity.hurt(getDamagesource(), getCalculatedDamage())) {
                        VectorUtils.knockBackA2B(this, entity, 3.5, 0.2);
                    }
                    if (passThrough.size() >= 4) {
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
