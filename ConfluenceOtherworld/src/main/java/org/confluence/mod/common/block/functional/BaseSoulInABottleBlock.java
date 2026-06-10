package org.confluence.mod.common.block.functional;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.jetbrains.annotations.Nullable;

public class BaseSoulInABottleBlock extends LanternBlock implements EntityBlock {
    private static final VoxelShape NORMAL_SHAPE = Shapes.or(
            box(4, 0.03125, 4, 12, 10.03125, 12),
            box(5, 10, 5, 11, 12, 11)
    );
    private static final VoxelShape HANGING_SHAPE = Shapes.or(
            box(4, 1.03125, 4, 12, 11.03125, 12),
            box(5, 11, 5, 11, 13, 11)
    );

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return pState.getValue(HANGING) ? HANGING_SHAPE : NORMAL_SHAPE;
    }

    public static class BEntity extends BlockEntity {
        public BEntity(BlockPos pos, BlockState state) {
            super(FunctionalBlocks.SOUL_BOTTLE_ENTITY.get(), pos, state);
        }
    }

    public BaseSoulInABottleBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BEntity(pos, state);
    }
}
