package org.confluence.mod.common.block.common;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import org.confluence.mod.common.init.block.ModBlocks;

import javax.annotation.Nullable;

public class VoidBlock extends LiquidBlock implements EntityBlock {
    public VoidBlock(FlowingFluid fluid, Properties properties) {
        super(fluid, properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new VoidBlockEntity(pos, state);
    }

    public static class VoidBlockEntity extends BlockEntity {
        public VoidBlockEntity(BlockPos pos, BlockState state) {
            super(ModBlocks.VOID_ENTITY.get(), pos, state);
        }
    }
}
