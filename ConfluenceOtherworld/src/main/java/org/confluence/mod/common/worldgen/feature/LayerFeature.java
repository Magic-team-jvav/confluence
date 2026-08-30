package org.confluence.mod.common.worldgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import org.joml.Vector2i;

import java.util.HashMap;
import java.util.Map;

// 該地物僅供建築建造等手動放置場景使用，不自然生成
public class LayerFeature extends Feature<LayerFeature.Config> {
    private final int[][] neighbors = {
            {0, 0},
            {-1, 0}, {1, 0}, {0, -1}, {0, 1},
            {-1, -1}, {1, -1}, {-1, 1}, {1, 1}
    };
    private final float[] weights = {4.0F, 1.0F, 1.0F, 1.0F, 1.0F, 0.5F, 0.5F, 0.5F, 0.5F};

    public LayerFeature(Codec<Config> pCodec) {
        super(pCodec);
    }

    @Override
    public boolean place(FeaturePlaceContext<Config> pContext) {
        final RandomSource random = pContext.random();
        final Config config = pContext.config();
        final WorldGenLevel level = pContext.level();
        final BlockPos basePos = pContext.origin();
        final int radius = config.radius + random.nextInt(config.radiusMore + 1);
        final int radius_2 = radius * radius;
        BlockState layerBlock = config.layer.getState(random, basePos);
        Map<Vector2i, Integer> layerHeight = new HashMap<>();

        for (int x = -radius; x <= radius; x++) {
            int x_2 = x * x;
            for (int z = -radius; z <= radius; z++) {
                int z_2 = z * z;
                if (x_2 + z_2 < radius_2) {
                    BlockPos checkPos = basePos.offset(x, 0, z);
                    boolean nearCactus = false;
                    int[][] offsets = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
                    for (int[] offset : offsets) {
                        BlockPos neighborTopPos = level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, checkPos.offset(offset[0], 0, offset[1]));
                        if (level.getBlockState(neighborTopPos).is(Blocks.CACTUS)) {
                            nearCactus = true;
                            break;
                        }
                    }

                    if (nearCactus) continue;
                    int y = getY(10, checkPos, level);
                    if (y != -1) layerHeight.put(new Vector2i(x + basePos.getX(), z + basePos.getZ()), y * 8);
                }
            }
        }

        Map<Vector2i, Integer> currentHeight = new HashMap<>(layerHeight);
        Map<Vector2i, Integer> nextHeight = new HashMap<>();

        int smoothIterations = 5;

        for (int iter = 0; iter < smoothIterations; iter++) {
            nextHeight.clear();

            for (Map.Entry<Vector2i, Integer> entry : currentHeight.entrySet()) {
                Vector2i pos = entry.getKey();
                int currentY = entry.getValue();

                int baseY = layerHeight.get(pos);

                float weightedSum = 0;
                float totalWeight = 0;

                for (int i = 0; i < neighbors.length; i++) {
                    int nx = pos.x + neighbors[i][0];
                    int nz = pos.y + neighbors[i][1];
                    Vector2i neighborPos = new Vector2i(nx, nz);

                    if (currentHeight.containsKey(neighborPos)) {
                        float weight = weights[i];
                        weightedSum += currentHeight.get(neighborPos) * weight;
                        totalWeight += weight;
                    }
                }

                float averagedY = (totalWeight > 0) ? (weightedSum / totalWeight) : currentY;
                int finalY = Math.round(averagedY);

                finalY = Math.max(baseY, Math.min(baseY + 7, finalY));

                nextHeight.put(pos, finalY);
            }

            Map<Vector2i, Integer> temp = currentHeight;
            currentHeight = nextHeight;
            nextHeight = temp;
        }

        for (Map.Entry<Vector2i, Integer> entry : currentHeight.entrySet()) {
            int y = entry.getValue() / 8;
            int layer = entry.getValue() % 8;
            Vector2i xz = entry.getKey();
            if (layer != 0) level.setBlock(new BlockPos(xz.x, y + 1, xz.y), layerBlock.trySetValue(BlockStateProperties.LAYERS, layer), 3);
        }

        return true;
    }

    public record Config(BlockStateProvider layer,
                         int radius,
                         int radiusMore) implements FeatureConfiguration {
        public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BlockStateProvider.CODEC.fieldOf("layer_block").forGetter(Config::layer),
                Codec.INT.fieldOf("radius").forGetter(Config::radius),
                Codec.INT.fieldOf("radius_more").forGetter(Config::radiusMore)
        ).apply(instance, Config::new));
    }

    @SuppressWarnings("SameParameterValue")
    private int getY(int maxStep, BlockPos blockPos, WorldGenLevel level) {
        int face = 1;
        for (int i = 0; i < maxStep; i++) {
            BlockPos checkPos = blockPos.offset(0, face * i, 0);
            BlockState state = level.getBlockState(checkPos);
            BlockState stateUp = level.getBlockState(checkPos.offset(0, 1, 0));
            if (stateUp.isAir() && state.isSolidRender(level, checkPos)) return blockPos.getY() + face * i;
            if (stateUp.canBeReplaced() && state.canBeReplaced()) face = -1;
        }
        return -1;
    }
}
