package org.confluence.mod.common.summoner.minion.goal.ruin_relic;

import net.minecraft.world.entity.LivingEntity;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntityGoal;
import org.confluence.mod.common.summoner.attachmentEntity.PathNode;
import org.confluence.mod.common.summoner.minion.RuinRelicMinion;

public class RuinRelicAttackGoal extends AttachmentEntityGoal<RuinRelicMinion> {

    public RuinRelicAttackGoal(RuinRelicMinion minion) {
        super(minion);
    }

    @Override
    public boolean canUse() {
        return minion.getTarget() != null;
    }

    @Override
    public void tick() {
        LivingEntity target = minion.getTarget();
        PathNode currentPathNode = minion.getCurrentPathNode();
        PathNode targetPathNode = minion.getAttackRingNode(target, 1.0F);
        minion.setCurrentPathNode(new PathNode(currentPathNode.pos().lerp(targetPathNode.pos(), 0.2F), currentPathNode.yaw(), currentPathNode.pitch(), currentPathNode.roll()));
        minion.setDesiredRotation(targetPathNode.yaw(), targetPathNode.pitch(), targetPathNode.roll());
        if (minion.getCooldown() <= 0) {
            minion.shootForbiddenOrb(target);
            minion.setCooldown(minion.getAttackCooldown());
        }
    }
}
