package org.confluence.mod.common.entity.projectile.boulder;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.init.ModEntities;

public class ExplodeBoulderEntity extends BoulderEntity {
    public ExplodeBoulderEntity(EntityType<ExplodeBoulderEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public ExplodeBoulderEntity(Level level, Vec3 pos, BlockState blockState) {
        super(ModEntities.EXPLODE_BOULDER.get(), level, pos, blockState);
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        super.onHitEntity(entityHitResult);
        Entity entity = entityHitResult.getEntity();
        Level level = this.level();
        if (!level.isClientSide) {
            explode(level, entity.getX(), entity.getY(), entity.getZ());
        }
        onRemove();
    }

    @Override
    protected void horizontalHitBlock(BlockHitResult blockHitResult, Direction direction) {
        Level level = this.level();
        if (!level.isClientSide) {
            Vec3 pos = blockHitResult.getLocation();
            explode(level, pos.x, pos.y, pos.z);
        }
        super.horizontalHitBlock(blockHitResult, direction);
    }

    protected void explode(Level level, double pos, double pos1, double pos2) {
        level.explode(this, pos, pos1, pos2, 2.85F, true, Level.ExplosionInteraction.TNT);
    }
}
