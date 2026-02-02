package org.confluence.terraentity.entity.ai.goal.behavior.leaf;

import net.minecraft.world.entity.Mob;
import org.confluence.terraentity.entity.ai.goal.behavior.BTNode;

/**
 * 看向目标
 */
public class LookAtTargetAction extends BTNode {

    final Mob mob;

    public LookAtTargetAction(Mob mob) {
        this.mob = mob;

    }

    @Override
    public BTStatus execute() {
        if (mob.getTarget() == null) {
            return BTStatus.FAILURE;
        }
        mob.lookAt(mob.getTarget(), 90, 85);
        mob.getLookControl().setLookAt(mob.getTarget());

        return BTStatus.RUNNING;
    }
}
