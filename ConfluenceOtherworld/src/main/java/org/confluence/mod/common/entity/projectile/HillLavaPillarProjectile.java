package org.confluence.mod.common.entity.projectile;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

/// 血肉山二阶段生成的熔岩柱。
///
/// <p>前 50 tick 只显示烟雾预警，随后持续 40 tick 喷发，并且每 10 tick
/// 结算一次范围伤害。伤害区域固定在生成位置，不会追踪目标，因此玩家能够在预警期间主动离开。</p>
public final class HillLavaPillarProjectile extends Projectile {
    private static final int WARNING_TICKS = 50;
    private static final int ACTIVE_TICKS = 40;
    private static final int MAX_LIFETIME = WARNING_TICKS + ACTIVE_TICKS;
    private float damage;

    public HillLavaPillarProjectile(EntityType<? extends HillLavaPillarProjectile> type, Level level) {
        super(type, level);
        setNoGravity(true);
    }

    /// 绑定生成该熔岩柱的 Boss，并保存服务端伤害值。
    public void configure(Mob owner, float damage) {
        setOwner(owner);
        this.damage = damage;
    }

    @Override
    protected void defineSynchedData() {}

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) {
            return;
        }
        if (tickCount >= MAX_LIFETIME) {
            discard();
            return;
        }
        if (tickCount < WARNING_TICKS) {
            if (tickCount % 4 == 0 && level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.SMOKE, getX(), getY() + 0.2, getZ(), 4, 0.0, 0.5, 0.0, 0.0);
            }
            return;
        }

        if (tickCount % 8 == 0 && level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.LAVA, getX(), getY() + 1.0, getZ(), 10, 0.0, 1.0, 0.0, 0.0);
        }
        if (tickCount % 10 == 0) {
            damageEntities();
        }
    }

    /// 对固定柱体范围内、可被所有者攻击的生物结算伤害。
    ///
    /// <p>喷发后半段会向水平方向扩张一格，让视觉上的熔岩外溅与判定范围保持一致。</p>
    private void damageEntities() {
        AABB area = getBoundingBox().inflate(tickCount > WARNING_TICKS + ACTIVE_TICKS / 2 ? 1.0 : 0.0);
        area = area.setMaxY(getY() + 3.0);
        for (LivingEntity target : level().getEntitiesOfClass(LivingEntity.class, area)) {
            if (getOwner() instanceof Mob owner && owner.canAttack(target)) {
                target.hurt(damageSources().mobProjectile(this, owner), damage);
            }
        }
    }
}
