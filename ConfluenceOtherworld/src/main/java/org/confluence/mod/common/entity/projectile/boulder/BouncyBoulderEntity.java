package org.confluence.mod.common.entity.projectile.boulder;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.init.ModEntities;

public class BouncyBoulderEntity extends BoulderEntity {
    public BouncyBoulderEntity(EntityType<? extends BoulderEntity> entityType, Level level) {
        super(entityType, level);
        speed = 0.9;
    }

    public BouncyBoulderEntity(Level level, Vec3 pos, BlockState blockState) {
        super(ModEntities.BOUNCY_BOULDER.get(), level, pos, blockState);
        speed = 0.9;
    }

    @Override
    protected void horizontalHitBlock(BlockHitResult blockHitResult, Direction direction) {
        if (getHorizontalVectorLength(getDeltaMovement()) > 0.1) {
            rebound(direction);
        }
    }

    @Override
    protected void verticalHitRebound(BlockHitResult blockHitResult, Direction direction) {
        if (fallDistance > 1) {
            rebound(direction);
            fallDistance = 0;
        }
    }

    protected void rebound(Direction direction) {
        Vec3 directionVec3 = Vec3.atLowerCornerOf(direction.getNormal());
        Vec3 deltaMovementVec3 = getDeltaMovement();
        double dot = deltaMovementVec3.dot(directionVec3);
        double e = 1; // 弹力系数
        double x = -(1 + e) * dot * directionVec3.x;
        double y = -(1 + e) * dot * directionVec3.y;
        double z = -(1 + e) * dot * directionVec3.z;
        setDeltaMovement(deltaMovementVec3.add(x, y, z));
    }
}
