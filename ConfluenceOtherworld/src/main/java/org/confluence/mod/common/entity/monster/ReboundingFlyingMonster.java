package org.confluence.mod.common.entity.monster;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.mixin.world.entity.EntityAccessor;

/// 会在撞到实体方块后主动调整速度的飞行敌怪。
///
/// <p>本类只负责一次碰撞计算和 1.20 移动后的速度写回。不同生物如何反弹由
/// {@link #reboundVelocity(Vec3, Vec3)} 决定：恶魔眼按受阻轴使用固定速度，
/// 鸟妖及恶魔则在水平反向的同时获得少量向上速度。</p>
///
/// <p>1.21 会在检测到碰撞后立即使用反弹向量完成本刻位移，而不是先贴到墙面、下一刻才离开。
/// Forge 1.20 的移动收尾还可能清零受阻速度轴，因此这里在移动前写入反弹向量以还原位移，
/// 并在移动后再次写回同一向量以保留下一刻速度。两次写入只是在不同版本间保持同一轨迹。</p>
public abstract class ReboundingFlyingMonster extends BaseFlyingMonster {
    protected ReboundingFlyingMonster(
            EntityType<? extends Monster> type,
            Level level) {
        super(type, level);
    }

    @Override
    public final void move(MoverType type, Vec3 movement) {
        if (dead) {
            super.move(type, movement);
            return;
        }

        Vec3 allowed = ((EntityAccessor) this).callCollide(movement);
        Vec3 rebound = reboundVelocity(movement, allowed);
        setDeltaMovement(rebound);
        super.move(type, rebound);
        setDeltaMovement(rebound);
    }

    /// 根据请求位移与碰撞后允许位移计算下一刻速度。
    protected abstract Vec3 reboundVelocity(
            Vec3 requested,
            Vec3 allowed);
}
