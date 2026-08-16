package org.confluence.mod.common.entity.projectile.sword;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/// 带逐 tick 速度倍率的直线剑气。
///
/// <p>父类已经用本 tick 的完整移动向量执行一次原版 swept collision，并完成唯一一次位置更新。
/// 本类只为下一 tick 调整速度，不能再次修改位置，否则会产生一段没有碰撞检测的额外位移。</p>
public class ForwardSwordProjectile extends SwordProjectile {
    public ForwardSwordProjectile(EntityType<? extends ForwardSwordProjectile> type, Level level) {
        super(type, level);
    }

    @Override
    public void tick() {
        super.tick();
        if (isRemoved()) {
            return;
        }
        float acceleration = projComponent == null ? 0.8F : projComponent.acceleration();
        setDeltaMovement(getDeltaMovement().scale(acceleration));
    }
}
