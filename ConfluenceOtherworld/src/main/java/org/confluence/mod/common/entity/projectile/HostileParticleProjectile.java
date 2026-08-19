package org.confluence.mod.common.entity.projectile;

import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

/// 使用粒子表现的敌对生物弹幕。
///
/// <p>每个注册实体类型在构造时固定一种 {@link Variant}，客户端只需根据实体类型即可
/// 还原外观和命中特效，不必为运行期间不会改变的数据额外同步字段。行为树负责请求创建
/// 弹幕，具体粒子和附加效果由弹幕变种自身保存。</p>
public final class HostileParticleProjectile extends StraightMonsterProjectile {
    private static final int MAXIMUM_LIFETIME = 100;
    private static final float VELOCITY = 0.3F;
    private static final float INACCURACY = 0.8F;

    private final Variant variant;

    public HostileParticleProjectile(EntityType<? extends HostileParticleProjectile> type, Level level, Variant variant) {
        super(type, level);
        this.variant = variant;
    }

    /// 使用普通施法怪物的低速、有轻微散布参数瞄准目标。
    public void configure(Mob owner, LivingEntity target, float damage) {
        Vec3 origin = owner.getEyePosition();
        Vec3 aim = new Vec3(target.getX() - owner.getX(), target.getY() - target.getBbHeight() * 0.3F - owner.getY(), target.getZ() - owner.getZ());
        configureAimed(owner, origin, aim, damage, VELOCITY, INACCURACY, MAXIMUM_LIFETIME);
    }

    public Variant getVariant() {
        return variant;
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide || isRemoved()) {
            return;
        }
        Vec3 movement = getDeltaMovement();
        for (int i = 0; i < 3; i++) {
            level().addParticle(variant.particle(), getRandomX(0.5), getRandomY(), getRandomZ(0.5), movement.x, movement.y, movement.z);
        }
    }

    @Override
    protected void onSuccessfulHit(Mob owner, LivingEntity target) {
        switch (variant) {
            case VILE_SPIT ->
                    target.addEffect(new MobEffectInstance(MobEffects.HUNGER, 100), owner);
            case FIRE_IMP -> target.setSecondsOnFire(4);
            case DARK_CASTER, GASTROPOD,
                 WALL_OF_FLESH_LASER -> {
                // 这些能量弹幕只结算直接伤害，不附加状态。
            }
        }
    }

    /// 外观和命中特性由注册类型固定，禁止在运行期间临时切换。
    public enum Variant {
        DARK_CASTER(ParticleTypes.SOUL),
        VILE_SPIT(ParticleTypes.WITCH),
        FIRE_IMP(ParticleTypes.FLAME),
        GASTROPOD(new DustParticleOptions(new Vector3f(1.0F, 0.2F, 0.8F), 1.15F)),
        WALL_OF_FLESH_LASER(new DustParticleOptions(new Vector3f(0.72F, 0.08F, 0.62F), 1.35F));

        private final ParticleOptions particle;

        Variant(ParticleOptions particle) {
            this.particle = particle;
        }

        ParticleOptions particle() {
            return particle;
        }
    }
}
