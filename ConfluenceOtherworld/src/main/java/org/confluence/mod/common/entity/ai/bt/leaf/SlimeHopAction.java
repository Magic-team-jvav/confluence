package org.confluence.mod.common.entity.ai.bt.leaf;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTStatus;
import org.confluence.terra_curio.mixin.accessor.LivingEntityAccessor;

/**
 * 史莱姆跳跃：蓄力→起跳→落地。towardTarget 决定向目标跳还是随机跳。
 */
public class SlimeHopAction extends BTNode {
    protected final Mob mob;
    protected final boolean towardTarget;
    protected int tick;
    protected static final int PRE_JUMP_TICKS = 5;
    protected static final int TIMEOUT = 60;

    public SlimeHopAction(Mob mob, boolean towardTarget) {
        this.mob = mob;
        this.towardTarget = towardTarget;
    }

    @Override
    public void start() {
        tick = 0;
    }

    @Override
    public BTStatus execute() {
        tick++;

        if (tick <= PRE_JUMP_TICKS) {
            return BTStatus.RUNNING;
        }

        if (tick == PRE_JUMP_TICKS + 1) {
            Vec3 dir;
            if (towardTarget && mob.getTarget() != null) {
                Vec3 toTarget = mob.getTarget().position().subtract(mob.position());
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
            mob.setDeltaMovement(dir.x * h, jumpPower, dir.z * h);
            mob.hasImpulse = true;
            return BTStatus.RUNNING;
        }

        if (mob.onGround() && tick > PRE_JUMP_TICKS + 2) {
            return BTStatus.SUCCESS;
        }

        if (tick > TIMEOUT) {
            return BTStatus.SUCCESS;
        }

        return BTStatus.RUNNING;
    }
}
