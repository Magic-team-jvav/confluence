package org.confluence.mod.common.worldgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

import java.util.ArrayList;
import java.util.List;

public class BranchTreeFeature extends Feature<BranchTreeFeature.Config> {
    public BranchTreeFeature(Codec<Config> pCodec) {
        super(pCodec);
    }

    @Override
    public boolean place(FeaturePlaceContext<Config> pContext) {
        RandomSource random = pContext.random();
        Config config = pContext.config();
        WorldGenLevel level = pContext.level();
        BlockPos basePos = pContext.origin();
        BlockPos checkPos = basePos.offset(-3, 0, -3);
        BlockPos checkPosBranch;
        BlockState trunkBlockState = config.trunk().getState(random, basePos);
        BlockState branchBlockState = config.branch().getState(random, basePos);
        int height = config.height + random.nextInt(3);
        int branchLength = config.branchHeight;

        boolean placed = true;
        boolean placeBranch = true;
        List<BlockPos> branchPos = new ArrayList<>();

        for (int i = 0; i < height; i++) {
            placed = level.getBlockState(basePos.offset(0, i, 0)).isAir() && placed;
        }

        if (placed) {
            for (int i = 0; i < height; i++) {
                level.setBlock(basePos.offset(0, i, 0), trunkBlockState, 3);
            }
            branch(2, branchLength, placeBranch, branchPos, basePos.offset(0, height - 4, 0), branchBlockState, random, level);
            for (int x = 0; x < 7; x++) {
                for (int z = 0; z < 7; z++) {
                    for (int y = 0; y < (height + branchLength + 6); y++) {
                        checkPosBranch = checkPos.offset(x, y, z);
                        if (level.getBlockState(checkPosBranch).is(branchBlockState.getBlock())) {
                            if ((level.getBlockState(checkPosBranch.offset(0, -1, 0)).is(branchBlockState.getBlock())) || (level.getBlockState(checkPosBranch.offset(0, -1, 0)).is(trunkBlockState.getBlock()))) {
                                level.setBlock(checkPosBranch, level.getBlockState(checkPosBranch).trySetValue(PipeBlock.DOWN, Boolean.TRUE), 3);
                            } else {
                                if ((level.getBlockState(checkPosBranch.offset(1, 0, 0)).is(branchBlockState.getBlock())) || (level.getBlockState(checkPosBranch.offset(1, 0, 0)).is(trunkBlockState.getBlock()))) {
                                    level.setBlock(checkPosBranch, level.getBlockState(checkPosBranch).trySetValue(PipeBlock.EAST, Boolean.TRUE), 3);
                                    if (level.getBlockState(checkPosBranch.offset(1, 0, 0)).is(branchBlockState.getBlock())) {
                                        level.setBlock(checkPosBranch.offset(1, 0, 0), level.getBlockState(checkPosBranch.offset(1, 0, 0)).trySetValue(PipeBlock.WEST, Boolean.TRUE), 3);
                                    }
                                }
                                if ((level.getBlockState(checkPosBranch.offset(0, 0, 1)).is(branchBlockState.getBlock())) || (level.getBlockState(checkPosBranch.offset(0, 0, 1)).is(trunkBlockState.getBlock()))) {
                                    level.setBlock(checkPosBranch, level.getBlockState(checkPosBranch).trySetValue(PipeBlock.SOUTH, Boolean.TRUE), 3);
                                    if (level.getBlockState(checkPosBranch.offset(0, 0, 1)).is(branchBlockState.getBlock())) {
                                        level.setBlock(checkPosBranch.offset(0, 0, 1), level.getBlockState(checkPosBranch.offset(0, 0, 1)).trySetValue(PipeBlock.NORTH, Boolean.TRUE), 3);
                                    }
                                }
                                if ((level.getBlockState(checkPosBranch.offset(-1, 0, 0)).is(branchBlockState.getBlock())) || (level.getBlockState(checkPosBranch.offset(-1, 0, 0)).is(trunkBlockState.getBlock()))) {
                                    level.setBlock(checkPosBranch, level.getBlockState(checkPosBranch).trySetValue(PipeBlock.WEST, Boolean.TRUE), 3);
                                    if (level.getBlockState(checkPosBranch.offset(-1, 0, 0)).is(branchBlockState.getBlock())) {
                                        level.setBlock(checkPosBranch.offset(-1, 0, 0), level.getBlockState(checkPosBranch.offset(-1, 0, 0)).trySetValue(PipeBlock.EAST, Boolean.TRUE), 3);
                                    }
                                }
                                if ((level.getBlockState(checkPosBranch.offset(0, 0, -1)).is(branchBlockState.getBlock())) || (level.getBlockState(checkPosBranch.offset(0, 0, -1)).is(trunkBlockState.getBlock()))) {
                                    level.setBlock(checkPosBranch, level.getBlockState(checkPosBranch).trySetValue(PipeBlock.NORTH, Boolean.TRUE), 3);
                                    if (level.getBlockState(checkPosBranch.offset(0, 0, -1)).is(branchBlockState.getBlock())) {
                                        level.setBlock(checkPosBranch.offset(0, 0, -1), level.getBlockState(checkPosBranch.offset(0, 0, -1)).trySetValue(PipeBlock.SOUTH, Boolean.TRUE), 3);
                                    }
                                }
                            }
                            if ((level.getBlockState(checkPosBranch.offset(0, 1, 0)).is(branchBlockState.getBlock())) || (level.getBlockState(checkPosBranch.offset(0, 1, 0)).is(trunkBlockState.getBlock()))) {
                                level.setBlock(checkPosBranch, level.getBlockState(checkPosBranch).trySetValue(PipeBlock.UP, Boolean.TRUE), 3);
                            }
                        }
                    }
                }
            }
            return true;
        }
        return false;
    }

