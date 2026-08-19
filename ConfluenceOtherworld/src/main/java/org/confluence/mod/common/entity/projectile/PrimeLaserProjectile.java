package org.confluence.mod.common.entity.projectile;

import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

/// 机械骷髅王激光臂发射的直线弹幕。
///
/// <p>弹幕仅在生成时计算一次方向，之后保持匀速飞行并正常与方块、实体碰撞。
/// 目标后续移动不会改变弹道，因此玩家可以通过走位躲避，而不是承受无法规避的瞬时射线。</p>
public final class PrimeLaserProjectile extends StraightMonsterProjectile {
    private static final DustParticleOptions TRAIL = new DustParticleOptions(new Vector3f(1.0F, 0.12F, 0.08F), 1.25F);

    public PrimeLaserProjectile(EntityType<? extends PrimeLaserProjectile> type, Level level) {
        super(type, level);
    }

    /// 按当前目标位置配置弹道，不在后续 tick 中重新索敌。
    public void configure(Mob owner, Vec3 origin, LivingEntity target, float damage) {
        Vec3 direction = target.getEyePosition().subtract(origin);
        if (direction.lengthSqr() <= 1.0E-7) {
            direction = owner.getLookAngle();
        }
        super.configure(owner, origin, direction.normalize().scale(1.55), damage, 80);
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide || isRemoved()) {
            return;
        }
        Vec3 movement = getDeltaMovement();
        for (int index = 0; index < 3; index++) {
            level().addParticle(TRAIL, getRandomX(0.25), getRandomY(), getRandomZ(0.25), movement.x, movement.y, movement.z);
        }
    }
}
