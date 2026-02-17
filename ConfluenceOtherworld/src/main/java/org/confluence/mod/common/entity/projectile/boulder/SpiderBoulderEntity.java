package org.confluence.mod.common.entity.projectile.boulder;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.init.ModEntities;

public class SpiderBoulderEntity extends BoulderEntity {
    public SpiderBoulderEntity(EntityType<? extends BoulderEntity> entityType, Level level) {
        super(entityType, level);
    }

    public SpiderBoulderEntity(Level level, Vec3 pos, BlockState blockState) {
        super(ModEntities.SPIDER_BOULDER.get(), level, pos, blockState);
    }

    @Override
    public void tick() {
        super.tick();
        if (tickCount % 10 != 0 || !(level() instanceof ServerLevel serverLevel)) {
            return;
        }
        BlockPos pos = blockPosition();
        if (serverLevel.getBlockState(pos).canBeReplaced()) {
            serverLevel.setBlockAndUpdate(pos, Blocks.COBWEB.defaultBlockState());
        }
    }


    @Override
    protected void removeEffect(ServerLevel serverLevel) {
        super.removeEffect(serverLevel);
        // TODO 补充WALL_CREEPER
//        serverLevel.addFreshEntity(ModEntities.WALL_CREEPER);
        Spider entity = EntityType.SPIDER.create(serverLevel);
        if (entity != null) {
            entity.setPos(position());
            serverLevel.addFreshEntity(entity);
        }
    }

    @Override
    public void makeStuckInBlock(BlockState state, Vec3 motionMultiplier) {
        if (!state.is(Blocks.COBWEB)) {
            super.makeStuckInBlock(state, motionMultiplier);
        }
    }
}
