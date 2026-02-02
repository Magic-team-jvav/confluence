package org.confluence.terraentity.entity.ai.goal.behavior.leaf;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;
import org.confluence.terraentity.entity.ai.goal.behavior.BTNode;

public class JumpAttackAction extends BTNode {

    final Mob mob;
    final float horizonPower;
    final float jumpAdditionSpeed;

    public JumpAttackAction(Mob mob, float horizonPower, float jumpAdditionSpeed) {
        this.mob = mob;
        this.horizonPower = horizonPower;
        this.jumpAdditionSpeed = jumpAdditionSpeed;
    }

    public JumpAttackAction(Mob mob, float horizonPower) {
        this(mob, horizonPower, 0);
    }

    @Override
    public BTStatus execute() {
        if (this.mob.getTarget() == null) {
            return BTStatus.FAILURE;
        }
        if (!this.mob.onGround()) {
            return BTStatus.RUNNING;
        }

        Vec3 targetPos = this.mob.getTarget().position();
        Vec3 horizonDir = targetPos.subtract(this.mob.position()).multiply(1, 0, 1).normalize();
        this.mob.jumpFromGround();
        this.mob.addDeltaMovement(horizonDir.scale(horizonPower));
        this.mob.addDeltaMovement(new Vec3(0, jumpAdditionSpeed, 0));
        return BTStatus.SUCCESS;
    }
}
