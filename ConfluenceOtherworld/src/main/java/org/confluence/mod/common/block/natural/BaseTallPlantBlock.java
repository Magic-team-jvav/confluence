package org.confluence.mod.common.block.natural;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TallGrassBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.mod.common.block.natural.spreadable.ISpreadable;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class BaseTallPlantBlock extends TallGrassBlock {
    public static final MapCodec<BaseTallPlantBlock> CODEC = RecordCodecBuilder.mapCodec(
            builder -> builder.group(propertiesCodec(),
                            BuiltInRegistries.BLOCK.byNameCodec().listOf().fieldOf("ground").forGetter(basePlantBlock ->
                                    Arrays.asList(basePlantBlock.survive)))
                    .apply(builder, (prop, ground) -> new BaseTallPlantBlock(prop, ground.toArray(new Block[0])))
    );

    private final Block[] survive;

    public BaseTallPlantBlock(Block... survive) {
        super(Properties.ofFullCopy(Blocks.TALL_GRASS).replaceable());
        this.survive = survive;
    }

    public BaseTallPlantBlock(Properties prop, Block... survive) {
        super(prop);
        this.survive = survive;
    }

    @Override
    public boolean mayPlaceOn(BlockState groundState, BlockGetter worldIn, BlockPos pos) {
        return Arrays.stream(survive).anyMatch(state -> state == groundState.getBlock());
    }

    @Override
    public boolean canSurvive(BlockState blockstate, LevelReader worldIn, BlockPos pos) {
        BlockPos blockpos = pos.below();
        BlockState groundState = worldIn.getBlockState(blockpos);
        return mayPlaceOn(groundState, worldIn, blockpos) &&
                worldIn.isEmptyBlock(pos.above());
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        LevelReader level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        if (!canSurvive(this.defaultBlockState(), level, pos)) {
            return Blocks.AIR.defaultBlockState();
        }
        return this.defaultBlockState();
    }

    @Override
    @NotNull
    public BlockState updateShape(BlockState originState, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        BlockState after = super.updateShape(originState, facing, facingState, level, currentPos, facingPos);
        if (facing == Direction.UP && !facingState.isAir()) {
            return Blocks.AIR.defaultBlockState();
        }
        if (facing != Direction.DOWN) return after;
        ISpreadable.Type type = facingState.getBlock() instanceof ISpreadable spreadable ? spreadable.getSpreadType() : ISpreadable.Type.PURE;
        BlockState transformResult = type.getNotNull(originState);
        return transformResult.canSurvive(level, currentPos) ? transformResult : Blocks.AIR.defaultBlockState();
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide()) {
            BlockPos abovePos = pos.above();
            BlockState aboveState = level.getBlockState(abovePos);
            if (aboveState.getBlock() == this) {
                level.setBlock(abovePos, Blocks.AIR.defaultBlockState(), 35);
                level.levelEvent(player, 2001, abovePos, Block.getId(aboveState));
            }
        }
        super.playerWillDestroy(level, pos, state, player);
        return state;
    }
}