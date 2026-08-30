package org.confluence.mod.common.block.natural;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

import java.util.List;

public class InverseTallPlantBlock extends BaseTallPlantBlock{

    @SuppressWarnings("unused")
    public InverseTallPlantBlock(Properties prop, List<Block> survive) {
        super(prop, survive);
    }

    public InverseTallPlantBlock(Block... survive) {
        super(survive);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos above = pos.above();
        BlockState groundState = level.getBlockState(above);
        return mayPlaceOn(groundState, level, above) && level.getBlockState(pos.below()).canBeReplaced();
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide) {
            BlockPos belowPos = pos.below();
            BlockState belowState = level.getBlockState(belowPos);
            if (belowState.getBlock() == this) {
                level.setBlock(belowPos, Blocks.AIR.defaultBlockState(), 35);
                level.levelEvent(player, 2001, belowPos, Block.getId(belowState));
            }
        }
        super.playerWillDestroy(level, pos, state, player);
        return state;
    }

    @Override
    public BlockState updateShape(BlockState originState, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        if (facing == originState.getValue(HALF).getDirectionToOther().getOpposite()) return (facingState.is(this) && (facingState.getValue(HALF) != originState.getValue(HALF))) ? originState : Blocks.AIR.defaultBlockState();
        if ((originState.getValue(HALF) == DoubleBlockHalf.LOWER) && !canSurvive(originState, level, currentPos)) return Blocks.AIR.defaultBlockState();
        return originState;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        BlockPos blockpos = pos.below();
        level.setBlock(blockpos, copyWaterloggedFrom(level, blockpos, this.defaultBlockState().setValue(HALF, DoubleBlockHalf.UPPER)), 3);
    }
}
