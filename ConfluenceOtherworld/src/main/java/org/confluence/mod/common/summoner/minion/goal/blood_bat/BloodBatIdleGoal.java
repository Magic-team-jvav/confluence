package org.confluence.mod.common.summoner.minion.goal.blood_bat;

import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntityGoal;
import org.confluence.mod.common.summoner.minion.BloodBatMinion;

public class BloodBatIdleGoal extends AttachmentEntityGoal<BloodBatMinion> {

    private final float speed = 0.4F + (float) Math.random() * 0.2F;
    private int prepTick;

    public BloodBatIdleGoal(BloodBatMinion minion) {
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
