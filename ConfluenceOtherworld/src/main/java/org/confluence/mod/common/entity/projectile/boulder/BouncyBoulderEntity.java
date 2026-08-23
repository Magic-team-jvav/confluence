package org.confluence.mod.common.entity.projectile.boulder;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.LibMathUtils;
import org.confluence.mod.common.init.entity.ModEntities;

/// 不会因碰撞立即移除的高弹性巨石。
///
/// 弹性与摩擦系数由基类统一保存，确保所有巨石变体使用同一份版本化运行状态。
public class BouncyBoulderEntity extends BoulderEntity {

    public BouncyBoulderEntity(EntityType<? extends BoulderEntity> entityType, Level level) {
        super(entityType, level);
        speed = 0.9;
        bounceFactor = 0.9999999999;
    }

    public BouncyBoulderEntity(Level level, Vec3 pos, BlockState blockState) {
        super(ModEntities.BOUNCY_BOULDER.get(), level, pos, blockState);
        speed = 0.9;
        bounceFactor = 0.9999999999;
    }

    @Override
    protected void onHitBlock(BlockHitResult blockHitResult) {
        super.onHitBlock(blockHitResult);
        Vec3 motion = LibMathUtils.relativeScale(getDeltaMovement(), blockHitResult.getDirection().getAxis(), -bounceFactor);
        if (Math.abs(motion.y) < 0.01) motion = new Vec3(motion.x, 0.0, motion.z);
        setDeltaMovement(motion.scale(frictionFactor));
    }

    @Override
    protected void verticalHitRebound(BlockHitResult blockHitResult, Direction direction) {
    }

    @Override
    protected void horizontalHitBlock(BlockHitResult blockHitResult, Direction direction) {
    }
}
