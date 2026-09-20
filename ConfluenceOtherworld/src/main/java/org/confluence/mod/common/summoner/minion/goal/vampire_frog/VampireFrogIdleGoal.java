package org.confluence.mod.common.summoner.minion.goal.vampire_frog;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntityGoal;
import org.confluence.mod.common.summoner.minion.VampireFrogMinion;

public class VampireFrogIdleGoal extends AttachmentEntityGoal<VampireFrogMinion> {

    public VampireFrogIdleGoal(VampireFrogMinion minion) {
        super(minion);
    }

    @Override
    public boolean canUse() {
        return minion.getTarget() == null;
    }

    @Override
    public void tick() {
        Player owner = minion.getOwner();
        Vec3 forward = Vec3.directionFromRotation(0.0F, owner.yBodyRot);
        Vec3 right = forward.cross(new Vec3(0.0, 1.0, 0.0));
        right = right.lengthSqr() < 1.0E-6 ? new Vec3(1.0, 0.0, 0.0) : right.normalize();
        double lateral = (minion.getOrder() - (minion.getSameSize() - 1) * 0.5) * 1.1;
        Vec3 idlePos = Vec3.atBottomCenterOf(BlockPos.containing(owner.position().subtract(forward.scale(2.5)).add(right.scale(lateral))));
        Vec3 subtract = idlePos.subtract(minion.getPos());
        if (!owner.onGround()) {
            minion.lookAtPos(owner.getEyePosition());
            minion.flyTo(idlePos, 0.06f);
        } else {
            if (subtract.horizontalDistance() > 1) {
                minion.lookAtPos(idlePos);
                minion.moveTo(idlePos, 0.1f);
            } else {
                minion.lookAtPos(owner.getEyePosition());
            }
        }
        if (subtract.length() > 64) {
            minion.setVelocity(Vec3.ZERO);
            minion.init(minion.getCurrentPathNode().modifyPos(owner.getBoundingBox().getCenter().offsetRandom(minion.getRandom(), 3)));
        }
    }
}
