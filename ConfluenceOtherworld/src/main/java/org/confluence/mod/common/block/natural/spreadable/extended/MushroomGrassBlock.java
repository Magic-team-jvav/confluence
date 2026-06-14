package org.confluence.mod.common.block.natural.spreadable.extended;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.mod.common.block.natural.spreadable.ISpreadable;
import org.confluence.mod.common.block.natural.spreadable.SpreadingGrassBlock;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.IntUnaryOperator;

public class MushroomGrassBlock extends SpreadingGrassBlock implements BonemealableBlock {
    public MushroomGrassBlock() {
        super(ISpreadable.Type.GLOWING, Properties.ofFullCopy(Blocks.MUD));
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!level.isAreaLoaded(pos, 3)) return;
        if (isFullBlock(level, pos.above())) {
            level.setBlockAndUpdate(pos, Blocks.MUD.defaultBlockState());
        } else {
            super.randomTick(state, level, pos, random);
        }
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
        return level.getBlockState(pos.above()).isAir();
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        final int attempts = 128;
        final int range = 4;
        BiConsumer<BlockState, IntUnaryOperator> tryPlaceBlock = (blockState, dyGenerator) -> {
            for (int i = 0; i < attempts; i++) {
                int dx = random.nextInt(range * 2 + 1) - range;
                int dz = random.nextInt(range * 2 + 1) - range;
                int dy = dyGenerator.applyAsInt(random.nextInt(2));
                BlockPos targetPos = pos.offset(dx, dy, dz);
                if (level.getBlockState(targetPos).isAir() && blockState.canSurvive(level, targetPos)) {
                    level.setBlockAndUpdate(targetPos, blockState);
                }
            }
        };
        BlockState glowingMushroom = NatureBlocks.GLOWING_MUSHROOM.get().defaultBlockState();
        BlockState glowingMushroomVine = NatureBlocks.GLOWING_MUSHROOM_VINE.get().defaultBlockState();
        tryPlaceBlock.accept(glowingMushroom, dy -> dy);
        tryPlaceBlock.accept(glowingMushroomVine, dy -> -dy);
    }

    @Override
    public @Nullable BlockState getToolModifiedState(BlockState state, UseOnContext context, ItemAbility itemAbility, boolean simulate) {
        if (itemAbility == net.minecraftforge.common.ItemAbilities.SHOVEL_FLATTEN) {
            return NatureBlocks.MUSHROOM_PATH.get().defaultBlockState();
        }
        return null;
    }
}
