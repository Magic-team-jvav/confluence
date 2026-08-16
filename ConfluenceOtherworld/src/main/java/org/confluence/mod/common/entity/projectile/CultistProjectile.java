package org.confluence.mod.common.entity.projectile;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/// 拜月教邪教徒三种基础法术的共享真实弹幕。
///
/// <p>火球、冰雾与闪电球使用独立注册类型，客户端无需依赖额外同步字段判断表现。
/// 三者都在发射时锁定弹道，不会因为玩家随后移动而自动转向；差异只保留在速度变化、
/// 命中效果和粒子表现中。</p>
public final class CultistProjectile extends StraightMonsterProjectile {
    private final Variant variant;

    public CultistProjectile(
            EntityType<? extends CultistProjectile> type,
            Level level,
            Variant variant) {
        super(type, level);
        this.variant = variant;
    }

    public void configure(
            Mob owner,
            LivingEntity target,
            float damage,
            float velocity) {
        super.configure(owner, target, damage, velocity, 0.0F, 120);
    }

    @Override
    protected Vec3 modifyVelocity(Vec3 velocity) {
        return switch (variant) {
            case FIREBALL -> velocity;
            case ICE_MIST -> velocity.scale(0.992);
            case LIGHTNING_ORB -> velocity.lengthSqr() < 1.44
                    ? velocity.scale(1.012)
                    : velocity;
        };
    }

    @Override
    protected void onSuccessfulHit(Mob owner, LivingEntity target) {
        if (variant == Variant.FIREBALL) {
            target.setSecondsOnFire(4);
        } else if (variant == Variant.ICE_MIST) {
            target.addEffect(new MobEffectInstance(
                    MobEffects.MOVEMENT_SLOWDOWN, 80, 1));
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide || isRemoved()) {
            return;
        }
        for (int index = 0; index < variant.particleCount; index++) {
            level().addParticle(
                    variant.particle,
                    getRandomX(0.35),
                    getRandomY(),
                    getRandomZ(0.35),
                    0.0, 0.0, 0.0);
        }
    }

    public enum Variant {
        FIREBALL(ParticleTypes.FLAME, 2),
        ICE_MIST(ParticleTypes.SNOWFLAKE, 3),
        LIGHTNING_ORB(ParticleTypes.ELECTRIC_SPARK, 3);

        private final ParticleOptions particle;
        private final int particleCount;

        Variant(ParticleOptions particle, int particleCount) {
            this.particle = particle;
            this.particleCount = particleCount;
        }
    }
}
