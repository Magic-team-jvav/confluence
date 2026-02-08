package org.confluence.mod.common.block.natural.spreadable.extended;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import org.confluence.mod.common.block.natural.spreadable.ISpreadable;
import org.confluence.mod.common.block.natural.spreadable.SpreadingGrassBlock;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class JungleGrassBlock extends SpreadingGrassBlock implements BonemealableBlock {
    public JungleGrassBlock(ISpreadable.Type type, Properties properties) {
        super(type, properties);
    }

    @Override
    public void randomTick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, RandomSource random) {
        if (!serverLevel.isAreaLoaded(blockPos, 3)) return;
        if (isFullBlock(serverLevel, blockPos.above())) {
            serverLevel.setBlockAndUpdate(blockPos, Blocks.MUD.defaultBlockState());
        } else {
            super.randomTick(blockState, serverLevel, blockPos, random);
        }
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel serverLevel, RandomSource random, BlockPos pos, BlockState state) {
        BlockPos startPos = pos.above();
        BlockState shortGrassState = Blocks.SHORT_GRASS.defaultBlockState();
        Optional<Holder.Reference<PlacedFeature>> grassFeatureOpt = serverLevel.registryAccess()
                .registryOrThrow(Registries.PLACED_FEATURE)
                .getHolder(VegetationPlacements.GRASS_BONEMEAL);
        for (int i = 0; i < 128; i++) {
            BlockPos targetPos = startPos;
            boolean validPosition = true;
            for (int j = 0; j < i / 16; j++) {
                int offsetX = random.nextInt(3) - 1;
                int offsetY = (random.nextInt(3) - 1) * random.nextInt(3) / 2;
                int offsetZ = random.nextInt(3) - 1;
                targetPos = targetPos.offset(offsetX, offsetY, offsetZ);
                BlockState belowState = serverLevel.getBlockState(targetPos.below());
                BlockState currentState = serverLevel.getBlockState(targetPos);
                if (!belowState.is(this) || currentState.isCollisionShapeFullBlock(serverLevel, targetPos)) {
                    validPosition = false;
                    break;
                }
            }
            if (!validPosition) {
                continue;
            }
            BlockState targetState = serverLevel.getBlockState(targetPos);
            if (targetState.is(shortGrassState.getBlock()) && random.nextInt(10) == 0) {
                ((BonemealableBlock) shortGrassState.getBlock()).performBonemeal(serverLevel, random, targetPos, targetState);
            }
            if (targetState.isAir()) {
                Holder<PlacedFeature> featureHolder;
                if (random.nextInt(8) == 0) {
                    List<ConfiguredFeature<?, ?>> flowerFeatures = serverLevel.getBiome(targetPos).value().getGenerationSettings().getFlowerFeatures();
                    if (flowerFeatures.isEmpty()) {
                        continue;
                    }
                    featureHolder = ((RandomPatchConfiguration) flowerFeatures.getFirst().config()).feature();
                } else {
                    if (grassFeatureOpt.isEmpty()) {
                        continue;
                    }
                    featureHolder = grassFeatureOpt.get();
                }
                featureHolder.value().place(serverLevel, serverLevel.getChunkSource().getGenerator(), random, targetPos);
            }
        }
    }

    @Override
    public BonemealableBlock.Type getType() {
        return BonemealableBlock.Type.NEIGHBOR_SPREADER;
    }

    @Override
    public @Nullable BlockState getToolModifiedState(BlockState state, UseOnContext context, ItemAbility itemAbility, boolean simulate) {
        if (itemAbility == ItemAbilities.SHOVEL_FLATTEN) {
            return NatureBlocks.JUNGLE_PATH.get().defaultBlockState();
        }
        return null;
    }
}
