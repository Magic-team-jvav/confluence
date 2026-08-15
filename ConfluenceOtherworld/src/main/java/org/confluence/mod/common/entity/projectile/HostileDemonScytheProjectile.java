package org.confluence.mod.common.entity.projectile;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.common.entitiy.IAxisZRotate;

/**
 * 恶魔生物发射的镰刀弹幕。
 *
 * <p>它与玩家法杖生成的恶魔镰刀使用相同的外观和后段加速规律，
 * 但伤害来源、所有者约束和生命周期属于敌对生物攻击，二者不能共享
 * 玩家法力弹幕的持久化与战斗快照状态。</p>
 */
public final class HostileDemonScytheProjectile
        extends StraightMonsterProjectile implements IAxisZRotate {
    private static final double MAX_SPEED_SQUARED = 2.18300625;
    private static final double ACCELERATION = 1.1940371819652;
    public final Rotate rotate = new Rotate();

    public HostileDemonScytheProjectile(
            EntityType<? extends HostileDemonScytheProjectile> type,
            Level level) {
        super(type, level);
    }

    public void configure(Mob owner, LivingEntity target, float damage) {
        super.configure(owner, target, damage, 0.2F, 2.0F, 100);
    }

    @Override
    protected Vec3 modifyVelocity(Vec3 velocity) {
        if (tickCount > 10 && velocity.lengthSqr() < MAX_SPEED_SQUARED) {
            return velocity.scale(ACCELERATION);
        }
        return velocity;
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) {
            rotateZ(rotate, getDeltaMovement().lengthSqr(), 0.125F);
        }
    }
}
