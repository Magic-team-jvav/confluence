package org.confluence.mod.common.entity.ai.bt.leaf;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;
import net.minecraft.util.Mth;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTStatus;
import org.confluence.terra_curio.mixin.accessor.LivingEntityAccessor;

/**
 * 负责史莱姆一次完整的蓄力、起跳与落地过程。
 *
 * <p>该节点只实现所有史莱姆共用的跳跃状态，不判断具体变种。蓄力时长由实体类传入，
 * 因而金史莱姆一类只有数值差异的变种无需复制整套行为节点。</p>
 */
public class SlimeHopAction extends BTNode {
    protected final Mob mob;
    protected final boolean towardTarget;
    protected final int preJumpTicks;
    protected int tick;
    protected static final int TIMEOUT = 60;

    public SlimeHopAction(Mob mob, boolean towardTarget) {
        this(mob, towardTarget, 5);
    }

    public SlimeHopAction(Mob mob, boolean towardTarget, int preJumpTicks) {
        if (preJumpTicks < 0) {
            throw new IllegalArgumentException("Slime hop windup must be non-negative");
        }
        this.mob = mob;
        this.towardTarget = towardTarget;
        this.preJumpTicks = preJumpTicks;
    }

    @Override
    public void start() {
        tick = 0;
    }

    @Override
    public BTStatus execute() {
        tick++;

        if (tick <= preJumpTicks) {
            return BTStatus.RUNNING;
        }

        if (tick == preJumpTicks + 1) {
            var target = mob.getTarget();
            Vec3 dir;
            if (towardTarget && target != null) {
                Vec3 toTarget = target.position().subtract(mob.position());
                double hDist = Math.sqrt(toTarget.x * toTarget.x + toTarget.z * toTarget.z);
                dir = hDist > 0.01
                        ? new Vec3(toTarget.x / hDist, 0, toTarget.z / hDist)
                        : Vec3.ZERO;
            } else {
                float yaw = mob.getRandom().nextFloat() * (float) Math.PI * 2;
                dir = new Vec3(-Math.sin(yaw), 0, Math.cos(yaw));
            }

            double jumpPower = ((LivingEntityAccessor) mob).callGetJumpPower();
            double h = jumpPower * 0.7;
            if (dir.lengthSqr() > 1.0E-6) {
                // 跳跃节点直接写入速度，不会像原版 MoveControl 那样自动更新朝向，因此必须在起跳时同步身体方向。
                float wantedYaw = (float) (Mth.atan2(-dir.x, dir.z) * Mth.RAD_TO_DEG);
                mob.setYRot(wantedYaw);
                mob.setYBodyRot(wantedYaw);
                mob.setYHeadRot(wantedYaw);
            }
            mob.setDeltaMovement(dir.x * h, jumpPower, dir.z * h);
            mob.hasImpulse = true;
            return BTStatus.RUNNING;
        }

        if (mob.onGround() && tick > preJumpTicks + 2) {
            return BTStatus.SUCCESS;
        }

        if (tick > TIMEOUT) {
            return BTStatus.SUCCESS;
        }

        return BTStatus.RUNNING;
    }
}
