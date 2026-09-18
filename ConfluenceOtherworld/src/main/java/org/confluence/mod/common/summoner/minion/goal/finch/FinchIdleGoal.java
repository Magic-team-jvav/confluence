package org.confluence.mod.common.summoner.minion.goal.finch;

import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntityGoal;
import org.confluence.mod.common.summoner.minion.FinchMinion;

public class FinchIdleGoal extends AttachmentEntityGoal<FinchMinion> {

    public FinchIdleGoal(FinchMinion minion) {
        super(minion);
    }

    @Override
    public boolean canUse() {
        return true;
    }

    @Override
    public void tick() {
        Vec3 targetPos = minion.getInterpolatedIdleState(1).pos();
        Vec3 minionPos = minion.getPos();
        Vec3 normalize = targetPos.subtract(minionPos).normalize();
        minion.lookAtDirection(normalize);
        minion.addVelocity(normalize.scale(0.01 * Math.min(1 + targetPos.distanceTo(minionPos) * 0.2, 16)));
    }
}
