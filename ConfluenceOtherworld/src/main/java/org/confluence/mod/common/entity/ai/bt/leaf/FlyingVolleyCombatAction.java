package org.confluence.mod.common.entity.ai.bt.leaf;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.projectile.Projectile;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;

/// 飞行敌怪的接近与定时齐射循环。
///
/// 循环前半段复用平滑冲撞动作，使生物继续追踪并掠过目标；进入施法窗口后停止
/// 冲撞校正、逐刻衰减速度，并在指定 tick 生成弹幕。最后一发完成后立即开始下一轮，
/// 因此具体生物只需声明射击时间表，不必各自复制冷却、转向和入世逻辑。
///
/// 弹幕的类型、伤害、速度和额外效果仍由实体自己的工厂负责。本节点只验证时间表、
/// 调用工厂并在服务端把已经配置好的弹幕加入世界。
public final class FlyingVolleyCombatAction extends BTNode {
    private final PathfinderMob mob;
    private final SteeringDashAction approachAction;
    private final Function<LivingEntity, @Nullable Projectile>
            projectileFactory;
    private final int approachTicks;
    private final int[] shotTicks;
    private int cycleTick;

    public FlyingVolleyCombatAction(PathfinderMob mob, SteeringDashAction approachAction, Function<LivingEntity, @Nullable Projectile> projectileFactory, int approachTicks, int... shotTicks) {
        this.mob = Objects.requireNonNull(mob, "mob");
        this.approachAction = Objects.requireNonNull(approachAction, "approachAction");
        this.projectileFactory = Objects.requireNonNull(projectileFactory, "projectileFactory");
        if (approachTicks < 0) {
            throw new IllegalArgumentException("Approach time cannot be negative");
        }
        if (shotTicks.length == 0) {
            throw new IllegalArgumentException("Volley schedule must contain at least one shot");
        }
        this.shotTicks = shotTicks.clone();
        int previous = approachTicks;
        for (int shotTick : this.shotTicks) {
            if (shotTick <= previous) {
                throw new IllegalArgumentException("Volley shot ticks must be strictly increasing and after the approach phase");
            }
            previous = shotTick;
        }
        this.approachTicks = approachTicks;
    }

    @Override
    public void start() {
        cycleTick = 0;
        approachAction.start();
    }

    @Override
    public BTStatus execute() {
        LivingEntity target = mob.getTarget();
        if (target == null || !target.isAlive()) {
            return BTStatus.FAILURE;
        }

        cycleTick++;
        if (cycleTick <= approachTicks) {
            return approachAction.execute();
        }

        mob.getLookControl().setLookAt(target, 5.0F, 80.0F);
        mob.setDeltaMovement(mob.getDeltaMovement().scale(0.95));
        if (Arrays.binarySearch(shotTicks, cycleTick) >= 0 && !spawnProjectile(target)) {
            return BTStatus.FAILURE;
        }

        if (cycleTick >= shotTicks[shotTicks.length - 1]) {
            cycleTick = 0;
            approachAction.start();
        }
        return BTStatus.RUNNING;
    }

    private boolean spawnProjectile(LivingEntity target) {
        Projectile projectile = projectileFactory.apply(target);
        return projectile != null
                && mob.level().addFreshEntity(projectile);
    }
}
