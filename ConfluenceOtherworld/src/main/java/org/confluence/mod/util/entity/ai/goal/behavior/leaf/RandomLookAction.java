package org.confluence.mod.util.entity.ai.goal.behavior.leaf;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.utils.TEUtils;
import org.confluence.mod.util.entity.ai.goal.behavior.BTNode;

/**
 * 看向目标
 */
public class RandomLookAction extends BTNode {

    final Mob mob;
    Vec3 pos;
    public RandomLookAction(Mob mob) {
        this.mob = mob;

    }

    @Override
    public void start() {
        super.start();
        pos = mob.getEyePosition().add(TEUtils.sphere(1, mob.getRandom().nextFloat() * 6.28f, (float) Math.PI * 0.5f));
    }

    @Override
    public BTStatus execute() {
        mob.lookAt(EntityAnchorArgument.Anchor.EYES, pos);
        mob.getLookControl().setLookAt(pos);

        return BTStatus.RUNNING;
    }

}
