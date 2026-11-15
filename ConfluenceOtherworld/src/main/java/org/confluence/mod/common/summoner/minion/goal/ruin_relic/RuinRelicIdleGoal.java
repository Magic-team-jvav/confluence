package org.confluence.mod.common.summoner.minion.goal.ruin_relic;

import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntityGoal;
import org.confluence.mod.common.summoner.attachmentEntity.PathNode;
import org.confluence.mod.common.summoner.minion.RuinRelicMinion;

public class RuinRelicIdleGoal extends AttachmentEntityGoal<RuinRelicMinion> {

    public RuinRelicIdleGoal(RuinRelicMinion minion) {
        super(minion);
    }

    @Override
    public boolean canUse() {
        return minion.getTarget() == null;
    }

    @Override
    public void tick() {
        PathNode currentPathNode = minion.getCurrentPathNode();
        PathNode targetPathNode = minion.getIdleRingNode(1.0F);
        minion.setCurrentPathNode(new PathNode(currentPathNode.pos().lerp(targetPathNode.pos(), 0.3F), currentPathNode.yaw(), currentPathNode.pitch(), currentPathNode.roll()));
        minion.setDesiredRotation(targetPathNode.yaw(), targetPathNode.pitch(), targetPathNode.roll());
    }
}
