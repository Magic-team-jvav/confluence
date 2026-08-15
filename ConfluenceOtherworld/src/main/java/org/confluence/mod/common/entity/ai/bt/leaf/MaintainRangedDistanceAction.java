package org.confluence.mod.common.entity.ai.bt.leaf;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTStatus;

/**
 * 让飞行远程单位在目标周围维持一个稳定的作战距离带。
 *
 * <p>距离小于下限时远离目标，大于上限时接近目标，位于距离带内时沿随机选定的顺时针
 * 或逆时针方向绕行。判断使用平方距离，避免行为树每 tick 为单纯比较计算平方根；最终
 * 加速度仍会归一化，以保证水平移动和高度修正组合后速度不会异常放大。</p>
 *
 * <p>每次启动只运行固定的 {@code duration}，到期主动成功退出，把调度权交还给上层选择器。
 * 这样远程走位不会长期占用行为树，也能与射击、冲刺等节点轮换。目标丢失或死亡时立即失败。</p>
 */
public final class MaintainRangedDistanceAction extends BTNode {
    private final PathfinderMob mob;
    private final double minimumDistanceSqr;
    private final double maximumDistanceSqr;
    private final double speed;
    private final int duration;
    private int ticks;
    private double orbitDirection;

    public MaintainRangedDistanceAction(
            PathfinderMob mob, double minimumDistance, double maximumDistance,
            double speed, int duration) {
        if (minimumDistance <= 0 || maximumDistance <= minimumDistance) {
            throw new IllegalArgumentException("Ranged distance band must be positive and ordered");
        }
        if (duration <= 0) {
            throw new IllegalArgumentException("Ranged movement duration must be positive");
        }
        this.mob = mob;
        this.minimumDistanceSqr = minimumDistance * minimumDistance;
        this.maximumDistanceSqr = maximumDistance * maximumDistance;
        this.speed = speed;
        this.duration = duration;
    }

    @Override
    public void start() {
        ticks = 0;
        // 一轮行为内方向保持稳定，避免每 tick 随机切换造成左右抖动。
        orbitDirection = mob.getRandom().nextBoolean() ? 1.0 : -1.0;
    }

    @Override
    public BTStatus execute() {
        LivingEntity target = mob.getTarget();
        if (target == null || !target.isAlive()) return BTStatus.FAILURE;
        if (ticks++ >= duration) return BTStatus.SUCCESS;

        Vec3 offset = mob.position().subtract(target.position());
        double distanceSqr = offset.lengthSqr();
        Vec3 horizontalAway = new Vec3(offset.x, 0.0, offset.z);
        if (horizontalAway.lengthSqr() < 1.0E-4) {
            // 与目标几乎重叠时给出确定方向，防止零向量归一化产生非法速度。
            horizontalAway = new Vec3(1.0, 0.0, 0.0);
        } else {
            horizontalAway = horizontalAway.normalize();
        }

        Vec3 direction;
        if (distanceSqr < minimumDistanceSqr) {
            direction = horizontalAway;
        } else if (distanceSqr > maximumDistanceSqr) {
            direction = horizontalAway.scale(-1.0);
        } else {
            direction = new Vec3(-horizontalAway.z * orbitDirection, 0.0,
                    horizontalAway.x * orbitDirection);
        }

        // 以目标视线略上方为飞行高度，减少贴地卡碰撞和远程弹道被地形遮挡的概率。
        double verticalCorrection = (target.getEyeY() + 1.5 - mob.getY()) * 0.08;
        Vec3 acceleration = direction.add(0.0, verticalCorrection, 0.0)
                .normalize().scale(speed * 0.08);
        mob.setDeltaMovement(mob.getDeltaMovement().scale(0.82).add(acceleration));
        mob.getLookControl().setLookAt(target, 30.0F, 30.0F);
        return BTStatus.RUNNING;
    }
}
