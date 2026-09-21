package org.confluence.mod.common.summoner.minion.goal.sanguine_bat;

import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntityGoal;
import org.confluence.mod.common.summoner.minion.SanguineBatMinion;

public class SanguineBatIdleGoal extends AttachmentEntityGoal<SanguineBatMinion> {

    private final float speed = 0.4F + (float) Math.random() * 0.2F;
    private int prepTick;

    public SanguineBatIdleGoal(SanguineBatMinion minion) {
        super(minion);
    }

    @Override
    public boolean canUse() {
        return minion.getTarget() == null;
    }

    @Override
    public void start() {
        prepTick = 5 + minion.getRandom().nextIntBetweenInclusive(0, 5);
    }

    @Override
    public void tick() {
        minion.setCurrentPathNode(minion.getCurrentPathNode().lerp(minion.getInterpolatedIdleState(1.0F), speed));
        if (--prepTick < 0) {
            minion.prep = true;
        }
    }
}
