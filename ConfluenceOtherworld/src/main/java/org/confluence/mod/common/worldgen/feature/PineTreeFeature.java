package org.confluence.mod.common.worldgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import org.confluence.mod.common.block.natural.BaseDroopingPlantsHeadBlock;

import static org.confluence.lib.util.FeatureUtils.updateLeavesOptimized;

public class PineTreeFeature extends Feature<PineTreeFeature.Config> {
    public PineTreeFeature(Codec<Config> pCodec) {
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

        LongArrayList trunkX = new LongArrayList();
        LongArrayList trunkY = new LongArrayList();
        LongArrayList trunkZ = new LongArrayList();

        for (int i = 0; i < height / 4; i++) {
            int brunchHeight = height - i * 4;
            if (brunchHeight < height / 3.0) break;

            int length = i * 2 + 1;
            boolean rotate = (i % 2 == 0);

            for (int j = 0; j < 4; j++) {
                Direction dir = Direction.from2DDataValue(j);
                Direction sideDir = Direction.from2DDataValue((j + 1) % 4);
                int hOffset = brunchHeight + (random.nextFloat() < 0.2 ? -1 : (random.nextFloat() > 0.8 ? 1 : 0));

                for (int k = 0; k < length; k++) {
                    BlockPos bPos = basePos.offset(0, hOffset - 1 - (int) (Mth.sqrt(k) * 1.5), 0)
                            .relative(dir, rotate ? k : (int) (k * 0.72))
                            .relative(sideDir, rotate ? 0 : (int) (k * 0.72));

                    long bPosLong = bPos.asLong();
                    if (j % 2 == 0) trunkZ.add(bPosLong);
                    else trunkX.add(bPosLong);
                    trunkSet.add(bPosLong);

                    int side = (int) (Mth.sqrt(length - k) / 3 + 1);
                    for (int x = -side; x <= side; x++) {
                        for (int z = -side; z <= side; z++) {
                            if (Mth.abs(x) + Mth.abs(z) <= side) {
                                BlockPos lPos = bPos.offset(x, 0, z);
                                if (level.getBlockState(lPos).canBeReplaced() && random.nextFloat() >= 0.5F * (1 + (float) (Mth.abs(x) + Mth.abs(z) - side) / side)) {
                                    long lPosLong = lPos.asLong();
                                    leavesSet.add(lPosLong);

                                    if (random.nextFloat() < ((float) (length - k) / length) * 0.5) {
                                        int vLen = random.nextInt(1, (int) ((float) (length - k) / length * 2.5 + 2));
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
        }

        for (int i = 0; i < height; i++) {
            long p = basePos.above(i).asLong();
            trunkY.add(p);
            trunkSet.add(p);
        }
        leavesSet.add(basePos.above(height).asLong());
        leavesSet.add(basePos.above(height + 1).asLong());

        for (long p : trunkSet) {
            BlockPos bp = BlockPos.of(p);
            if (!level.getBlockState(bp).canBeReplaced())
                return false;
        }

        int flags = 19;

        for (long p : vinePosList) level.setBlock(BlockPos.of(p), vineBlock, flags);

        for (long p : leavesSet) {
            level.setBlock(BlockPos.of(p), leavesBlock, flags);
        }

        trunkX.forEach(p -> level.setBlock(BlockPos.of(p), trunkBlock.setValue(BlockStateProperties.AXIS, Direction.Axis.X), flags));
        trunkZ.forEach(p -> level.setBlock(BlockPos.of(p), trunkBlock.setValue(BlockStateProperties.AXIS, Direction.Axis.Z), flags));
        trunkY.forEach(p -> level.setBlock(BlockPos.of(p), trunkBlock, flags));
        for (long p : vinePosList) {
            BlockPos vp = BlockPos.of(p);
            if (level.getBlockState(vp).is(vineBlock.getBlock())) {
                boolean up = level.getBlockState(vp.above()).is(vineBlock.getBlock());
                boolean down = level.getBlockState(vp.below()).is(vineBlock.getBlock());
                if (up && down) {
                    level.setBlock(vp, vineBlock.setValue(BaseDroopingPlantsHeadBlock.PART, BaseDroopingPlantsHeadBlock.VinePart.BODY), flags);
                } else if (down) {
                    level.setBlock(vp, vineBlock.setValue(BaseDroopingPlantsHeadBlock.PART, BaseDroopingPlantsHeadBlock.VinePart.TAIL), flags);
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
