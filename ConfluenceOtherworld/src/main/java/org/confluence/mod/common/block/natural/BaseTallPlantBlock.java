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

    public BaseTallPlantBlock(Block... survive){
        super(Properties.ofFullCopy(Blocks.TALL_GRASS).replaceable());
        this.survive = survive;
    }

    public BaseTallPlantBlock(Properties prop, Block... survive){
        super(prop);
        this.survive = survive;
    }
    @Override
    public boolean mayPlaceOn(BlockState groundState, BlockGetter worldIn, BlockPos pos){
        return Arrays.stream(survive).anyMatch(state -> state == groundState.getBlock());
    }

    @Override
    public boolean canSurvive(BlockState blockstate, LevelReader worldIn, BlockPos pos){
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
    public BlockState updateShape(BlockState originState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos){
        BlockState after = super.updateShape(originState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
        if(pFacing == Direction.UP && !pFacingState.isAir()) {
            return Blocks.AIR.defaultBlockState();
        }
        if(pFacing != Direction.DOWN) return after;
        ISpreadable.Type type = pFacingState.getBlock() instanceof ISpreadable sp ? sp.getSpreadType() : ISpreadable.Type.PURE;
        BlockState transformResult = type.getNotNull(originState);
        return transformResult.canSurvive(pLevel, pCurrentPos) ? transformResult : Blocks.AIR.defaultBlockState();
    }

    @Override
    public BlockState playerWillDestroy(Level pLevel, BlockPos pPos, BlockState pState, Player pPlayer) {
        if (!pLevel.isClientSide()) {
            BlockPos abovePos = pPos.above();
            BlockState aboveState = pLevel.getBlockState(abovePos);
            if (aboveState.getBlock() == this) {
                pLevel.setBlock(abovePos, Blocks.AIR.defaultBlockState(), 35);
                pLevel.levelEvent(pPlayer, 2001, abovePos, Block.getId(aboveState));
            }
        }
        super.playerWillDestroy(pLevel, pPos, pState, pPlayer);
        return pState;
    }
}