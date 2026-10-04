package org.confluence.mod.common.entity.ai.bt.leaf;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTStatus;
import org.confluence.mod.common.entity.monster.BaseMonster;

/// 恶魔眼在夜间锁定玩家后的追击行为。
///
/// 行为模仿泰拉恶魔眼：先短暂悬停蓄势并朝向玩家，然后沿当前方向直线冲撞，命中或
/// 超时后进入下一轮。它不做绕圈巡航 —— 绕圈会让航点始终保持在玩家周围若干格之外，
/// 既不会发生接触伤害（`hasEntityContactAttack` 依赖身体碰撞），也就没有"冲撞攻击"
/// 可言。
///
/// 单次冲撞的飞行方向在开冲瞬间锁定，中途不再修正：因此转弯是"一轮一次"的，
/// 天然形成泰拉里那种偏慢的转向手感，而不是贴脸追踪。
public final class DemonEyeSurroundAction extends BTNode {
    /// 悬停蓄势时长，单位为刻。
    private static final int HOVER_TICKS = 14;
    /// 单次冲撞的最长时长，超过后强制进入下一轮，避免在障碍后长时间贴墙。
    private static final int CHARGE_TICKS = 36;
    /// 开冲时向玩家方向施加的初始速度。
    private static final double CHARGE_IMPULSE = 0.32D;
    /// 冲撞途中的持续加速度（沿锁定方向）。
    private static final double STEER_ACCELERATION = 0.06D;
    /// 冲撞途中允许的最大速度。
    private static final double MAX_SPEED = 0.75D;
    /// 水面规避探测距离，单位为方块。
    private static final double WATER_LOOKAHEAD = 1.5D;

    private final BaseMonster mob;
    private int stateTicks;
    private boolean charging;
    private Vec3 chargeDirection = Vec3.ZERO;

    public DemonEyeSurroundAction(BaseMonster mob) {
        this.mob = mob;
    }

    @Override
    public void start() {
        charging = false;
        stateTicks = 0;
        chargeDirection = Vec3.ZERO;
        // 立即锁定一次朝向，避免刚进入追击时还沿用上一段游荡的朝向。
        faceTarget();
    }

    @Override
    public BTStatus execute() {
        LivingEntity target = mob.getTarget();
        if (target == null || !target.isAlive() || !mob.level().isNight()) {
            return BTStatus.SUCCESS;
        }

        if (!charging) {
            return tickHover(target);
        }
        return tickCharge();
    }

    /// 悬停蓄势：把水平速度压下来，让身体停在原地并转向玩家。
    private BTStatus tickHover(LivingEntity target) {
        Vec3 movement = mob.getDeltaMovement();
        Vec3 damped = new Vec3(movement.x * 0.7D, movement.y * 0.85D, movement.z * 0.7D);
        mob.setDeltaMovement(damped);
        mob.hasImpulse = true;
        faceTarget();
        if (++stateTicks >= HOVER_TICKS) {
            beginCharge(target);
        }
        return BTStatus.RUNNING;
    }

    /// 开始一次冲撞：锁定方向并给出初速。
    private void beginCharge(LivingEntity target) {
        Vec3 aim = target.getEyePosition().subtract(mob.getEyePosition());
        chargeDirection = aim.lengthSqr() < 1.0E-6D ? mob.getViewVector(1.0F) : aim.normalize();
        mob.setDeltaMovement(chargeDirection.scale(CHARGE_IMPULSE));
        mob.hasImpulse = true;
        stateTicks = 0;
        charging = true;
    }

    /// 冲撞推进：沿锁定方向继续加速，命中减速或超时后回到悬停。
    private BTStatus tickCharge() {
        Vec3 movement = mob.getDeltaMovement();
        Vec3 next = movement.add(chargeDirection.scale(STEER_ACCELERATION));
        if (next.length() > MAX_SPEED) {
            next = next.normalize().scale(MAX_SPEED);
        }
        // 迎面有水就抬高，把冲撞抬过水面；否则恶魔眼会一头扎进水里。
        next = next.add(waterAvoidance(next));
        mob.setDeltaMovement(next);
        mob.faceCombatDirection(next, 30.0F, 85.0F);
        mob.hasImpulse = true;

        stateTicks++;
        // 撞到东西会明显掉速，这时就该收势，让下一轮重新瞄准。
        if (stateTicks >= CHARGE_TICKS || movement.length() < CHARGE_IMPULSE * 0.4D) {
            charging = false;
            stateTicks = 0;
            chargeDirection = Vec3.ZERO;
        }
        return BTStatus.RUNNING;
    }

    /// 若沿给定方向前进一小段会进入液体，则返回一个向上分量。
    private Vec3 waterAvoidance(Vec3 direction) {
        if (direction.lengthSqr() < 1.0E-8D) {
            return Vec3.ZERO;
        }
        Vec3 probe = mob.getEyePosition().add(direction.normalize().scale(WATER_LOOKAHEAD));
        BlockPos pos = BlockPos.containing(probe);
        if (!mob.level().getFluidState(pos).is(FluidTags.WATER)) {
            return Vec3.ZERO;
        }
        return new Vec3(0.0D, 0.06D, 0.0D);
    }

    /// 蓄势时面向目标，冲撞时改由实际飞行方向控制朝向。
    private void faceTarget() {
        LivingEntity target = mob.getTarget();
        if (target == null) {
            return;
        }
        mob.faceCombatPosition(target.getEyePosition(), 30.0F, 85.0F);
    }

    /// 碰撞改变垂直速度时同步清理冲撞状态，避免继续朝同一块天花板或地面推进。
    public void adjustTargetAfterVerticalCollision(boolean movingDown) {
        if (charging) {
            charging = false;
            stateTicks = 0;
            chargeDirection = Vec3.ZERO;
        }
        // 抬高或压低一点目标高度，让下一轮冲撞不再重复撞同一个面。
        Vec3 movement = mob.getDeltaMovement();
        mob.setDeltaMovement(new Vec3(movement.x, movingDown ? Mth.clamp(-movement.y, 0.1D, 0.22D) : movement.y, movement.z));
    }
}
