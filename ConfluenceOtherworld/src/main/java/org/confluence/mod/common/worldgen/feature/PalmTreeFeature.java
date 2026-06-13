package org.confluence.mod.common.worldgen.feature;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

import java.util.List;

public class PalmTreeFeature extends Feature<PalmTreeFeature.Config> {
    public static final int[] LEAVES_LIST_X = new int[]{0, 3, 1, -3, -1, 0, 0, 0, 0, 1, 1, -1, -1, 0, 0, 0, 0, 1, -1, 2, -2, 0, 0, 4, -4, 0, 0, 2, -2, 2, 2, -2, -2};
    public static final int[] LEAVES_LIST_Y = new int[]{1, 1, 0, 1, 0, 1, 0, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, -1, -1, -1, -1, 0, 0, 0, 0};
    public static final int[] LEAVES_LIST_Z = new int[]{0, 0, 0, 0, 0, 3, 1, -3, -1, 1, -1, 1, -1, 1, -1, 2, -2, 0, 0, 0, 0, 4, -4, 0, 0, 2, -2, 0, 0, 2, -2, 2, -2};
    public static final int[] LEAVES_LIST_T = new int[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3};
    public static final int[] LEAVES_LIST_D = new int[]{1, 3, 1, 3, 1, 3, 1, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 4, 4, 4, 4, 2, 2, 2, 2, 2, 2, 2, 2};

    public PalmTreeFeature(Codec<Config> pCodec) {
        super(pCodec);
    }

    @Override
    public boolean place(FeaturePlaceContext<Config> pContext) {
        RandomSource random = pContext.random();
        int height = 7 + random.nextInt(3);
        boolean facingT = random.nextBoolean();
        boolean facingF = random.nextBoolean();
        int treeX;
        int treeZ;
        int bl = facingF ? 1 : -1;
        Config config = pContext.config();
        WorldGenLevel level = pContext.level();
        BlockPos trunkBlockPos = pContext.origin();
        BlockPos leavesBlockPos = pContext.origin();
        BlockState trunkBlockState = config.trunk().getState(random, trunkBlockPos);
        BlockState leavesBlockState1 = config.leavesBlockBottom().getState(random, leavesBlockPos);
        BlockState leavesBlockState2 = config.leavesBlockTop().getState(random, leavesBlockPos);
        BlockState leavesBlockState3 = config.leaveBlockDouble().getState(random, leavesBlockPos);
        List<BlockState> leavesBlocks = Lists.newArrayList(leavesBlockState1, leavesBlockState2, leavesBlockState3);

        boolean placed = true;

        for (int checkY = 1; checkY <= height; checkY++) {
            treeX = 0;
            treeZ = 0;
            double v = (Math.sqrt(-checkY + height) - Math.sqrt(height)) * bl;
            if (facingT) {
                treeZ = (int) v;
            } else {
                treeX = (int) v;
            }
            BlockPos trunkBlockPosPlace = new BlockPos(trunkBlockPos.getX() + treeX, trunkBlockPos.getY() + checkY - 1, trunkBlockPos.getZ() + treeZ);
            BlockState trunkPos = level.getBlockState(trunkBlockPosPlace);
            if (!(trunkPos.isAir() || trunkPos.is(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("minecraft", "leaves"))))) {
                placed = false;
            }
        }

        if (placed) {

            for (int treeY = 1; treeY <= height; treeY++) {
                treeX = 0;
                treeZ = 0;
                double v = (Math.sqrt(-treeY + height) - Math.sqrt(height)) * bl;
                if (facingT) {
                    treeZ = (int) v;
                } else {
                    treeX = (int) v;
                }
                BlockPos trunkBlockPosPlace = new BlockPos(trunkBlockPos.getX() + treeX, trunkBlockPos.getY() + treeY - 1, trunkBlockPos.getZ() + treeZ);
                level.setBlock(trunkBlockPosPlace, trunkBlockState, 3);
            }
            treeX = 0;
            treeZ = 0;

            if (facingT) {
                treeZ = (int) (Math.sqrt(height)) * bl;
            } else {
                treeX = (int) (Math.sqrt(height)) * bl;
            }
            leavesBlockPos = new BlockPos(leavesBlockPos.getX() - treeX, leavesBlockPos.getY() + height - 1, leavesBlockPos.getZ() - treeZ);
            for (int i = 0; i < LEAVES_LIST_X.length; i++) {
                BlockPos leavesBlockPosPlace = leavesBlockPos.offset(LEAVES_LIST_X[i], LEAVES_LIST_Y[i], LEAVES_LIST_Z[i]);
                BlockState leavesPos = level.getBlockState(leavesBlockPosPlace);
                if (leavesPos.isAir()) {
                    level.setBlock(leavesBlockPosPlace, leavesBlocks.get(LEAVES_LIST_T[i] - 1).setValue(BlockStateProperties.DISTANCE, LEAVES_LIST_D[i]), 3);
                }
            }

            return true;
        }
        return false;
    }

    public record Config(
            BlockStateProvider trunk, BlockStateProvider leavesBlockBottom,
            BlockStateProvider leavesBlockTop,
            BlockStateProvider leaveBlockDouble
    ) implements FeatureConfiguration {
        public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BlockStateProvider.CODEC.fieldOf("trunk_block").forGetter(Config::trunk),
                BlockStateProvider.CODEC.fieldOf("leaves_block_bottom").forGetter(Config::leavesBlockBottom),
                BlockStateProvider.CODEC.fieldOf("leaves_block_top").forGetter(Config::leavesBlockTop),
                BlockStateProvider.CODEC.fieldOf("leaves_block_double").forGetter(Config::leaveBlockDouble)
        ).apply(instance, Config::new));
    }
}
