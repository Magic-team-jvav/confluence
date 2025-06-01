package org.confluence.mod.common.worldgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.confluence.lib.util.FeatureUtils;

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
    public boolean place(FeaturePlaceContext<Config> context) {
        RandomSource random = context.random();
        Config config = context.config();
        WorldGenLevel level = context.level();
        BlockPos baseBlockPos = context.origin();
        BlockState droopingBlockBlockState = config.droopingBlock.getState(random, baseBlockPos);
        int height = config.height + random.nextInt(config.extraHeight + 1);
        int minY = context.chunkGenerator().getMinY();
        int endY = -minY;
        boolean toGround = config.toGround;
        boolean placed = level.getBlockState(baseBlockPos).canBeReplaced() && baseBlockPos.getY() > -63;

        if (placed) {
            if (toGround) {
                boolean down;
                int yCheck = 0;
                do {
                    if ((baseBlockPos.getY() + yCheck) <= -minY) break;
                    endY = baseBlockPos.getY() + yCheck;
                    yCheck--;
                    down = level.getBlockState(baseBlockPos.offset(0, yCheck, 0)).canBeReplaced();
                } while (down);
            } else {
                for (int yCheck = 0; yCheck < height; yCheck++) {
                    if ((baseBlockPos.getY() - yCheck) <= -minY) break;
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

    public record Config(BlockStateProvider droopingBlock, boolean toGround, int height, int extraHeight) implements FeatureConfiguration {
        public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BlockStateProvider.CODEC.fieldOf("drooping_block").forGetter(Config::droopingBlock),
                Codec.BOOL.fieldOf("to_ground").forGetter(DroopingBlockFeature.Config::toGround),
                Codec.INT.fieldOf("height").forGetter(DroopingBlockFeature.Config::height),
                Codec.INT.fieldOf("extra_height").forGetter(DroopingBlockFeature.Config::extraHeight)
        ).apply(instance, Config::new));
    }
}
