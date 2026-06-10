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
import org.confluence.lib.util.LibFeatureUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DroopingVineTreeFeature extends Feature<DroopingVineTreeFeature.Config> {
    public DroopingVineTreeFeature(Codec<Config> pCodec) {
        super(pCodec);
    }

    private static void setLeaves(BoundingBox box, BlockState leaves, boolean up, RandomSource random, WorldGenLevel level) {
        LibFeatureUtils.leaves(box, leaves, up, random, level, Blocks.AIR.defaultBlockState(), false);
    }

    private static void setLeaves(BoundingBox box, BlockState leaves, boolean up, RandomSource random, WorldGenLevel level, BlockState droopingLeaves) {
        LibFeatureUtils.leaves(box, leaves, up, random, level, droopingLeaves, true);
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
        BoundingBox box = new BoundingBox(baseBlockPos.getX() - 2, baseBlockPos.getY(), baseBlockPos.getZ() - 2, baseBlockPos.getX() + 2, baseBlockPos.getY() + height + 4, baseBlockPos.getZ() + 2);
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
                rootPos.add(trunkPosList.get(i));
            }
            setLeaves(new BoundingBox(baseBlockPos.getX() - 2, baseBlockPos.getY() + height, baseBlockPos.getZ() - 2, baseBlockPos.getX() + 2, baseBlockPos.getY() + height + 1, baseBlockPos.getZ() + 2), leavesBlockState, true, random, level, droopingLeavesBlockState);
            setLeaves(new BoundingBox(baseBlockPos.getX() - 1, baseBlockPos.getY() + height + 2, baseBlockPos.getZ() - 1, baseBlockPos.getX() + 1, baseBlockPos.getY() + height + 3, baseBlockPos.getZ() + 1), leavesBlockState, false, random, level);
            TreeFeature.updateLeaves(level, box, rootPos, trunkPos, leavesPos);
            return true;
        }
        return false;
    }

    public record Config(BlockStateProvider trunk, BlockStateProvider leaves,
                         BlockStateProvider drooping_leaves,
                         int height) implements FeatureConfiguration {
        public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BlockStateProvider.CODEC.fieldOf("trunk_block").forGetter(Config::trunk),
                BlockStateProvider.CODEC.fieldOf("leaves_block").forGetter(Config::leaves),
                BlockStateProvider.CODEC.fieldOf("drooping_vine_block").forGetter(Config::drooping_leaves),
                Codec.INT.fieldOf("height").forGetter(DroopingVineTreeFeature.Config::height)
        ).apply(instance, Config::new));
    }
}
