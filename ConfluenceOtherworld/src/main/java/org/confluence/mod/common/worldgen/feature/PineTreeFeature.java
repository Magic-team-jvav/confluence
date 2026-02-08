package org.confluence.mod.common.worldgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import org.confluence.lib.util.VectorUtils;
import org.confluence.mod.common.block.natural.BaseDroopingPlantsHeadBlock;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        TagKey<Block> SAPLING_TAG = TagKey.create(Registries.BLOCK, ResourceLocation.parse("minecraft:saplings"));

        if (config.chineseStyle()) {
            List<Vector3d> trunkPath = new ArrayList<>();
            int offset = height / 3 * 2;
            int xOff = random.nextInt(offset / 2, offset + 1) * (random.nextBoolean() ? 1 : -1);
            int zOff = random.nextInt(offset / 2, offset + 1) * (random.nextBoolean() ? 1 : -1);

            trunkPath.add(VectorUtils.toVector3d(basePos));
            trunkPath.add(VectorUtils.toVector3d(basePos.offset(xOff / 4, height / 2, zOff / 4)));
            trunkPath.add(VectorUtils.toVector3d(basePos.offset(xOff, height, zOff)));

            List<List<Vector3d>> segments = VectorUtils.lightningPathList(trunkPath, 1, 0.2F, random, random.nextInt(2, 5), 0.6F);
            for (List<Vector3d> segment : segments) {
                for (Vector3d v : segment) {
                    level.setBlock(BlockPos.containing(v.x, v.y, v.z), trunkBlock, 3);
                }
            }
            return true;
        }

        Set<BlockPos> trunkSet = new HashSet<>();
        Set<BlockPos> leavesSet = new HashSet<>();
        List<BlockPos> vinePosList = new ArrayList<>();

        List<BlockPos> trunkX = new ArrayList<>();
        List<BlockPos> trunkY = new ArrayList<>();
        List<BlockPos> trunkZ = new ArrayList<>();

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

                    if (j % 2 == 0) trunkZ.add(bPos);
                    else trunkX.add(bPos);
                    trunkSet.add(bPos);

                    int side = (int) (Mth.sqrt(length - k) / 3 + 1);
                    for (int x = -side; x <= side; x++) {
                        for (int z = -side; z <= side; z++) {
                            if (Mth.abs(x) + Mth.abs(z) <= side) {
                                BlockPos lPos = bPos.offset(x, 0, z);
                                if (level.getBlockState(lPos).canBeReplaced() && random.nextFloat() >= 0.5F * (1 + (float) (Mth.abs(x) + Mth.abs(z) - side) / side)) {
                                    leavesSet.add(lPos);

                                    if (random.nextFloat() < ((float) (length - k) / length) * 0.5) {
                                        int vLen = random.nextInt(1, (int) ((float) (length - k) / length * 2.5 + 2));
                                        for (int l = 1; l <= vLen; l++) {
                                            BlockPos vPos = lPos.below(l);
                                            if (level.getBlockState(vPos).canBeReplaced())
                                                vinePosList.add(vPos);
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
            BlockPos p = basePos.above(i);
            trunkY.add(p);
            trunkSet.add(p);
        }
        leavesSet.add(basePos.above(height));
        leavesSet.add(basePos.above(height + 1));

        for (BlockPos p : trunkSet) {
            if (!level.getBlockState(p).canBeReplaced() && !level.getBlockState(p).is(SAPLING_TAG))
                return false;
        }

        int flags = 19;

        for (BlockPos vp : vinePosList) level.setBlock(vp, vineBlock, flags);
        for (BlockPos vp : vinePosList) {
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

        for (BlockPos lp : leavesSet) {
            level.setBlock(lp, leavesBlock, flags);
        }

        trunkX.forEach(p -> level.setBlock(p, trunkBlock.setValue(BlockStateProperties.AXIS, Direction.Axis.X), flags));
        trunkZ.forEach(p -> level.setBlock(p, trunkBlock.setValue(BlockStateProperties.AXIS, Direction.Axis.Z), flags));
        trunkY.forEach(p -> level.setBlock(p, trunkBlock, flags));

        updateLeavesOptimized(level, trunkSet, leavesSet, true, false);

        return true;
    }

    public record Config(BlockStateProvider trunk, BlockStateProvider vine,
                         BlockStateProvider leaves,
                         int height, int heightMore,
                         boolean chineseStyle) implements FeatureConfiguration {
        public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BlockStateProvider.CODEC.fieldOf("trunk_block").forGetter(Config::trunk),
                BlockStateProvider.CODEC.fieldOf("vine_block").forGetter(Config::vine),
                BlockStateProvider.CODEC.fieldOf("leaves_block").forGetter(Config::leaves),
                Codec.INT.fieldOf("height").forGetter(Config::height),
                Codec.INT.fieldOf("height_more").forGetter(Config::heightMore),
                Codec.BOOL.fieldOf("chinese_style").forGetter(Config::chineseStyle)
        ).apply(instance, Config::new));
    }
}
