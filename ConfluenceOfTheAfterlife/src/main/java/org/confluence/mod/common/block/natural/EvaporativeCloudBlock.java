package org.confluence.mod.common.block.natural;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.util.ModUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EvaporativeCloudBlock extends BaseEntityBlock {
    public static final MapCodec<EvaporativeCloudBlock> CODEC = simpleCodec(EvaporativeCloudBlock::new);

    public EvaporativeCloudBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected @NotNull MapCodec<EvaporativeCloudBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pPos, @NotNull BlockState pState) {
        return new Entity(pPos, pState);
    }

    public void fallOn(@NotNull Level level, @NotNull BlockState state, @NotNull BlockPos pos, net.minecraft.world.entity.Entity entity, float fallDistance) {
        if (entity.isSuppressingBounce()) {
            super.fallOn(level, state, pos, entity, fallDistance);
        }
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level pLevel, @NotNull BlockState pState, @NotNull BlockEntityType<T> pBlockEntityType) {
        if (pLevel.isClientSide) return null;
        return ModUtils.getTicker(pBlockEntityType, NatureBlocks.EVAPORATIVE_CLOUD_BLOCK_ENTITY.get(), (level, blockPos, blockState, e) -> {
            level.updateNeighborsAt(blockPos, blockState.getBlock());
            level.removeBlock(blockPos, false);
        });
    }

    public static class Entity extends BlockEntity {
        public Entity(BlockPos pos, BlockState state) {
            super(NatureBlocks.EVAPORATIVE_CLOUD_BLOCK_ENTITY.get(), pos, state);
        }
    }
}
