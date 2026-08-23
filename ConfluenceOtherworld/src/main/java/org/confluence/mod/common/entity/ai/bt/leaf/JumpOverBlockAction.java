package org.confluence.mod.common.entity.ai.bt.leaf;

import PortLib.extensions.net.minecraft.world.entity.ai.attributes.Attributes.PortAttributesExtension;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.pathfinder.Path;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTStatus;

/// 路径下一节点较高时原地起跳，并在两 tick 后补入朝向冲量。
public final class JumpOverBlockAction extends BTNode {
    private final PathfinderMob mob;
    private final double speedMultiplier;
    private int impulseDelay;

    public JumpOverBlockAction(PathfinderMob mob, double speedMultiplier) {
        this.mob = mob;
        this.speedMultiplier = speedMultiplier;
    }

    @Override
    public BTStatus execute() {
        if (--impulseDelay == 0)
            mob.addDeltaMovement(mob.getForward().normalize().scale(mob.getSpeed() * speedMultiplier));
        if (!mob.onGround()) return BTStatus.FAILURE;
        Path path = mob.getNavigation().getPath();
        if (path == null || path.getNodeCount() <= path.getNextNodeIndex() + 1)
            return BTStatus.FAILURE;
        double jumpHeight = mob.getAttributeBaseValue(PortAttributesExtension.jumpStrength().value()) * 4.0;
        int nextY = path.getNextNode().y;
        if (nextY <= mob.getY() || nextY >= mob.getY() + jumpHeight) return BTStatus.FAILURE;
        mob.setDeltaMovement(0.0, 0.0, 0.0);
        mob.getJumpControl().jump();
        impulseDelay = 2;
        return BTStatus.SUCCESS;
    }
}
