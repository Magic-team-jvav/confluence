package org.confluence.mod.common.worldgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import org.confluence.mod.mixed.IWorldGenRegion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PalmTreeFeature extends Feature<PalmTreeFeature.Config> {
    public PalmTreeFeature(Codec<Config> pCodec) {
        super(pCodec);
    }

    @Override
    public boolean place(FeaturePlaceContext<Config> pContext) {
        int height = 7 + pContext.random().nextInt(3);
        boolean facingT = pContext.random().nextBoolean();
        boolean facingF = pContext.random().nextBoolean();
        List<Integer> leavesListX = new ArrayList<>(Arrays.asList(0, 3, 1, -3, -1, 0, 0, 0, 0, 1, 1, -1, -1, 0, 0, 0, 0, 1, -1, 2, -2, 0, 0, 4, -4, 0, 0, 2, -2, 2, 2, -2, -2));
        List<Integer> leavesListY = new ArrayList<>(Arrays.asList(1, 1, 0, 1, 0, 1, 0, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, -1, -1, -1, -1, 0, 0, 0, 0));
        List<Integer> leavesListZ = new ArrayList<>(Arrays.asList(0, 0, 0, 0, 0, 3, 1, -3, -1, 1, -1, 1, -1, 1, -1, 2, -2, 0, 0, 0, 0, 4, -4, 0, 0, 2, -2, 0, 0, 2, -2, 2, -2));
        List<Integer> leavesListT = new ArrayList<>(Arrays.asList(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3));
        int treeX;
        int treeZ;
        int bl = facingF ? 1 : -1;
        Config config = pContext.config();
        WorldGenLevel level = pContext.level();
        IWorldGenRegion worldGenRegion = (IWorldGenRegion) level;
        BlockPos trunkBlockPos = pContext.origin();
        BlockPos leavesBlockPos = pContext.origin();
        BlockState trunkBlockState = config.trunk().getState(pContext.random(), trunkBlockPos);
        BlockState leavesBlockState1 = config.leaves1().getState(pContext.random(), leavesBlockPos);
        BlockState leavesBlockState2 = config.leaves2().getState(pContext.random(), leavesBlockPos);
        BlockState leavesBlockState3 = config.leaves3().getState(pContext.random(), leavesBlockPos);
        List<BlockState> leavesBlocks = new ArrayList<>(Arrays.asList(leavesBlockState1, leavesBlockState2, leavesBlockState3));

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
                worldGenRegion.confluence$setBlock(trunkBlockPosPlace, trunkBlockState, 2);
            }
            treeX = 0;
            treeZ = 0;

            if (facingT) {
                treeZ = (int) (Math.sqrt(height)) * bl;
            } else {
                treeX = (int) (Math.sqrt(height)) * bl;
            }
            leavesBlockPos = new BlockPos(leavesBlockPos.getX() - treeX, leavesBlockPos.getY() + height - 1, leavesBlockPos.getZ() - treeZ);
            for (int i = 0; i < leavesListT.size(); i++) {
                BlockPos leavesBlockPosPlace = new BlockPos(leavesBlockPos.getX() + leavesListX.get(i), leavesBlockPos.getY() + leavesListY.get(i), leavesBlockPos.getZ() + leavesListZ.get(i));
                BlockState leavesPos = level.getBlockState(leavesBlockPosPlace);
                if (leavesPos.isAir()) {
                    worldGenRegion.confluence$setBlock(leavesBlockPosPlace, leavesBlocks.get(leavesListT.get(i) - 1), 2);
                }
            }

            return true;
        }
        return false;
    }

    public record Config(BlockStateProvider trunk, BlockStateProvider leaves1, BlockStateProvider leaves2, BlockStateProvider leaves3) implements FeatureConfiguration {
        public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BlockStateProvider.CODEC.fieldOf("trunk_block").forGetter(Config::trunk),
                BlockStateProvider.CODEC.fieldOf("leaves_block_bottom").forGetter(Config::leaves1),
                BlockStateProvider.CODEC.fieldOf("leaves_block_top").forGetter(Config::leaves2),
                BlockStateProvider.CODEC.fieldOf("leaves_block_double").forGetter(Config::leaves3)
        ).apply(instance, Config::new));
    }
}
