package org.confluence.mod.common.summoner.minion.goal.terraprisma;

import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntityGoal;
import org.confluence.mod.common.summoner.minion.TerraprismaMinion;

import java.util.Collections;

public class TerraprismIdleGoal extends AttachmentEntityGoal<TerraprismaMinion> {

    public TerraprismIdleGoal(TerraprismaMinion minion) {
        super(minion);
    }

    @Override
    public boolean canUse() {
        return true;
    }

    @Override
    public void start() {
        minion.attacking = false;
    }

    @Override
    public void tick() {
        minion.setPath(Collections.singletonList(minion.getCurrentPathNode().lerp(minion.getInterpolatedIdleState(1), 0.35F)));
    }
}
