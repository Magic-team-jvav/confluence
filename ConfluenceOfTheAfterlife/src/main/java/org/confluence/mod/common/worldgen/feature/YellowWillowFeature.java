package org.confluence.mod.common.worldgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class YellowWillowFeature extends Feature<YellowWillowFeature.Config> {
    public YellowWillowFeature(Codec<Config> pCodec) {
        super(pCodec);
    }

    private static Set<BlockPos> setLeaves(BlockPos startPos, BlockPos endPos, BlockState leaves, boolean up, RandomSource random, WorldGenLevel level) {
        return leaves(startPos, endPos, leaves, up, random, level, Blocks.AIR.defaultBlockState(), false);
    }

    private static Set<BlockPos> setLeaves(BlockPos startPos, BlockPos endPos, BlockState leaves, boolean up, RandomSource random, WorldGenLevel level, BlockState droopingLeaves) {
        return leaves(startPos, endPos, leaves, up, random, level, droopingLeaves, true);
    }

    private static Set<BlockPos> leaves(BlockPos startPos, BlockPos endPos, BlockState leaves, boolean up, RandomSource random, WorldGenLevel level, BlockState droopingLeaves, boolean droop) {
        int xStart = startPos.getX();
        int yStart = startPos.getY();
        int zStart = startPos.getZ();
        int xEnd = endPos.getX();
        int yEnd = endPos.getY();
        int zEnd = endPos.getZ();
        boolean set;
        BlockPos posPlace;
        BlockPos posDroop;
        int yDroop;
        int length;
        Set<BlockPos> leavesPos = new HashSet<>();
        for (int x = xStart; x <= xEnd; x++) {
            for (int y = yStart; y <= yEnd; y++) {
                for (int z = zStart; z <= zEnd; z++) {
                    posPlace = new BlockPos(x, y, z);
                    set = (!((x == xStart || x == xEnd) && (z == zStart || z == zEnd)) || ((y == yStart || up) && random.nextInt(3) == 0)) && (level.getBlockState(posPlace).isAir());
                    if (set) {
                        level.setBlock(posPlace, leaves, 3);
                        leavesPos.add(posPlace);
                    }
                    if (droop) {
                        if (posPlace.getY() == yStart) {
                            yDroop = posPlace.getY() - 1;
                            length = (level.getBlockState(posPlace).isAir()) ? 0 : random.nextInt(4);
                            for (int i = 0; i < length; i++) {
                                posDroop = new BlockPos(x, yDroop - i, z);
                                if (level.getBlockState(posDroop).isAir()) {
                                    level.setBlock(posDroop, droopingLeaves, 3);
                                }
                            }
                        }
                    }
                }
            }
        }
        return leavesPos;
    }

    @Override
    public boolean place(FeaturePlaceContext<Config> pContext) {
        RandomSource random = pContext.random();
        Config config = pContext.config();
        WorldGenLevel level = pContext.level();
        BlockPos baseBlockPos = pContext.origin();
        BlockState trunkBlockState = config.trunk().getState(random, baseBlockPos);
        BlockState leavesBlockState = config.leaves().getState(random, baseBlockPos);
        BlockState droopingLeavesBlockState = config.drooping_leaves.getState(random, baseBlockPos);
        int height = config.height + random.nextInt(3);
        List<BlockPos> trunkPosList = new ArrayList<>();
        Set<BlockPos> trunkPos = new HashSet<>();
        Set<BlockPos> leavesPos = new HashSet<>();
        Set<BlockPos> rootPos = new HashSet<>();
        BoundingBox box = new BoundingBox(baseBlockPos.getX() - 4, baseBlockPos.getY(), baseBlockPos.getZ() - 4, baseBlockPos.getX() + 4, baseBlockPos.getY() + height + 8, baseBlockPos.getZ() + 4);
        rootPos.add(baseBlockPos);
        for (int i = 0; i < height + 3; i++) {
            trunkPosList.add(baseBlockPos.offset(0, i, 0));
        }

        boolean placed = true;

        for (int i = 0; i < trunkPosList.size(); i++) {
            if (!(level.getBlockState(trunkPosList.get(i)).isAir() || level.getBlockState(trunkPosList.get(i)).is(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("minecraft", "leaves"))))) {
                placed = false;
            }
        }

        if (placed) {
            for (int i = 0; i < trunkPosList.size(); i++) {
                level.setBlock(trunkPosList.get(i), trunkBlockState, 3);
                trunkPos.add(trunkPosList.get(i));
            }
            leavesPos.addAll(setLeaves(baseBlockPos.offset(-2, height, -2), baseBlockPos.offset(2, height + 1, 2), leavesBlockState, true, random, level, droopingLeavesBlockState));
            leavesPos.addAll(setLeaves(baseBlockPos.offset(-1, height + 2, -1), baseBlockPos.offset(1, height + 3, 1), leavesBlockState, false, random, level));
            TreeFeature.updateLeaves(level, box, rootPos, trunkPos, leavesPos);
            return true;
        }
        return false;
    }

    public record Config(BlockStateProvider trunk, BlockStateProvider leaves, BlockStateProvider drooping_leaves,
                         int height) implements FeatureConfiguration {
        public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BlockStateProvider.CODEC.fieldOf("trunk_block").forGetter(Config::trunk),
                BlockStateProvider.CODEC.fieldOf("leaves_block").forGetter(Config::leaves),
                BlockStateProvider.CODEC.fieldOf("drooping_leaves_block").forGetter(Config::drooping_leaves),
                Codec.INT.fieldOf("height").forGetter(YellowWillowFeature.Config::height)
        ).apply(instance, Config::new));
    }
}