    public record Config(BlockStateProvider trunk, BlockStateProvider branch, int height,
                         int branchHeight) implements FeatureConfiguration {
        public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BlockStateProvider.CODEC.fieldOf("trunk_block").forGetter(Config::trunk),
                BlockStateProvider.CODEC.fieldOf("branch_block").forGetter(Config::branch),
                Codec.INT.fieldOf("height").forGetter(Config::height),
                Codec.INT.fieldOf("branch_height").forGetter(Config::branchHeight)
        ).apply(instance, Config::new));
    }

    private static void branch(int layer, int branchLength, boolean set, List<BlockPos> branchPos, BlockPos trunkPos, BlockState branchBlockState, RandomSource random, WorldGenLevel level) {
        int length = (int) Math.pow(2, layer - 1);
        BlockPos lengthSet;
        int x;
        int y;
        int z;
        if (layer > 0) {
            for (int j = 0; j < 4; j++) {
                branchPos.clear();
                lengthSet = trunkPos.offset((int) (length * Mth.cos(j * Mth.PI / 2)), random.nextInt(2), (int) (length * Mth.sin(j * Mth.PI / 2)));
                x = trunkPos.getX() - lengthSet.getX();
                y = branchLength + layer + random.nextInt(2);
                z = trunkPos.getZ() - lengthSet.getZ();
                set = (level.getBlockState(lengthSet).isAir() || level.getBlockState(lengthSet).is(branchBlockState.getBlock()));
                branchPos.add(lengthSet);
                if (set) {
                    for (int xCheck = x; xCheck != 0; xCheck -= (x / Math.abs(x))) {
                        if (x != xCheck) {
                            set = (level.getBlockState(lengthSet.offset(xCheck, 0, 0)).isAir() || level.getBlockState(lengthSet.offset(xCheck, 0, 0)).is(branchBlockState.getBlock())) && set;
                            branchPos.add(lengthSet.offset(xCheck, 0, 0));
                        }
                    }
                    for (int yCheck = y; yCheck != 0; yCheck -= (y / Math.abs(y))) {
                        set = (level.getBlockState(lengthSet.offset(0, yCheck, 0)).isAir() || level.getBlockState(lengthSet.offset(0, yCheck, 0)).is(branchBlockState.getBlock())) && set;
                        branchPos.add(lengthSet.offset(0, yCheck, 0));
                    }
                    for (int zCheck = z; zCheck != 0; zCheck -= (z / Math.abs(z))) {
                        if (z != zCheck) {
                            set = (level.getBlockState(lengthSet.offset(0, 0, zCheck)).isAir() || level.getBlockState(lengthSet.offset(0, 0, zCheck)).is(branchBlockState.getBlock())) && set;
                            branchPos.add(lengthSet.offset(0, 0, zCheck));
                        }
                    }
                    if (set) {
                        for (int k = 0; k < branchPos.size(); k++) {
                            level.setBlock(branchPos.get(k), branchBlockState, 3);
                        }
                        branch(layer - 1, branchLength, set, branchPos, lengthSet, branchBlockState, random, level);
                    }
                }
            }
        }
    }
}
