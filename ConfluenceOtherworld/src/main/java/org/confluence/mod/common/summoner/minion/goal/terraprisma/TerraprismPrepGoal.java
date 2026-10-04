package org.confluence.mod.common.summoner.minion.goal.terraprisma;

import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntityGoal;
import org.confluence.mod.common.summoner.attachmentEntity.PathNode;
import org.confluence.mod.common.summoner.minion.TerraprismaMinion;

import java.util.Collections;

public class TerraprismPrepGoal extends AttachmentEntityGoal<TerraprismaMinion> {

    private PathNode prep;

    public TerraprismPrepGoal(TerraprismaMinion minion) {
        super(minion);
    }

    @Override
    public boolean canUse() {
        return minion.getTarget() != null && !minion.attacking && minion.getRandom().nextDouble() < 0.1;
    }

    @Override
    public boolean canContinueToUse() {
        return minion.getTarget() != null && !minion.attacking;
    }

    @Override
    public void start() {
        prep = new PathNode(minion.getPos().add(0, 2, 0), minion.getYaw(), minion.getPitch(), minion.getRoll());
    }

    @Override
    public void tick() {
        Vec3 toTarget = minion.getTarget().getBoundingBox().getCenter().subtract(minion.getPos());
        PathNode prepNode = minion.getEulerNode(minion.getPos(), toTarget, toTarget.cross(new Vec3(0, 1, 0)).normalize());
        prep = new PathNode(prep.pos(), prepNode.yaw(), prepNode.pitch(), prepNode.roll());
        minion.setPath(Collections.singletonList(minion.getCurrentPathNode().lerp(prep, 0.25F)));
        if (minion.getPos().distanceToSqr(prep.pos()) < 0.05) {
            minion.attacking = true;
        }
    }
}
