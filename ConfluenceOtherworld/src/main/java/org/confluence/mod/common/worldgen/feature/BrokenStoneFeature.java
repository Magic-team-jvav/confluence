package org.confluence.mod.common.worldgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import org.confluence.lib.util.VectorUtils;
import org.confluence.mod.common.block.natural.BaseDroopingPlantsHeadBlock;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.List;

import static org.confluence.lib.util.FeatureUtils.updateLeavesOptimized;

public class BrokenStoneFeature extends Feature<BrokenStoneFeature.Config> {
    public BrokenStoneFeature(Codec<Config> pCodec) {
        super(pCodec);
    }

    @Override
    public boolean place(FeaturePlaceContext<Config> pContext) {
        RandomSource random = pContext.random();
        Config config = pContext.config();
        WorldGenLevel level = pContext.level();
        BlockPos basePos = pContext.origin();
        long seed = random.nextLong();
        WorldgenRandom worldgenRandom = new WorldgenRandom(new LegacyRandomSource(seed));
        BlockState air = Blocks.AIR.defaultBlockState();

        int height = config.height + random.nextInt(config.heightMore + 1);
        int radius = config.radius + random.nextInt(config.radiusMore + 1);
        List<Vector3d> posList = VectorUtils.ballPos(radius, basePos, 0.006F, worldgenRandom);
        List<BlockPos> movePos = VectorUtils.getBlocksInConvexHull(posList);
        List<BlockPos> placePos = new ArrayList<>();

        if (movePos.isEmpty()) return false;

        Object2IntOpenHashMap<Block> counts = new Object2IntOpenHashMap<>();
        counts.defaultReturnValue(0);

        Block maxBlock = null;
        int maxCount = 0;

        for (BlockPos p : movePos) {
            BlockState state = level.getBlockState(p);
            if (!state.isAir()) {
                Block block = state.getBlock();

                level.setBlock(p, air, 3);
                level.setBlock(p.offset(0, height, 0), state, 3);

                int newCount = counts.addTo(block, 1);

                if (newCount > maxCount) {
                    maxCount = newCount;
                    maxBlock = block;
                }
            }
        }

        if (maxBlock == null) return false;

        if (radius > 5) {
            radius -= 4;
            for (int i = 0; i < height - 10; i++) {
                if (random.nextFloat() < 0.05F) {
                    double smallRadius = radius * ((double) i / height);
                    int intSmallRadius = (int) smallRadius;
                    placePos.addAll(VectorUtils.getBlocksInConvexHull(VectorUtils.ballPos(smallRadius, basePos.offset(random.nextInt(-intSmallRadius, intSmallRadius + 1), i, random.nextInt(-intSmallRadius, intSmallRadius + 1)), 0.06F, worldgenRandom)));
                }
            }
        }

        BlockState placedBlock = maxBlock.defaultBlockState();
        placePos.forEach(pos -> level.setBlock(pos, placedBlock, 3));

        return true;
    }

    public record Config(int height,
                         int heightMore,
                         int radius,
                         int radiusMore
    ) implements FeatureConfiguration {
        public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.fieldOf("height").forGetter(Config::height),
                Codec.INT.fieldOf("height_more").forGetter(Config::heightMore),
                Codec.INT.fieldOf("radius").forGetter(Config::radius),
                Codec.INT.fieldOf("radius_more").forGetter(Config::radiusMore)
        ).apply(instance, Config::new));
    }
}
