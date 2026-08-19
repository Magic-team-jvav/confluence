package org.confluence.mod.common.entity.projectile;

import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

/// 双子魔眼使用的真实直线弹幕。
///
/// <p>激光与魔焰拥有独立注册类型，但共用碰撞和存活期；变种只负责外观与命中附加
/// 效果，避免两份几乎相同的弹道代码。</p>
public final class TwinEyeProjectile extends StraightMonsterProjectile {
    private final Variant variant;

    public TwinEyeProjectile(EntityType<? extends TwinEyeProjectile> type, Level level, Variant variant) {
        super(type, level);
        this.variant = variant;
    }

    public void configure(Mob owner, LivingEntity target, float damage, float velocity, float inaccuracy) {
        super.configure(owner, target, damage, velocity, inaccuracy, 100);
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide || isRemoved()) {
            return;
        }
        Vec3 movement = getDeltaMovement();
        for (int i = 0; i < 3; i++) {
            level().addParticle(variant.particle, getRandomX(0.35), getRandomY(), getRandomZ(0.35), movement.x, movement.y, movement.z);
        }
    }

    @Override
    protected void onSuccessfulHit(Mob owner, LivingEntity target) {
        if (variant == Variant.CURSED_FLAME) {
            target.setSecondsOnFire(5);
        }
    }

    public enum Variant {
        LASER(new DustParticleOptions(new Vector3f(1.0F, 0.12F, 0.12F), 1.25F)),
        CURSED_FLAME(ParticleTypes.FLAME);

        private final ParticleOptions particle;

        Variant(ParticleOptions particle) {
            this.particle = particle;
        }
    }
}
