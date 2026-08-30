package org.confluence.mod.common.entity.projectile;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/// 鸟妖发射的直线羽毛弹幕。
///
/// 伤害只在服务端由鸟妖攻击属性的发射快照确定。弹幕具有真实飞行时间和方块碰撞，
/// 因而玩家可以观察轨迹、躲避攻击或利用墙体阻挡，不再被无预警的瞬时射线命中。
public final class HarpyFeatherProjectile extends StraightMonsterProjectile {
    public static final int MAX_LIFETIME = 100;

    public HarpyFeatherProjectile(EntityType<? extends HarpyFeatherProjectile> type, Level level) {
        super(type, level);
    }

    public void configure(Mob owner, LivingEntity target, float damage, float velocity, float inaccuracy) {
        Vec3 aim = new Vec3(target.getX() - owner.getX(), target.getY() - owner.getY(), target.getZ() - owner.getZ());
        configureAimed(owner, owner.getEyePosition(), aim, damage, velocity, inaccuracy, MAX_LIFETIME);
    }
}
