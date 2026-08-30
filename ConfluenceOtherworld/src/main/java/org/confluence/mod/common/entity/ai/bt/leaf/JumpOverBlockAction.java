package org.confluence.mod.common.entity.ai.bt.leaf;

import PortLib.extensions.net.minecraft.world.entity.ai.attributes.Attributes.PortAttributesExtension;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTStatus;

/// 路径下一节点较高时沿路径方向起跳。
public final class JumpOverBlockAction extends BTNode {
    private final PathfinderMob mob;
    private final double speedMultiplier;

    public JumpOverBlockAction(PathfinderMob mob, double speedMultiplier) {
        this.mob = mob;
        this.speedMultiplier = speedMultiplier;
    }

    @Override
    public BTStatus execute() {
        if (!mob.onGround()) return BTStatus.FAILURE;
        Path path = mob.getNavigation().getPath();
        if (path == null || path.getNodeCount() <= path.getNextNodeIndex() + 1)
            return BTStatus.FAILURE;
        double jumpHeight = mob.getAttributeBaseValue(PortAttributesExtension.jumpStrength().value()) * 4.0;
        int nextY = path.getNextNode().y;
        if (nextY <= mob.getY() || nextY >= mob.getY() + jumpHeight) return BTStatus.FAILURE;
        Vec3 direction = Vec3.atBottomCenterOf(path.getNextNode().asBlockPos())
                .subtract(mob.position()).multiply(1.0, 0.0, 1.0);
        if (direction.lengthSqr() < 1.0E-8) return BTStatus.FAILURE;
        double speed = mob.getAttributeValue(Attributes.MOVEMENT_SPEED) * speedMultiplier;
        Vec3 impulse = direction.normalize().scale(speed);
        mob.setDeltaMovement(impulse.x, mob.getDeltaMovement().y, impulse.z);
        mob.getJumpControl().jump();
        mob.hasImpulse = true;
        return BTStatus.SUCCESS;
    }
}
