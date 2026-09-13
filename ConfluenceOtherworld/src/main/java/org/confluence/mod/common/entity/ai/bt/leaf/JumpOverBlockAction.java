package org.confluence.mod.common.entity.ai.bt.leaf;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTStatus;

/// 路径下一节点较高时沿路径方向起跳。
public final class JumpOverBlockAction extends BTNode {
    private final PathfinderMob mob;
    private final double speedMultiplier;
    private Vec3 jumpImpulse;

    public JumpOverBlockAction(PathfinderMob mob, double speedMultiplier) {
        if (speedMultiplier <= 0.0) {
            throw new IllegalArgumentException("Jump speed multiplier must be positive");
        }
        this.mob = mob;
        this.speedMultiplier = speedMultiplier;
    }

    @Override
    public boolean canStart() {
        jumpImpulse = null;
        if (!mob.onGround()) return false;
        Path path = mob.getNavigation().getPath();
        if (path == null || path.isDone()) return false;
        AttributeInstance jumpStrength = mob.getAttribute(Attributes.JUMP_STRENGTH_1211);
        double jumpHeight = (jumpStrength == null ? 0.42D : jumpStrength.getBaseValue()) * 4.0D;
        int nextY = path.getNextNode().y;
        if (nextY <= mob.getY() || nextY >= mob.getY() + jumpHeight) return false;
        Vec3 direction = Vec3.atBottomCenterOf(path.getNextNode().asBlockPos()).subtract(mob.position()).multiply(1.0, 0.0, 1.0);
        if (direction.lengthSqr() < 1.0E-8) return false;
        double speed = mob.getAttributeValue(Attributes.MOVEMENT_SPEED) * speedMultiplier;
        jumpImpulse = direction.normalize().scale(speed);
        return true;
    }

    @Override
    public BTStatus execute() {
        if (jumpImpulse == null && !canStart()) return BTStatus.FAILURE;
        Vec3 impulse = jumpImpulse;
        jumpImpulse = null;
        mob.setDeltaMovement(impulse.x, mob.getDeltaMovement().y, impulse.z);
        mob.getJumpControl().jump();
        mob.hasImpulse = true;
        return BTStatus.SUCCESS;
    }
}
