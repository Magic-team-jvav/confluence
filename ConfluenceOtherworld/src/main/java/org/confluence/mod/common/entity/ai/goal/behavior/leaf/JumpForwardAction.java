package org.confluence.mod.common.entity.ai.goal.behavior.leaf;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.goal.behavior.BTNode;

public class JumpForwardAction extends BTNode {

    final Mob mob;
    final float horizonPower;
    final float jumpAdditionSpeed;

    public JumpForwardAction(Mob mob, float horizonPower, float jumpAdditionSpeed) {
        this.mob = mob;
        this.horizonPower = horizonPower;
        this.jumpAdditionSpeed = jumpAdditionSpeed;
    }

    public JumpForwardAction(Mob mob, float horizonPower) {
        this(mob, horizonPower, 0);
    }

    @Override
    public BTStatus execute() {
        if (!this.mob.onGround()) {
            return BTStatus.RUNNING;
        }
        Vec3 horizonDir = this.mob.getForward().multiply(1,0,1).normalize();
        this.mob.jumpFromGround();
        this.mob.addDeltaMovement(horizonDir.scale(horizonPower));
        this.mob.addDeltaMovement(new Vec3(0, jumpAdditionSpeed, 0));
        return BTStatus.SUCCESS;
    }
}
