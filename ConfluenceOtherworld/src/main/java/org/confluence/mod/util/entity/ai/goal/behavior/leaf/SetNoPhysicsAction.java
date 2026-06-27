package org.confluence.mod.util.entity.ai.goal.behavior.leaf;

import net.minecraft.world.entity.Mob;
import org.confluence.mod.util.entity.ai.goal.behavior.BTNode;

public class SetNoPhysicsAction extends BTNode {
    final Mob mob;
    final boolean isNoPhysics;
    public SetNoPhysicsAction(Mob mob, boolean isNoPhysics) {
        this.mob = mob;
        this.isNoPhysics = isNoPhysics;
    }

    @Override
    public BTStatus execute() {
        this.mob.noPhysics = this.isNoPhysics;
        return BTStatus.SUCCESS;
    }
}
