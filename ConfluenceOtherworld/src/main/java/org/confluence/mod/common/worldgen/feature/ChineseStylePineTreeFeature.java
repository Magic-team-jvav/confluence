package org.confluence.mod.common.worldgen.feature;

import PortLib.extensions.java.util.List.PortListExtension;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import org.confluence.lib.util.LibGeometryUtils;
import org.confluence.lib.util.LibMathUtils;
import org.confluence.mod.common.block.natural.BaseDroopingPlantsHeadBlock;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

import static org.confluence.lib.util.LibFeatureUtils.updateLeavesOptimized;

public class ChineseStylePineTreeFeature extends Feature<ChineseStylePineTreeFeature.Config> {
    public ChineseStylePineTreeFeature(Codec<Config> pCodec) {
        super(pCodec);
    }

    @Override
    public boolean place(FeaturePlaceContext<Config> pContext) {
        RandomSource random = pContext.random();
        Config config = pContext.config();
        WorldGenLevel level = pContext.level();
        BlockPos basePos = pContext.origin();

        BlockState trunkBlock = config.trunk().getState(random, basePos);
        BlockState vineBlock = config.vine().getState(random, basePos);
        BlockState leavesBlock = config.leaves().getState(random, basePos);

        int height = config.height() + random.nextInt(config.heightMore() + 1);

        LongOpenHashSet trunkSet = new LongOpenHashSet();
        LongOpenHashSet leavesSet = new LongOpenHashSet();
        LongArrayList vinePosList = new LongArrayList();

        List<Vector3f> trunkPath = new ArrayList<>();
        int offset = height / 3 * 2;
        int xOff = random.nextInt(offset / 2, offset + 1) * (random.nextBoolean() ? 1 : -1);
        int zOff = random.nextInt(offset / 2, offset + 1) * (random.nextBoolean() ? 1 : -1);
        BlockPos leavesPos;

        trunkPath.add(LibMathUtils.toVector3f(basePos));
        trunkPath.add(LibMathUtils.toVector3f(basePos.offset(xOff / 4, height / 2, zOff / 4)));
        trunkPath.add(LibMathUtils.toVector3f(basePos.offset(xOff / 4 * 3, height / 2, zOff / 4 * 3)));
        trunkPath.add(LibMathUtils.toVector3f(basePos.offset(xOff, height / 2 - 1, zOff)));
        trunkPath.add(LibMathUtils.toVector3f(basePos.offset(xOff / 2 * 3, height / 2, zOff / 2 * 3)));
        List<List<Vector3f>> segments = LibGeometryUtils.lightningPathList(trunkPath, 1, 0.2F, random, random.nextInt(2, 5), 0.8F);

        trunkPath.clear();
        trunkPath.add(LibMathUtils.toVector3f(basePos.offset(xOff / 8, height / 4, zOff / 8)));
        trunkPath.add(LibMathUtils.toVector3f(basePos.offset(-xOff / 3, height / 4, -zOff / 3)));
        trunkPath.add(LibMathUtils.toVector3f(basePos.offset(-xOff / 2, height / 4 * 3, -zOff / 2)));
        segments.addAll(LibGeometryUtils.lightningPathList(trunkPath, 1, 0.2F, random, random.nextInt(2, 5), 0.8F));

        trunkPath.clear();
        trunkPath.add(LibMathUtils.toVector3f(basePos.offset(xOff / 4 * 3, height / 2, zOff / 4 * 3)));
        trunkPath.add(LibMathUtils.toVector3f(basePos.offset(xOff, height, zOff)));
        LibGeometryUtils.lightningPathList(trunkPath, 1, 0.2F, random);
        segments.add(trunkPath);

        for (List<Vector3f> segment : segments) {
            for (Vector3f v : segment) {
                trunkSet.add(BlockPos.containing(v.x, v.y, v.z).asLong());
            }
            Vector3f lastV = PortListExtension.getLast(segment);
            leavesPos = BlockPos.containing(lastV.x, lastV.y, lastV.z);
            int mainSide = random.nextInt(3, 6);
            int side;
            for (int y = -1; y < 3; y++) {
                side = mainSide - Mth.abs(y);
                for (int x = -side; x <= side; x++) {
                    for (int z = -side; z <= side; z++) {
                        if (Mth.abs(x) + Mth.abs(z) <= side) {
                            BlockPos lPos = leavesPos.offset(x, y, z);
                            if (level.getBlockState(lPos).canBeReplaced() && random.nextFloat() >= 0.5F * (1 + (float) (Mth.abs(x) + Mth.abs(z) - side) / side)) {
                                long lPosLong = lPos.asLong();
                                leavesSet.add(lPosLong);
                                if (random.nextFloat() > 0.67F) {
                                    int vLen = random.nextInt(1, 3);
                                    for (int l = 1; l <= vLen; l++) {
                                        BlockPos vPos = lPos.below(l);
                                        if (level.getBlockState(vPos).canBeReplaced())
                                            vinePosList.add(vPos.asLong());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        for (long p : trunkSet) {
            BlockPos bp = BlockPos.of(p);
            if (!level.getBlockState(bp).canBeReplaced())
                return false;
        }

        vinePosList.forEach(p -> level.setBlock(BlockPos.of(p), vineBlock, 3));
        leavesSet.forEach(p -> level.setBlock(BlockPos.of(p), leavesBlock, 3));
        trunkSet.forEach(p -> level.setBlock(BlockPos.of(p), trunkBlock, 3));
        for (long p : vinePosList) {
            BlockPos vp = BlockPos.of(p);
            if (level.getBlockState(vp).is(vineBlock.getBlock())) {
                boolean up = level.getBlockState(vp.above()).is(vineBlock.getBlock());
                boolean down = level.getBlockState(vp.below()).is(vineBlock.getBlock());
                if (up && down) {
                    level.setBlock(vp, vineBlock.setValue(BaseDroopingPlantsHeadBlock.PART, BaseDroopingPlantsHeadBlock.VinePart.BODY), 3);
                } else if (down) {
                    level.setBlock(vp, vineBlock.setValue(BaseDroopingPlantsHeadBlock.PART, BaseDroopingPlantsHeadBlock.VinePart.TAIL), 3);
                }
            }
        }
        updateLeavesOptimized(level, trunkSet, leavesSet, true, false);

        return true;
    }

    public record Config(BlockStateProvider trunk, BlockStateProvider vine,
                         BlockStateProvider leaves, int height,
                         int heightMore) implements FeatureConfiguration {
        public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BlockStateProvider.CODEC.fieldOf("trunk_block").forGetter(Config::trunk),
                BlockStateProvider.CODEC.fieldOf("vine_block").forGetter(Config::vine),
                BlockStateProvider.CODEC.fieldOf("leaves_block").forGetter(Config::leaves),
                Codec.INT.fieldOf("height").forGetter(Config::height),
                Codec.INT.fieldOf("height_more").forGetter(Config::heightMore)
        ).apply(instance, Config::new));
    }
}
