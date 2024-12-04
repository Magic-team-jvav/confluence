package org.confluence.mod.common.block.natural;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.VinesFeature;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class BaseDroopingPlantsBlock extends GrowingPlantHeadBlock {
    public static final MapCodec<BaseDroopingPlantsBlock> CODEC = RecordCodecBuilder.mapCodec(
        builder -> builder.group(propertiesCodec(),
                BuiltInRegistries.BLOCK.byNameCodec().listOf().fieldOf("block").forGetter(baseDroopingPlantsBlock ->
                    Arrays.asList(baseDroopingPlantsBlock.block)))
            .apply(builder, (prop, block) -> new BaseDroopingPlantsBlock(block.toArray(new Block[0])))
    );;
    protected static final VoxelShape SHAPE = Block.box(4.0, 9.0, 4.0, 12.0, 16.0, 12.0);
    private final Block[] block;

    public BaseDroopingPlantsBlock(Block... attachedBlock) {
        super(BlockBehaviour.Properties.of().noCollission().instabreak().sound(SoundType.WEEPING_VINES).pushReaction(PushReaction.DESTROY), Direction.DOWN, SHAPE, false, 0.1);
        this.block = attachedBlock;
    }

    @NotNull
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
    public boolean canSurvive(@NotNull BlockState blockstate, LevelReader level, BlockPos pos){
        BlockPos blockpos = pos.above();
        BlockState state = level.getBlockState(blockpos);
        if (state.is(this) || Arrays.asList(block).contains(state.getBlock())) return true;
        return false;
    }
}
