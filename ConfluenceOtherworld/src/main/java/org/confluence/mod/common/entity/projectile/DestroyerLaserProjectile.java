package org.confluence.mod.common.entity.projectile;

import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

/// 毁灭者及其探测器共用的直线激光弹幕。
///
/// <p>发射时一次性计算方向，后续只执行碰撞和匀速移动。目标转向、死亡或离开区块都
/// 不会让已经存在的激光重新寻敌。</p>
public final class DestroyerLaserProjectile
        extends StraightMonsterProjectile {
    private static final DustParticleOptions TRAIL =
            new DustParticleOptions(
                    new Vector3f(0.15F, 0.85F, 1.0F), 1.2F);

    public DestroyerLaserProjectile(
            EntityType<? extends DestroyerLaserProjectile> type,
            Level level) {
        super(type, level);
    }

    public void configure(
            Mob owner,
            Vec3 origin,
            LivingEntity target,
            float damage) {
        Vec3 direction =
                target.getEyePosition().subtract(origin).normalize();
        super.configure(
                owner,
                origin,
                direction.scale(1.5),
                damage,
                80);
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide || isRemoved()) {
            return;
        }
        Vec3 movement = getDeltaMovement();
        for (int i = 0; i < 3; i++) {
            level().addParticle(
                    TRAIL,
                    getRandomX(0.25),
                    getRandomY(),
                    getRandomZ(0.25),
                    movement.x,
                    movement.y,
                    movement.z);
        }
    }
}
