package org.confluence.mod.common.block.natural;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.NetherVines;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Arrays;

public class BaseDroopingPlantsBlock extends GrowingPlantHeadBlock {
    public static final MapCodec<BaseDroopingPlantsBlock> CODEC = RecordCodecBuilder.mapCodec(
        builder -> builder.group(
                Codec.INT.fieldOf("side").forGetter(baseDroopingPlantsBlock -> baseDroopingPlantsBlock.side),
                Codec.BOOL.fieldOf("isNaturalGrowth").forGetter(baseDroopingPlantsBlock -> baseDroopingPlantsBlock.isNaturalGrowth),
                BuiltInRegistries.BLOCK.byNameCodec().listOf().fieldOf("attachedBlock").forGetter(baseDroopingPlantsBlock -> Arrays.asList(baseDroopingPlantsBlock.block))).
            apply(builder, (shape, isNaturalGrowth, attachedBlock) -> new BaseDroopingPlantsBlock(shape, isNaturalGrowth, attachedBlock.toArray(new Block[0])))
    );
    protected static VoxelShape SHAPE;
    private final Block[] block;
    private final boolean isNaturalGrowth;
    private final int side;

    public BaseDroopingPlantsBlock(int side, boolean isNaturalGrowth, Block... attachedBlock) {
        super(Properties.of().noCollission().instabreak().sound(SoundType.WEEPING_VINES).pushReaction(PushReaction.DESTROY), Direction.DOWN, SHAPE, false, 0.1);
        this.block = attachedBlock;
        this.isNaturalGrowth = isNaturalGrowth;
        this.side = side;
    }

    @Override
    protected MapCodec<BaseDroopingPlantsBlock> codec() {
        return CODEC;
    }

    @Override
    protected Block getBodyBlock() {
        return this;
    }

    @Override
    protected int getBlocksToGrowWhenBonemealed(RandomSource random) {
        return NetherVines.getBlocksToGrowWhenBonemealed(random);
    }

    @Override
    protected boolean canGrowInto(BlockState state) {
        return NetherVines.isValidGrowthState(state);
    }


    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return isNaturalGrowth;
    }

    @Override
    public boolean canSurvive(BlockState blockstate, LevelReader level, BlockPos pos) {
        BlockPos blockpos = pos.above();
        BlockState state = level.getBlockState(blockpos);
        return state.is(this) || Arrays.asList(block).contains(state.getBlock());
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        double halfSide = (16 - side) / 2.0;
        double height = state.getValue(AGE) == 0 ? 0 : 16 - side;

        return SHAPE = Block.box(halfSide, height, halfSide, 16 - halfSide, 16, 16 - halfSide);
    }
}
