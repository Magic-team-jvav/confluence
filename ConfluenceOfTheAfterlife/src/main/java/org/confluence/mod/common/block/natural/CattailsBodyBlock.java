package org.confluence.mod.common.block.natural;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.GrowingPlantBodyBlock;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.jetbrains.annotations.Nullable;

public class CattailsBodyBlock extends GrowingPlantBodyBlock implements LiquidBlockContainer {
    public static final MapCodec<CattailsBodyBlock> CODEC = simpleCodec(CattailsBodyBlock::new);
    private static final VoxelShape SHAPE = Block.box(1.0, 0.0, 1.0, 15.0, 16.0, 15.0);

    public CattailsBodyBlock(BlockBehaviour.Properties properties) {
        super(properties, Direction.UP, SHAPE, false);
    }

    @Override
    protected MapCodec<CattailsBodyBlock> codec() {
        return CODEC;
    }

    @Override
    protected GrowingPlantHeadBlock getHeadBlock() {
        BlockState state = this.defaultBlockState();
        if (state.is(NatureBlocks.CATTAILS_BODY.get())) {
            return NatureBlocks.CATTAILS_HEAD.get();
        } else if (state.is(NatureBlocks.JUNGLE_CATTAILS_BODY.get())) {
            return NatureBlocks.JUNGLE_CATTAILS_HEAD.get();
        } else if (state.is(NatureBlocks.GLOWING_MUSHROOM_CATTAILS_BODY.get())) {
            return NatureBlocks.GLOWING_MUSHROOM_CATTAILS_HEAD.get();
        } else if (state.is(NatureBlocks.HALLOW_CATTAILS_BODY.get())) {
            return NatureBlocks.HALLOW_CATTAILS_HEAD.get();
        } else if (state.is(NatureBlocks.EBONY_CATTAILS_BODY.get())) {
            return NatureBlocks.EBONY_CATTAILS_HEAD.get();
        } else if (state.is(NatureBlocks.TR_CRIMSON_CATTAILS_BODY.get())) {
            return NatureBlocks.TR_CRIMSON_CATTAILS_HEAD.get();
        } else {
            return NatureBlocks.CATTAILS_HEAD.get();
        }
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        return Fluids.WATER.getSource(false);
    }

    @Override
    public boolean canPlaceLiquid(@Nullable Player player, BlockGetter level, BlockPos pos, BlockState state, Fluid fluid) {
        return false;
    }

    @Override
    public boolean placeLiquid(LevelAccessor level, BlockPos pos, BlockState state, FluidState fluidState) {
        return false;
    }
}
