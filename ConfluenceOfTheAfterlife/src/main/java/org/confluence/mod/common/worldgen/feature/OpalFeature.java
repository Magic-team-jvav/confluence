package org.confluence.mod.common.worldgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.entity.BrushableBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.OreFeature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import org.confluence.mod.common.init.ModFeatures;
import org.confluence.mod.common.init.ModLootTables;
import org.jetbrains.annotations.NotNull;

import java.util.BitSet;

public class OpalFeature extends OreFeature {
    public OpalFeature(Codec<OreConfiguration> codec) {
        super(codec);
    }

    @Override
    protected boolean doPlace(@NotNull WorldGenLevel level, @NotNull RandomSource random, OreConfiguration config, double minX, double maxX, double minZ, double maxZ, double minY, double maxY, int x, int y, int z, int width, int height) {
        int i = 0;
        BitSet bitset = new BitSet(width * height * width);
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
        int j = config.size;
        double[] adouble = new double[j * 4];

        for (int k = 0; k < j; k++) {
            float f = (float) k / (float) j;
            double d0 = Mth.lerp(f, minX, maxX);
            double d1 = Mth.lerp(f, minY, maxY);
            double d2 = Mth.lerp(f, minZ, maxZ);
            double d3 = random.nextDouble() * (double) j / 16.0;
            double d4 = ((double) (Mth.sin((float) Math.PI * f) + 1.0F) * d3 + 1.0) / 2.0;
            adouble[k * 4] = d0;
            adouble[k * 4 + 1] = d1;
            adouble[k * 4 + 2] = d2;
            adouble[k * 4 + 3] = d4;
        }

        for (int l3 = 0; l3 < j - 1; l3++) {
            if (!(adouble[l3 * 4 + 3] <= 0.0)) {
                for (int i4 = l3 + 1; i4 < j; i4++) {
                    if (!(adouble[i4 * 4 + 3] <= 0.0)) {
                        double d8 = adouble[l3 * 4] - adouble[i4 * 4];
                        double d10 = adouble[l3 * 4 + 1] - adouble[i4 * 4 + 1];
                        double d12 = adouble[l3 * 4 + 2] - adouble[i4 * 4 + 2];
                        double d14 = adouble[l3 * 4 + 3] - adouble[i4 * 4 + 3];
                        if (d14 * d14 > d8 * d8 + d10 * d10 + d12 * d12) {
                            if (d14 > 0.0) {
                                adouble[i4 * 4 + 3] = -1.0;
                            } else {
                                adouble[l3 * 4 + 3] = -1.0;
                            }
                        }
                    }
                }
            }
        }

        for (int j4 = 0; j4 < j; j4++) {
            double d9 = adouble[j4 * 4 + 3];
            if (!(d9 < 0.0)) {
                double d11 = adouble[j4 * 4];
                double d13 = adouble[j4 * 4 + 1];
                double d15 = adouble[j4 * 4 + 2];
                int k4 = Math.max(Mth.floor(d11 - d9), x);
                int l = Math.max(Mth.floor(d13 - d9), y);
                int i1 = Math.max(Mth.floor(d15 - d9), z);
                int j1 = Math.max(Mth.floor(d11 + d9), k4);
                int k1 = Math.max(Mth.floor(d13 + d9), l);
                int l1 = Math.max(Mth.floor(d15 + d9), i1);

                for (int i2 = k4; i2 <= j1; i2++) {
                    double d5 = ((double) i2 + 0.5 - d11) / d9;
                    if (d5 * d5 < 1.0) {
                        for (int j2 = l; j2 <= k1; j2++) {
                            double d6 = ((double) j2 + 0.5 - d13) / d9;
                            if (d5 * d5 + d6 * d6 < 1.0) {
                                for (int k2 = i1; k2 <= l1; k2++) {
                                    double d7 = ((double) k2 + 0.5 - d15) / d9;
                                    if (d5 * d5 + d6 * d6 + d7 * d7 < 1.0 && !level.isOutsideBuildHeight(j2)) {
                                        int l2 = i2 - x + (j2 - y) * width + (k2 - z) * width * height;
                                        if (!bitset.get(l2)) {
                                            bitset.set(l2);
                                            blockpos$mutableblockpos.set(i2, j2, k2);
                                            BlockState blockstate = level.getBlockState(blockpos$mutableblockpos);
                                            for (OreConfiguration.TargetBlockState oreconfiguration$targetblockstate : config.targetStates) {
                                                if (canPlaceOre(
                                                        blockstate,
                                                        level::getBlockState,
                                                        random,
                                                        config,
                                                        oreconfiguration$targetblockstate,
                                                        blockpos$mutableblockpos
                                                )) {
                                                    level.setBlock(blockpos$mutableblockpos, oreconfiguration$targetblockstate.state, 2);
                                                    if (ModFeatures.getBlockEntity(level, blockpos$mutableblockpos) instanceof BrushableBlockEntity brushableBlockEntity) {
                                                        brushableBlockEntity.setLootTable(ModLootTables.OPAL_BLOCK, blockpos$mutableblockpos.asLong());
                                                    }
                                                    i++;
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return i > 0;
    }
}
