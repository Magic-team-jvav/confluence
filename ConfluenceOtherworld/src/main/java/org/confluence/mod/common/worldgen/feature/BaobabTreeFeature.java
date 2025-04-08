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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.confluence.lib.util.FeatureUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BaobabTreeFeature extends Feature<BaobabTreeFeature.Config> {
    public BaobabTreeFeature(Codec<Config> pCodec) {
        super(pCodec);
    }

    private static void setLeaves(BlockPos startPos, int size1, BlockState leaves, RandomSource random, WorldGenLevel level) {
        int x = startPos.getX();
        int y = startPos.getY();
        int z = startPos.getZ();
        int size = Math.max(size1, 1);
        Set<BlockPos> rootSet = new HashSet<>();
        Set<BlockPos> trunkSet = new HashSet<>();
        Set<BlockPos> leavesSet = new HashSet<>();
        rootSet.add(startPos);
        BoundingBox boxDown = new BoundingBox(x - size1, y, z - size1, x + size1, y, z + size1);
        BoundingBox boxUp = new BoundingBox(x - 1, y + 1, z - 1, x + 1, y + 1, z + 1);
        BoundingBox box = new BoundingBox(x - size, y, z - size, x + size, y + 1, z + size);
        FeatureUtils.leaves(boxDown, leaves, true, random, level, Blocks.AIR.defaultBlockState(), false);
        FeatureUtils.leaves(boxUp, leaves, true, random, level, Blocks.AIR.defaultBlockState(), false);
        TreeFeature.updateLeaves(level, box, rootSet, trunkSet, leavesSet);
    }

    @Override
    public boolean place(FeaturePlaceContext<Config> pContext) {
        RandomSource random = pContext.random();
        Config config = pContext.config();
        WorldGenLevel level = pContext.level();
        BlockPos baseBlockPos = pContext.origin();
        BlockState trunkBlockState = config.trunk().getState(random, baseBlockPos);
        BlockState branchBlockState = config.branch().getState(random, baseBlockPos);
        BlockState rootBlockState = config.root().getState(random, baseBlockPos);
        BlockState innerBlockState = config.inner().getState(random, baseBlockPos);
        BlockState leavesBlockState = config.leaves().getState(random, baseBlockPos);
        int height = config.height + random.nextInt(3);
        int rootHeight;
        int branchLength;
        int xOffset;
        int zOffset;
        int xMin = baseBlockPos.getX() - 1;
        int xMax = baseBlockPos.getX() + 2;
        int zMin = baseBlockPos.getZ() - 1;
        int zMax = baseBlockPos.getZ() + 2;
        int end;
        int transitionLength;
        BlockPos rootPosEnd;
        BlockPos rootPosPlace;
        BlockPos transitionPos;
        boolean rootPlace;
        boolean rootOrBranch;
        BlockState placeRootOrBranch;
        BlockState placeXZ;
        List<BlockPos> trunkPosList = new ArrayList<>();
        List<BlockPos> rootPosList = new ArrayList<>();
        List<BlockPos> leavesPosList = new ArrayList<>();
        for (int y = 0; y < height; y++) {
            for (int x = -1; x < 3; x++) {
                for (int z = -1; z < 3; z++) {
                    trunkPosList.add(baseBlockPos.offset(x, y, z));
                }
            }
        }

        boolean placed = true;

        for (int k = 0; k < trunkPosList.size() && placed; k++) {
            placed = (level.getBlockState(trunkPosList.get(k)).canBeReplaced() || level.getBlockState(trunkPosList.get(k)).is(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("minecraft", "leaves"))));
        }
        for (int kx = -1; kx < 3; kx++) {
            for (int kz = -1; kz < 3; kz++) {
                placed = (level.getBlockState(baseBlockPos.offset(kx, -1, kz)).is(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("minecraft", "dirt"))) && placed);
            }
        }

        if (placed) {
            for (BlockPos pos : trunkPosList) {
                level.setBlock(pos, trunkBlockState, 3);
            }
            for (int y = 1; y < height - 1; y++) {
                for (int x = 0; x < 2; x++) {
                    for (int z = 0; z < 2; z++) {
                        level.setBlock(baseBlockPos.offset(x, y, z), innerBlockState, 3);
                    }
                }
            }
            for (int i = 0; i < 8; i++) {
                rootOrBranch = (i / 4 == 0);
                placeRootOrBranch = rootOrBranch ? rootBlockState : branchBlockState;
                branchLength = rootOrBranch ? 3 : 6;
                rootPlace = !rootOrBranch;
                xOffset = -1 * (int) Mth.cos(i * Mth.PI / 2);
                zOffset = -1 * (int) Mth.sin(i * Mth.PI / 2);
                placeXZ = placeRootOrBranch.trySetValue(BlockStateProperties.AXIS, (Mth.abs(xOffset) == 1) ? Direction.Axis.X : Direction.Axis.Z);
                end = (Mth.abs(xOffset) == 1) ? ((xOffset == 1 ) ? xMin : xMax) : ((zOffset == 1) ? zMin : zMax);
                rootPosEnd = baseBlockPos.offset(branchLength * (int) Mth.cos(i * Mth.PI / 2) + random.nextInt(2), 0, branchLength * (int) Mth.sin(i * Mth.PI / 2) + random.nextInt(2));
                rootPosPlace = rootPosEnd.offset(0, (rootOrBranch ? 0 : height - 1), 0);
                rootHeight = 0;
                for (int j = 0; ((j < 5) && !rootPlace); j++) {
                    rootPosPlace = rootPosEnd.offset(0, -j, 0);
                    rootPlace = level.getBlockState(rootPosPlace).canBeReplaced() && level.getBlockState(rootPosPlace.offset(0, -1, 0)).is(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("minecraft", "dirt")));
                    rootHeight = j;
                }
                rootHeight += random.nextInt(3) + 2;
                rootPosPlace = rootPosPlace.offset(0, (rootOrBranch || random.nextInt(4) != 3) ? 0 : (-height / 2 + 1), 0);
                for (int j = 0; j < rootHeight; j++) {
                    rootPosList.add(rootPosPlace.offset(0, j, 0));
                    rootPlace = level.getBlockState(rootPosPlace.offset(0, j, 0)).canBeReplaced() && rootPlace;
                }
                transitionPos = rootPosList.getLast().offset(xOffset,  - (rootOrBranch ? 0 : rootHeight - 1), zOffset);
                transitionLength = Mth.abs(end - (Mth.abs(xOffset) == 1 ? transitionPos.getX() : transitionPos.getZ()));
                if (transitionLength == 0) {
                    if (rootPlace) {
                        for (int j = 0; j < rootPosList.size(); j++) {
                            level.setBlock(rootPosList.get(j), placeRootOrBranch, 3);
                            if (((j + 1) == rootPosList.size()) && (!rootOrBranch)) {
                                leavesPosList.add(rootPosList.get(j));
                            }
                        }
                    }
                } else {
                    for (int j = 0; j < transitionLength; j ++) {
                        rootPlace = level.getBlockState(transitionPos.offset(xOffset * j, 0, zOffset * j)).canBeReplaced() && rootPlace;
                    }
                    if (rootPlace) {
                        for (int j = 0; j < rootPosList.size(); j++) {
                            level.setBlock(rootPosList.get(j), placeRootOrBranch, 3);
                            if (((j + 1) == rootPosList.size()) && (!rootOrBranch)) {
                                leavesPosList.add(rootPosList.get(j));
                            }
                        }
                        for (int j = 0; j < transitionLength; j ++) {
                            level.setBlock(transitionPos.offset(xOffset * j, 0, zOffset * j), placeXZ, 3);
                        }
                    }
                }
                rootPosList.clear();
            }
            for (BlockPos blockPos : leavesPosList) {
                setLeaves(blockPos, 2 + random.nextInt(2), leavesBlockState, random, level);
            }
            return true;
        }
        return false;
    }

    public record Config(BlockStateProvider trunk, BlockStateProvider branch, BlockStateProvider root, BlockStateProvider leaves, BlockStateProvider inner,
                         int height) implements FeatureConfiguration {
        public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BlockStateProvider.CODEC.fieldOf("trunk_block").forGetter(Config::trunk),
                BlockStateProvider.CODEC.fieldOf("branch_block").forGetter(Config::branch),
                BlockStateProvider.CODEC.fieldOf("root_block").forGetter(Config::root),
                BlockStateProvider.CODEC.fieldOf("leaves_block").forGetter(Config::leaves),
                BlockStateProvider.CODEC.fieldOf("inner_block").forGetter(Config::inner),
                Codec.INT.fieldOf("height").forGetter(BaobabTreeFeature.Config::height)
        ).apply(instance, Config::new));
    }
}
