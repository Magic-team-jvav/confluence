package org.confluence.mod.util.entity.ai.goal.behavior.condition;

import net.minecraft.world.entity.PathfinderMob;

/**
 * 是否寻路完成
 */
public class NavigationCondition  extends AbstractConditionLeaf {
    PathfinderMob mob;

    public NavigationCondition(PathfinderMob mob) {
        this.mob = mob;
    }

    @Override
    public boolean check() {
        return mob.getNavigation().isDone();
    }
}
