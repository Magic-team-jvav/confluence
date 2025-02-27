package org.confluence.mod.common.entity.projectile.boulder;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
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
        this.level().explode(this, entity.getX(), entity.getY(), entity.getZ(), 2.85F, true, Level.ExplosionInteraction.TNT);
        onRemove();
    }

    /*    @Override
    protected void onHitBlock(BlockHitResult blockHitResult) {
        super.onHitBlock(blockHitResult);
        BlockPos pos = blockHitResult.getBlockPos();
        this.level().explode(this, pos.getX(), pos.getY(), pos.getZ(), 2.85F, true, Level.ExplosionInteraction.TNT);
        remove();
    }*/
}
