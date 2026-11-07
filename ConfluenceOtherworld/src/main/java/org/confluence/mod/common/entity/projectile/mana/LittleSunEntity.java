package org.confluence.mod.common.entity.projectile.mana;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.entity.ModEntities;
import org.jetbrains.annotations.Nullable;

/// 昏晓的日光弹幕 / 小日光。
///
/// 弹幕命中敌怪后变成挂在它头顶的小日光，持续 10 秒；每秒对 5x5 范围内的敌怪造成
/// 10% 伤害。宿主被击杀时，小日光向附近的敌怪补发一发不再附着的日光弹幕。
/// 同一敌怪头顶至多存在一个小日光，且与小月光互斥。
public class LittleSunEntity extends AbstractLittleCelestialEntity {
    /// 附着持续时间（10 秒）
    public static final int ATTACHED_LIFETIME = 200;
    /// 范围伤害间隔（1 秒）
    public static final int PULSE_INTERVAL = 20;
    /// 范围伤害倍率（10%）
    public static final float PULSE_FACTOR = 0.1F;
    /// 范围伤害半径（5x5）
    public static final double PULSE_RADIUS = 2.5;

    public LittleSunEntity(EntityType<LittleSunEntity> entityType, Level level) {
        super(entityType, level);
        withParticle(Confluence.asResource("little_sun_trail"));
    }

    public LittleSunEntity(LivingEntity shooter, float damage) {
        this(ModEntities.LITTLE_SUN.get(), shooter.level());
        setOwner(shooter);
        setDamage(damage);
        setPos(shooter.getX(), shooter.getEyeY() - 0.1, shooter.getZ());
    }

    @Override
    protected int getAttachedLifetime() {
        return ATTACHED_LIFETIME;
    }

    @Override
    protected void tickAttached(LivingEntity host, int ticks) {
        if (ticks % PULSE_INTERVAL == 0) pulse();
    }

    @Override
    public void triggerEffect() {
        pulse();
    }

    /// 一次 5x5 范围、10% 伤害的结算（服务端施加伤害，同时广播一圈粒子）
    public void pulse() {
        if (level() instanceof ServerLevel serverLevel) {
            for (LivingEntity victim : level().getEntitiesOfClass(LivingEntity.class, new AABB(position(), position()).inflate(PULSE_RADIUS))) {
                if (victim == getOwner() || !victim.isAlive() || victim.isSpectator()) continue;
                if (!LibEntityUtils.canHit(getOwner(), victim)) continue;
                victim.invulnerableTime = 0;
                victim.hurt(getDamageSource(), getCalculatedDamage() * PULSE_FACTOR);
            }
            serverLevel.sendParticles(ParticleTypes.END_ROD, getX(), getY(), getZ(),
                    16, PULSE_RADIUS * 0.6, 0.35, PULSE_RADIUS * 0.6, 0.02);
            serverLevel.sendParticles(ParticleTypes.FLAME, getX(), getY(), getZ(),
                    6, PULSE_RADIUS * 0.3, 0.2, PULSE_RADIUS * 0.3, 0.01);
        }
    }

    @Override
    protected void onAttached(LivingEntity host) {
        if (level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.FLAME, getX(), getY(), getZ(), 10, 0.3, 0.3, 0.3, 0.02);
        }
    }

    @Override
    protected void onExpire() {
        discard();
    }

    /// 宿主死亡：瞄准附近的敌怪补发一次不带效果的日光弹幕
    @Override
    protected void onHostLost() {
        fireAtNearest(null, true);
    }

    @Override
    protected AbstractLittleCelestialEntity createChild(boolean inertChild) {
        LittleSunEntity child = new LittleSunEntity(getLivingOwner(), getDamage());
        child.setInert(inertChild);
        return child;
    }

    /// 附近可攻击的敌怪（供外部查询，如异种弹幕命中时的联动）
    public @Nullable LivingEntity nearestOtherTarget(@Nullable LivingEntity exclude) {
        return findNearestEnemy(exclude);
    }

    @Override
    public float getPickRadius() {
        return 0.6F;
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        return distance < 4096.0;
    }
}
