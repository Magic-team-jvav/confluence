package org.confluence.mod.common.entity.projectile.mana;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.ModEntities;
import org.mesdag.particlestorm.PSGameClient;
import org.mesdag.particlestorm.particle.ParticleEmitter;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

// todo 专家模式下，黄金雨对毁灭者及其探测怪仅造成 75% 伤害。
public class GoldenShowerProjectile extends AbstractManaProjectile {
    private final Set<UUID> penetrateSet = new HashSet<>();
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
        }

        Vec3 vec3 = getDeltaMovement();
        double offX = getX() + vec3.x;
        double offY = getY() + vec3.y;
        double offZ = getZ() + vec3.z;
        setPos(offX, offY, offZ);
        setDeltaMovement(vec3.add(0.0, -0.24, 0.0));
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (level().isClientSide) return;
        discard();
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity entity = result.getEntity();
        if (penetrateSet.add(entity.getUUID())) {
            if (doHurtAndKnockback(entity, 3.5, 0.2) && entity instanceof LivingEntity living) {
                living.addEffect(new MobEffectInstance(ModEffects.ICHOR, 200));
            }
            if (penetrateSet.size() >= 4) {
                discard();
            }
        }
    }
}
