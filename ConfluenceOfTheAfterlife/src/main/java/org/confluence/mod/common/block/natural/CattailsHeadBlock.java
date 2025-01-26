package org.confluence.mod.common.block.natural;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.confluence.mod.common.init.block.NatureBlocks;

import javax.annotation.Nullable;

import static net.neoforged.neoforge.common.CommonHooks.canCropGrow;

public class CattailsHeadBlock extends GrowingPlantHeadBlock implements LiquidBlockContainer {
    public static final MapCodec<CattailsHeadBlock> CODEC = simpleCodec(CattailsHeadBlock::new);
    public static final BooleanProperty UP = BlockStateProperties.UP;
    public static final IntegerProperty AGE = BlockStateProperties.AGE_25;
    protected static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 9.0, 16.0);
    private static final int MAX_AGE = 3;

    public CattailsHeadBlock(Properties properties) {
        super(properties, Direction.UP, SHAPE, false, 0.20);
        registerDefaultState(stateDefinition.any().setValue(UP, true));
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        BlockPos blockpos = pos.relative(this.growthDirection);
        if (state.getValue(AGE) < MAX_AGE) {
            if (level.getBlockState(blockpos).is(Blocks.WATER) || canCropGrow(level, blockpos, state, random.nextDouble() < 0.20)) {
                level.setBlockAndUpdate(blockpos, this.getGrowIntoState(state, level.random));
            }
        } else {
            if (!level.getBlockState(pos.above()).isAir()) {
                BlockState newState = state.setValue(AGE, 0);
                level.setBlockAndUpdate(pos, newState);
            }
            else if (state.getValue(AGE) < MAX_AGE) {
                if (level.getBlockState(blockpos).is(Blocks.WATER) ||
                    this.canGrowInto(level.getBlockState(blockpos))) {
                    level.setBlockAndUpdate(blockpos, this.getGrowIntoState(state, level.random));
                }
            }
        }
    }

    @Override
    public BlockState getStateForPlacement(LevelAccessor level) {
        return this.defaultBlockState().setValue(AGE, level.getRandom().nextInt(MAX_AGE));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
        return fluidstate.is(FluidTags.WATER) && fluidstate.getAmount() == 8 ? super.getStateForPlacement(context) : null;
    }

    @Override
    protected Block getBodyBlock() {
        BlockState state = this.defaultBlockState();
        if (state.is(NatureBlocks.CATTAILS_HEAD.get())) {
            return NatureBlocks.CATTAILS_BODY.get();
        } else if (state.is(NatureBlocks.JUNGLE_CATTAILS_HEAD.get())) {
            return NatureBlocks.JUNGLE_CATTAILS_BODY.get();
        } else if (state.is(NatureBlocks.GLOWING_MUSHROOM_CATTAILS_HEAD.get())) {
            return NatureBlocks.GLOWING_MUSHROOM_CATTAILS_BODY.get();
        } else if (state.is(NatureBlocks.HALLOW_CATTAILS_HEAD.get())) {
            return NatureBlocks.HALLOW_CATTAILS_BODY.get();
        } else if (state.is(NatureBlocks.EBONY_CATTAILS_HEAD.get())) {
            return NatureBlocks.EBONY_CATTAILS_BODY.get();
        } else if (state.is(NatureBlocks.TR_CRIMSON_CATTAILS_HEAD.get())) {
            return NatureBlocks.TR_CRIMSON_CATTAILS_BODY.get();
        } else {
            return NatureBlocks.CATTAILS_BODY.get();
        }
    }

    @Override
    protected boolean canAttachTo(BlockState state) {
        return !state.is(Blocks.MAGMA_BLOCK);
    }

    @Override
    protected int getBlocksToGrowWhenBonemealed(RandomSource random) {
        return 1;
    }

    @Override
    protected boolean canGrowInto(BlockState state) {
        return state.is(Blocks.WATER) || state.is(Blocks.AIR);
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        int currentAge = state.getValue(AGE);
        if (currentAge == 3) {
            return;
        }
        BlockPos blockpos = pos.relative(this.growthDirection);
        int i = Math.min(currentAge + 1, MAX_AGE);
        int j = this.getBlocksToGrowWhenBonemealed(random);
        for (int k = 0; k < j && this.canGrowInto(level.getBlockState(blockpos)); k++) {
            level.setBlockAndUpdate(blockpos, state.setValue(AGE, i));
            blockpos = blockpos.relative(this.growthDirection);
            i = Math.min(i + 1, MAX_AGE);
        }
    }


    @Override
    public boolean canPlaceLiquid(@Nullable Player player, BlockGetter level, BlockPos pos, BlockState state, Fluid fluid) {
        return false;
    }

    @Override
    public boolean placeLiquid(LevelAccessor level, BlockPos pos, BlockState state, FluidState fluidState) {
        return false;
    }

    @Override
    protected MapCodec<CattailsHeadBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(UP, AGE);
    }
}
