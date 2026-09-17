package org.confluence.mod.common.entity.projectile;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/// 毁灭者及其探测器共用的直线激光弹幕。
///
/// 发射时一次性计算方向，后续只执行碰撞和匀速移动。目标转向、死亡或离开区块都
/// 不会让已经存在的激光重新寻敌。
public final class DestroyerLaserProjectile extends StraightMonsterProjectile {
    public DestroyerLaserProjectile(EntityType<? extends DestroyerLaserProjectile> type, Level level) {
        super(type, level);
    }

    public void configure(Mob owner, Vec3 origin, LivingEntity target, float damage) {
        Vec3 direction = target.getEyePosition().subtract(origin).normalize();
        super.configure(owner, origin, direction.scale(1.5), damage, 80);
    }
}
