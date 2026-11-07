package org.confluence.mod.common.entity.projectile.mana;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.entity.ModEntities;
import org.jetbrains.annotations.Nullable;

/// 昏晓的月光弹幕 / 小月光。
///
/// 弹幕命中敌怪后变成挂在它头顶的小月光，持续 7 秒；每 2 秒朝附近敌怪（不含宿主）
/// 射出一道月光射线，造成 80% 伤害。宿主死亡时，小月光瞄准附近的敌怪把自己再射出去。
/// 同一敌怪头顶至多存在一个小月光，且与小日光互斥。
public class LittleMoonEntity extends AbstractLittleCelestialEntity {
    /// 附着持续时间（7 秒）
    public static final int ATTACHED_LIFETIME = 140;
    /// 月光射线间隔（2 秒）
    public static final int RAY_INTERVAL = 40;
    /// 月光射线伤害倍率（80%）
    public static final float RAY_FACTOR = 0.8F;

    public LittleMoonEntity(EntityType<LittleMoonEntity> entityType, Level level) {
        super(entityType, level);
        withParticle(Confluence.asResource("little_moon_trail"));
    }

    public LittleMoonEntity(LivingEntity shooter, float damage) {
        this(ModEntities.LITTLE_MOON.get(), shooter.level());
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
        if (ticks % RAY_INTERVAL == 0) ray(host);
    }

    /// 朝宿主之外的最近敌怪射出一道月光射线（80% 伤害）
    public void ray(@Nullable LivingEntity host) {
        LivingEntity target = findNearestEnemy(host);
        if (target == null) return;
        if (level() instanceof ServerLevel serverLevel) {
            target.invulnerableTime = 0;
            target.hurt(getDamageSource(), getCalculatedDamage() * RAY_FACTOR);
            spawnRayParticles(serverLevel, target);
        }
    }

    /// 沿着射线方向撒一串月白色光点
    private void spawnRayParticles(ServerLevel level, LivingEntity target) {
        Vec3 from = position();
        Vec3 to = target.getBoundingBox().getCenter();
        double distance = from.distanceTo(to);
        int steps = Math.max(1, (int) Math.ceil(distance * 3.0));
        for (int i = 0; i <= steps; i++) {
            Vec3 point = from.lerp(to, i / (double) steps);
            level.sendParticles(ParticleTypes.END_ROD, point.x, point.y, point.z, 1, 0.0, 0.0, 0.0, 0.0);
            if ((i & 3) == 0) {
                level.sendParticles(ParticleTypes.ELECTRIC_SPARK, point.x, point.y, point.z, 1, 0.02, 0.02, 0.02, 0.0);
            }
        }
    }

    @Override
    protected void onAttached(LivingEntity host) {
        if (level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.END_ROD, getX(), getY(), getZ(), 10, 0.25, 0.25, 0.25, 0.01);
        }
    }

    @Override
    protected void onExpire() {
        discard();
    }

    /// 宿主死亡：瞄准附近的敌怪把自身再射出去
    @Override
    protected void onHostLost() {
        fireAtNearest(null, false);
    }

    @Override
    protected AbstractLittleCelestialEntity createChild(boolean inertChild) {
        LittleMoonEntity child = new LittleMoonEntity(getLivingOwner(), getDamage());
        child.setInert(inertChild);
        return child;
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
