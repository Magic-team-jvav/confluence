package org.confluence.mod.common.summoner.minion.worm;

import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntityGoal;
import org.confluence.mod.common.summoner.attachmentEntity.PathNode;

import java.util.List;

class WormFollowGoal extends AttachmentEntityGoal<WormAttachmentEntity> {
    WormFollowGoal(WormAttachmentEntity minion) {
        super(minion);
    }

    @Override
    public boolean canUse() {
        return !minion.isHead();
    }

    @Override
    public void tick() {
        WormAttachmentEntity preceding = minion.getPrecedingSegment();
        if (preceding != null) {
            List<PathNode> history = preceding.getHistoryNodes();
            double remaining = minion.getSegmentDistance();
            Vec3 targetPos = preceding.getPos();
            for (PathNode node : history) {
                double distance = targetPos.distanceTo(node.pos());
                if (distance >= remaining) {
                    targetPos = targetPos.lerp(node.pos(), remaining / distance);
                    remaining = 0;
                    break;
                }
                remaining -= distance;
                targetPos = node.pos();
            }
            if (remaining > 0) {
                PathNode oldest = history.getLast();
                targetPos = targetPos.subtract(Vec3.directionFromRotation(oldest.pitch(), oldest.yaw()).scale(remaining));
            }
            Vec3 direction = preceding.getPos().subtract(targetPos);
            Vec3 normal = minion.getCurrentNormal().lerp(preceding.getCurrentNormal(), 0.5);
            PathNode orientation = minion.getEulerNode(targetPos, direction.lengthSqr() > 0.0001 ? direction : minion.getLookAngle(), normal);
            minion.setCurrentPathNode(orientation);
            minion.setDesiredRotation(orientation.yaw(), orientation.pitch(), orientation.roll());
        }
    }
}
