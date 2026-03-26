package org.confluence.mod.common.block.natural;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.common.CommonHooks;
import org.confluence.mod.common.init.block.NatureBlocks;

import java.util.Optional;

public class BalloonStemBlock extends StemBlock {
    private final ResourceKey<Block> fruit;
    private final ResourceKey<Block> attachedStem;

    public BalloonStemBlock(ResourceKey<Block> fruit, ResourceKey<Block> attachedStem, ResourceKey<Item> seed) {
        super(fruit, attachedStem, seed,
                BlockBehaviour.Properties.of()
                        .mapColor(MapColor.PLANT)
                        .noCollission()
                        .randomTicks()
                        .instabreak()
                        .sound(SoundType.HARD_CROP)
                        .pushReaction(PushReaction.DESTROY));
        this.fruit = fruit;
        this.attachedStem = attachedStem;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos belowPos = pos.below();
        return this.mayPlaceOn(level.getBlockState(belowPos), level, belowPos);
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(NatureBlocks.CLOUD_BLOCK.get()) || state.is(NatureBlocks.RAIN_CLOUD_BLOCK.get());
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!level.isAreaLoaded(pos, 1)) return;

        if (level.getRawBrightness(pos, 0) >= 9) {
            float f = CropBlock.getGrowthSpeed(state, level, pos);
            if (CommonHooks.canCropGrow(level, pos, state, random.nextInt((int) (25.0F / f) + 1) == 0)) {
                int i = state.getValue(AGE);
                if (i < 7) {
                    level.setBlock(pos, state.setValue(AGE, i + 1), 2);
                } else {
                    BlockPos blockpos = pos.above();
                    if (level.isEmptyBlock(blockpos)) {
                        Registry<Block> registry = level.registryAccess().registryOrThrow(Registries.BLOCK);
                        Optional<Block> optional = registry.getOptional(this.fruit);
                        Optional<Block> optional1 = registry.getOptional(this.attachedStem);
                        if (optional.isPresent() && optional1.isPresent()) {
                            level.setBlockAndUpdate(blockpos, optional.get().defaultBlockState());
                            level.setBlockAndUpdate(pos, optional1.get().defaultBlockState().setValue(BalloonAttachedStemBlock.FACING, Direction.UP));
                        }
                    }
                }
                CommonHooks.fireCropGrowPost(level, pos, state);
            }
        }
    }
}
