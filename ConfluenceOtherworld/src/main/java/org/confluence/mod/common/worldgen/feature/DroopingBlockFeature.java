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
import org.confluence.lib.util.FeatureUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DroopingBlockFeature extends Feature<DroopingBlockFeature.Config> {
    public DroopingBlockFeature(Codec<Config> pCodec) {
        super(pCodec);
    }

    private static void setLeaves(BoundingBox box, BlockState leaves, boolean up, RandomSource random, WorldGenLevel level) {
        FeatureUtils.leaves(box, leaves, up, random, level, Blocks.AIR.defaultBlockState(), false);
    }

    private static void setLeaves(BoundingBox box, BlockState leaves, boolean up, RandomSource random, WorldGenLevel level, BlockState droopingLeaves) {
        FeatureUtils.leaves(box, leaves, up, random, level, droopingLeaves, true);
    }

    @Override
    public boolean place(FeaturePlaceContext<Config> pContext) {
        RandomSource random = pContext.random();
        Config config = pContext.config();
        WorldGenLevel level = pContext.level();
        BlockPos baseBlockPos = pContext.origin();
        BlockState droopingBlockBlockState = config.drooping_block.getState(random, baseBlockPos);
        int height = config.height + random.nextInt(config.height_more + 1);
        int endY = -63;
        boolean toGround = config.to_ground;

        boolean placed = (level.getBlockState(baseBlockPos).canBeReplaced() && (baseBlockPos.getY() > -63));

        if (placed) {
            if (toGround) {
                boolean down;
                int yCheck = 0;
                 do {
                    if ((baseBlockPos.getY() + yCheck) <= -63) break;
                    endY = baseBlockPos.getY() + yCheck;
                    yCheck--;
                    down = level.getBlockState(baseBlockPos.offset(0, yCheck, 0)).canBeReplaced();
                } while (down);
            } else {
                for (int yCheck = 0; yCheck < height; yCheck++) {
                    if ((baseBlockPos.getY() - yCheck) <= -63) break;
                    if (!level.getBlockState(baseBlockPos.offset(0, -yCheck, 0)).canBeReplaced()) break;
                    endY = baseBlockPos.getY() - yCheck;
                }
            }
        }

        if (placed) {
            for (int i = endY; i <= baseBlockPos.getY(); i++) {
                level.setBlock(baseBlockPos.offset(0, -baseBlockPos.getY() + i, 0), droopingBlockBlockState, 3);
            }
            return true;
        }
        return false;
    }

    public record Config(
            BlockStateProvider drooping_block,
            boolean to_ground,
            int height,
            int height_more
    ) implements FeatureConfiguration {
        public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BlockStateProvider.CODEC.fieldOf("drooping_block").forGetter(Config::drooping_block),
                Codec.BOOL.fieldOf("to_ground").forGetter(DroopingBlockFeature.Config::to_ground),
                Codec.INT.fieldOf("height").forGetter(DroopingBlockFeature.Config::height),
                Codec.INT.fieldOf("height_more").forGetter(DroopingBlockFeature.Config::height_more)
        ).apply(instance, Config::new));
    }
}
