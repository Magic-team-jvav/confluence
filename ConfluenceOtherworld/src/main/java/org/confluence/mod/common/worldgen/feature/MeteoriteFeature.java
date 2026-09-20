package org.confluence.mod.common.worldgen.feature;

import PortLib.extensions.com.mojang.serialization.Codec.PortCodecExtension;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import org.confluence.lib.util.LibCodecUtils;
import org.confluence.mod.common.init.block.OreBlocks;
import org.mesdag.particlestorm.data.curve.SplineCurve;

public class MeteoriteFeature extends Feature<MeteoriteFeature.Config> {
    private static final int BEDROCK = 5;
    private static final SplineCurve.CatMullRom CURVE = new SplineCurve.CatMullRom(0, 0, 0.4F, 1, 0.4F, 0, 0);

    public MeteoriteFeature(Codec<Config> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<Config> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource random = context.random();
        Config config = context.config();
        int radius = config.radius;
        float sparse = config.sparse;
        float fire = config.fire;
        float dense = 1.0F - sparse;
        int length = radius + radius + 1;
        int max = level.getMaxBuildHeight() - length;
        int min = level.getMinBuildHeight() + length + BEDROCK;
        BlockPos.MutableBlockPos chunkCenter = new ChunkPos(origin).getBlockAt(7, max, 7).mutable();
        while (chunkCenter.getY() > min && level.getBlockState(chunkCenter).canBeReplaced()) {
            chunkCenter.move(0, -1, 0);
        }
        BlockState air = Blocks.AIR.defaultBlockState();
        BlockState meteorite = OreBlocks.METEORITE_ORE.get().defaultBlockState();
        LongArrayList meteoritePositions = new LongArrayList();
        float c = length - 3.0F;
        float invRadius = 1.0F / radius;
        float outer = radius / 2.0F + 0.5F;
        for (int x = -radius; x < radius; x++) {
            for (int y = -radius; y < radius; y++) {
                for (int z = -radius; z < radius; z++) {
                    float dist = Mth.sqrt(x * x + y * y + z * z);
                    if (dist > radius) continue;
                    BlockPos offset = chunkCenter.offset(x, y, z);
                    if (level.getBlockState(offset).canBeReplaced()) continue;
                    float point = CURVE.getPoint((1.0F + dist * invRadius * c) / (c + 2.0F));
                    float v = random.nextFloat();
                    if (point < sparse) {
                        if (dist > outer) {
                            if (v > point + sparse) {
                                setBlock(level, offset, meteorite, random, fire, meteoritePositions);
                            }
                        } else {
                            setBlock(level, offset, v + sparse > point ? air : meteorite, random, fire, meteoritePositions);
                        }
                    } else {
                        setBlock(level, offset, v + dense < point ? air : meteorite, random, fire, meteoritePositions);
                    }
                }
            }
        }
        placeLava(level, random, meteoritePositions, config.lavaCount);
        return true;
    }

    private static void setBlock(WorldGenLevel level, BlockPos pos, BlockState state, RandomSource random, float fire, LongArrayList meteoritePositions) {
        boolean b = state.is(OreBlocks.METEORITE_ORE.get());
        level.setBlock(pos, state, Block.UPDATE_ALL);
        if (b) {
            meteoritePositions.add(pos.asLong());
            if (level.getBlockState(pos.above()).canBeReplaced() && random.nextFloat() < fire) {
                level.setBlock(pos.above(), Blocks.FIRE.defaultBlockState(), Block.UPDATE_ALL);
            }
        }
    }

    /// 按**个数**放熔岩：从已生成的陨石方块里随机挑 `count` 个。
    ///
    /// 之前是逐格按概率（`lava = 0.1`）掷骰：半径 7 时总共才几百格、撒出几十格还看得过去，
    /// 但半径放大到 23 之后陨石方块涨到万级，同样的概率会撒出上千格熔岩。
    /// 用部分洗牌（Fisher-Yates）保证挑到的位置不重样。
    private static void placeLava(WorldGenLevel level, RandomSource random, LongArrayList meteoritePositions, int count) {
        int total = meteoritePositions.size();
        int lavaCount = Math.min(count, total);
        for (int i = 0; i < lavaCount; i++) {
            int pick = i + random.nextInt(total - i);
            long swapped = meteoritePositions.set(i, meteoritePositions.getLong(pick));
            meteoritePositions.set(pick, swapped);
            level.setBlock(BlockPos.of(meteoritePositions.getLong(i)), Blocks.LAVA.defaultBlockState(), Block.UPDATE_ALL);
        }
    }

    public record Config(
            int radius,
            float sparse,
            int lavaCount,
            float fire
    ) implements FeatureConfiguration {
        /// 默认半径。**23 是有讲究的**：坑心被对齐到区块内 local (7, 7)，
        /// 所以 `x ∈ [-radius, radius)` 对应的全局范围是 `[区内-16, 区内+29]`，
        /// 正好跨 3 个区块（cx-1 / cx / cx+1）；取 24 就会多伸进第 4 个区块。
        public static final int DEFAULT_RADIUS = 23;

        /// 陨石坑里的熔岩块数。按**个数**而不是概率，免得坑变大后熔岩跟着涨。
        public static final int DEFAULT_LAVA_COUNT = 9;

        public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                PortCodecExtension.lenientOptionalFieldOf(ExtraCodecs.intRange(1, 32), "radius", DEFAULT_RADIUS).forGetter(Config::radius),
                PortCodecExtension.lenientOptionalFieldOf(LibCodecUtils.FLOAT_0_1, "sparse", 0.4F).forGetter(Config::sparse),
                PortCodecExtension.lenientOptionalFieldOf(ExtraCodecs.intRange(0, 4096), "lavaCount", DEFAULT_LAVA_COUNT).forGetter(Config::lavaCount),
                PortCodecExtension.lenientOptionalFieldOf(LibCodecUtils.FLOAT_0_1, "fire", 0.15F).forGetter(Config::fire)
        ).apply(instance, Config::new));
    }
}
