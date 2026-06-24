package org.confluence.mod.common.entity.ai.goal.summon;

import net.minecraft.world.entity.Mob;
import org.confluence.mod.common.api.entity.ISummonMob;

public class SummonFlyFlowOwnerGoal<T extends Mob & ISummonMob> extends SummonFollowOwnerGoal<T>{

    public SummonFlyFlowOwnerGoal(T tamable, double speedModifier, float stopDistance) {
        super(tamable, speedModifier, stopDistance);
    }

    public int getInterval() {
        return 0;
    }

    public boolean requiresUpdateEveryTick() {
        return true;
    }

    public void createPath(){
        this.tamable.getMoveControl().setWantedPosition(owner.position().x, owner.position().y + 2, owner.position().z, 10);
        super.createPath();
    }
}
