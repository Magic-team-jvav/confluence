package org.confluence.mod.common.summoner.minion.goal.vex_totem;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntityGoal;
import org.confluence.mod.common.summoner.attachmentEntity.PathNode;
import org.confluence.mod.common.summoner.minion.VexTotemMinion;

public class VexTotemAttackGoal extends AttachmentEntityGoal<VexTotemMinion> {

    public VexTotemAttackGoal(VexTotemMinion minion) {
        super(minion);
    }

    @Override
    public boolean canUse() {
        return minion.getTarget() != null;
    }

    @Override
    public boolean canContinueToUse() {
        return minion.chargeTime >= 0 || minion.getTarget() != null;
    }

    @Override
    public boolean isInterruptable() {
        return minion.chargeTime < 0;
    }

    @Override
    public void start() {
        minion.setVelocity(Vec3.ZERO);
    }

    @Override
    public void tick() {
        LivingEntity target = minion.getTarget();
        if (target != null) {
            Vec3 pos = minion.getPos();
            Vec3 center = LibEntityUtils.aimPoint(target, minion.getPos());
            boolean charging = minion.chargeTime >= 0;
            Vec3 destination = center;
            if (!charging) {
                Vec3 fromCenter = pos.subtract(center);
                destination = center.add(fromCenter.lengthSqr() < 1.0E-6 ? new Vec3(1.0, 0.0, 0.0) : fromCenter.normalize());
            }
            Vec3 toTarget = center.subtract(pos);
            Vec3 newPos = pos.lerp(destination, charging ? 0.05 : 0.2);
            float yaw = toTarget.lengthSqr() < 1.0E-6 ? minion.getYaw() : PathNode.yawFromDirection(toTarget);
            minion.setDesiredRotation(yaw, 0, 0);
            minion.setCurrentPathNode(new PathNode(newPos, yaw, 0, 0));
            if (!charging && newPos.distanceTo(destination) < 1) {
                minion.chargeTime = 0;
            }
        }
    }
}
