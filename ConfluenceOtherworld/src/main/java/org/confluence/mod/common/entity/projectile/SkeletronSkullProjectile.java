package org.confluence.mod.common.entity.projectile;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * 骷髅王在专家及以上难度发射的敌对骷髅弹。
 *
 * <p>该实体与玩家法术使用的追踪骷髅弹完全分离。它只追踪骷髅王在生成时指定的目标，
 * 不会重新搜索其他生物，也不会继承玩家弹幕的穿透次数和法术伤害结算。</p>
 */
public final class SkeletronSkullProjectile extends StraightMonsterProjectile {
    private static final double INITIAL_SPEED = 0.001;
    private static final double TRACKING_STRENGTH = 0.025;
    private static final double ACCELERATION = 0.03;

    private LivingEntity target;

    public SkeletronSkullProjectile(
            EntityType<? extends SkeletronSkullProjectile> type,
            Level level) {
        super(type, level);
    }

    /**
     * 从骷髅王中心朝目标当前位置发射，并保留目标供后续追踪。
     *
     * <p>目标引用只参与服务端轨迹计算；客户端依靠实体速度同步显示，不另行执行索敌。</p>
     */
    public void configure(Mob owner, LivingEntity target, float damage) {
        this.target = target;
        Vec3 direction = target.position()
                .subtract(owner.position())
                .normalize();
        super.configure(
                owner,
                owner.position(),
                direction.scale(INITIAL_SPEED),
                damage,
                100);
    }

    @Override
    protected Vec3 modifyVelocity(Vec3 velocity) {
        if (level().isClientSide || target == null
                || velocity.lengthSqr() <= 1.0E-12) {
            return velocity;
        }

        Vec3 toTarget = target.position().add(0.0, 1.0, 0.0)
                .subtract(position());
        double projection = velocity.dot(toTarget) / velocity.lengthSqr();
        Vec3 lateralCorrection = toTarget.subtract(velocity.scale(projection));
        if (lateralCorrection.lengthSqr() <= 1.0E-12) {
            return velocity;
        }
        return velocity.add(lateralCorrection.normalize().scale(TRACKING_STRENGTH));
    }

    /**
     * 1.21 在完成本刻位移后沿当前方向线性加速，下一刻再以新速度继续追踪。
     */
    @Override
    public void tick() {
        super.tick();
        if (isRemoved()) {
            return;
        }
        Vec3 velocity = getDeltaMovement();
        if (velocity.lengthSqr() > 1.0E-12) {
            setDeltaMovement(velocity.add(velocity.normalize().scale(ACCELERATION)));
        }
    }
}
