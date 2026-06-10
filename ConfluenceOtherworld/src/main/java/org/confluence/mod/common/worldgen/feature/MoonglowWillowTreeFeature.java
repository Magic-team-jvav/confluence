package org.confluence.mod.common.worldgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import org.confluence.lib.util.LibFeatureUtils;
import org.confluence.lib.util.LibGeometryUtils;
import org.confluence.lib.util.LibVectorUtils;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.List;

public class MoonglowWillowTreeFeature extends Feature<MoonglowWillowTreeFeature.Config> {
    public MoonglowWillowTreeFeature(Codec<Config> pCodec) {
        super(pCodec);
    }
    private static final TagKey<Block> END_PLANT_CAN_SURVIVE = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("confluence", "end_plant_can_survive"));

    @Override
    public boolean place(FeaturePlaceContext<Config> pContext) {
        RandomSource random = pContext.random();
        Config config = pContext.config();
        WorldGenLevel level = pContext.level();
        BlockPos basePos = pContext.origin();

        if (!level.getBlockState(basePos.below()).is(END_PLANT_CAN_SURVIVE)) return false;

        BlockState trunkBlock = config.trunk().getState(random, basePos);
        BlockState vineBlock = config.vine().getState(random, basePos);
        BlockState leavesBlock = config.leaves().getState(random, basePos);

        LongOpenHashSet trunkSet = new LongOpenHashSet();
        LongOpenHashSet leavesSet = new LongOpenHashSet();
        LongOpenHashSet vineSet = new LongOpenHashSet();

        int height = random.nextInt(5, 15);

        BlockPos endPos = basePos.offset(random.nextInt(-5, 6), height, random.nextInt(-5, 6));
        Vector3d baseVct = new Vector3d(basePos.getX(), basePos.getY(), basePos.getZ());
        Vector3d endVct = new Vector3d(endPos.getX(), endPos.getY(), endPos.getZ());
        List<Vector3d> trunkList = new ArrayList<>(List.of(baseVct, endVct));
        LibGeometryUtils.lightningPathList(trunkList, 0.5, 0.2F, random);

        for (Vector3d pos : trunkList) {
            BlockPos bPos = LibVectorUtils.fromVector3d(pos);
            if (!level.getBlockState(bPos).canBeReplaced()) return false;
            trunkSet.add(bPos.asLong());
        }

        int leavesCount = random.nextInt(4, 7);
        float startRotate = random.nextFloat() * 2 * Mth.PI;
        float stepRotate = 2 * Mth.PI / leavesCount;

        for (int i = 0; i < leavesCount; i++) {
            int leavesLength = random.nextInt(4, 7);
            int leavesHeight = random.nextInt(3, 5);
            float halfLength = (float) leavesLength / 2;
            float trueRotate = startRotate + stepRotate * i;
            int xMax = (int) (leavesLength * Mth.sin(trueRotate));
            int zMax = (int) (leavesLength * Mth.cos(trueRotate));
            if (Mth.abs(xMax) > Mth.abs(zMax)) {
                float oneStep = (float) zMax / Mth.abs(xMax);
                float oneStepLength = Mth.sqrt(oneStep * oneStep + 1);
                for (int x = 0; Mth.abs(x) <= Mth.abs(xMax); x += ((xMax > 0) ? 1 : -1)) {
                    int z = (int) (oneStep * Mth.abs(x));
                    int y = (int) ((Math.pow(halfLength, 2) - Math.pow(oneStepLength * Mth.abs(x) - halfLength, 2)) / Math.pow(halfLength, 2) * leavesHeight);
                    BlockPos leavesPos = endPos.offset(x, y, z);
                    if (level.getBlockState(leavesPos).canBeReplaced()) {
                        if (Mth.abs(x) <= Mth.abs(xMax / 2)) trunkSet.add(leavesPos.asLong());
                        else leavesSet.add(leavesPos.asLong());
                    }
                    for (int j = 0; j < 6; j++) {
                        Direction direction = Direction.from3DDataValue(j);
                        BlockPos leavesPos1 = leavesPos.relative(direction);
                        if (level.getBlockState(leavesPos1).canBeReplaced()) leavesSet.add(leavesPos1.asLong());
                        if (random.nextFloat() > 0.5) continue;
                        int vineLength = random.nextInt(1, 8) * (1 - (Mth.abs(xMax) - Mth.abs(x)) / Mth.abs(xMax));
                        for (int k = 1; k <= vineLength; k++) {
                            BlockPos vinePos = leavesPos1.below(k);
                            if (level.getBlockState(vinePos).canBeReplaced()) vineSet.add(vinePos.asLong());
                        }
                    }
                }
            } else {
                float oneStep = (float) xMax / Mth.abs(zMax);
                float oneStepLength = Mth.sqrt(oneStep * oneStep + 1);
                for (int z = 0; Mth.abs(z) <= Mth.abs(zMax); z += ((zMax > 0) ? 1 : -1)) {
                    int x = (int) (oneStep * Mth.abs(z));
                    int y = (int) ((Math.pow(halfLength, 2) - Math.pow(oneStepLength * Mth.abs(z) - halfLength, 2)) / Math.pow(halfLength, 2) * leavesHeight);
                    BlockPos leavesPos = endPos.offset(x, y, z);
                    if (level.getBlockState(leavesPos).canBeReplaced()) {
                        if (Mth.abs(z) <= Mth.abs(zMax / 2)) trunkSet.add(leavesPos.asLong());
                        else leavesSet.add(leavesPos.asLong());
                    }
                    for (int j = 0; j < 6; j++) {
                        Direction direction = Direction.from3DDataValue(j);
                        BlockPos leavesPos1 = leavesPos.relative(direction);
                        if (level.getBlockState(leavesPos1).canBeReplaced()) leavesSet.add(leavesPos1.asLong());
                        if (random.nextFloat() > 0.5) continue;
                        int vineLength = random.nextInt(1, 8) * (1 - (Mth.abs(zMax) - Mth.abs(z)) / Mth.abs(zMax));
                        for (int k = 1; k <= vineLength; k++) {
                            BlockPos vinePos = leavesPos1.below(k);
                            if (level.getBlockState(vinePos).canBeReplaced()) vineSet.add(vinePos.asLong());
                        }
                    }
                }
            }
        }

        vineSet.forEach(p -> level.setBlock(BlockPos.of(p), vineBlock, 3));
        leavesSet.forEach(p -> level.setBlock(BlockPos.of(p), leavesBlock, 3));
        trunkSet.forEach(p -> level.setBlock(BlockPos.of(p), trunkBlock, 3));
        LibFeatureUtils.updateLeavesOptimized(level, trunkSet, leavesSet, true, false);
        for (Long debugLong : vineSet) {
            BlockPos debugPos = BlockPos.of(debugLong);
            BlockState debugState = level.getBlockState(debugPos.above());
            if (!debugState.is(trunkBlock.getBlock()) && !debugState.is(leavesBlock.getBlock()) && !debugState.is(vineBlock.getBlock())) {
                BlockPos checkPos = debugPos;
                while (level.getBlockState(checkPos).is(vineBlock.getBlock())) {
                    level.setBlock(checkPos, Blocks.AIR.defaultBlockState(), 3);
                    checkPos = checkPos.below();
                }
            }
        }

        return true;
    }

    public record Config(BlockStateProvider trunk,
                         BlockStateProvider vine,
                         BlockStateProvider leaves) implements FeatureConfiguration {
        public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BlockStateProvider.CODEC.fieldOf("trunk_block").forGetter(Config::trunk),
                BlockStateProvider.CODEC.fieldOf("vine_block").forGetter(Config::vine),
                BlockStateProvider.CODEC.fieldOf("leaves_block").forGetter(Config::leaves)
        ).apply(instance, Config::new));
    }
}
