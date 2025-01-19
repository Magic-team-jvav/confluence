package org.confluence.mod.common.worldgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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
import org.confluence.mod.common.init.block.OreBlocks;
import org.mesdag.particlestorm.data.curve.SplineCurve;

public class MeteoriteFeature extends Feature<MeteoriteFeature.Config> {
    private static final int BEDROCK = 5;
    private static final SplineCurve.CatMullRom CURVE = new SplineCurve.CatMullRom(0, 0, 0.4F, 1, 0.4F, 0, 0);

    public MeteoriteFeature(Codec<Config> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<Config> context) { // todo 调整大小，外观
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource random = context.random();
        int radius = context.config().radius;
        float sparse = context.config().sparse;
        float dense = 1.0F - sparse;
        int length = radius + radius + 1;
        int max = level.getMaxBuildHeight() - length;
        int min = level.getMinBuildHeight() + length + BEDROCK;
        BlockPos.MutableBlockPos chunkCenter = new ChunkPos(origin).getBlockAt(7, max, 7).mutable();
        while (chunkCenter.getY() > min && level.getBlockState(chunkCenter).isAir()) {
            chunkCenter.move(0, -1, 0);
        }
        BlockState air = Blocks.AIR.defaultBlockState();
        BlockState meteorite = OreBlocks.METEORITE_ORE.get().defaultBlockState();
        float c = length - 3.0F;
        float invRadius = 1.0F / radius;
        float outer = radius / 2.0F + 0.5F;
        for (int x = -radius; x < radius; x++) {
            for (int y = -radius; y < radius; y++) {
                for (int z = -radius; z < radius; z++) {
                    float dist = Mth.sqrt(x * x + y * y + z * z);
                    if (dist > radius) continue;
                    BlockPos offset = chunkCenter.offset(x, y, z);
                    if (!level.getBlockState(offset).isCollisionShapeFullBlock(level, offset)) continue;
                    float point = CURVE.getPoint((1.0F + dist * invRadius * c) / (c + 2.0F));
                    float v = random.nextFloat();
                    if (point < sparse) {
                        if (dist > outer) {
                            if (v > point + sparse) {
                                level.setBlock(offset, meteorite, Block.UPDATE_ALL);
                            }
                        } else {
                            level.setBlock(offset, v + sparse > point ? air : meteorite, Block.UPDATE_ALL);
                        }
                    } else {
                        level.setBlock(offset, v + dense < point ? air : meteorite, Block.UPDATE_ALL);
                    }
                }
            }
        }
        return true;
    }

    public record Config(int radius, float sparse) implements FeatureConfiguration {
        public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ExtraCodecs.intRange(1, 7).lenientOptionalFieldOf("radius", 7).forGetter(Config::radius),
                Codec.floatRange(0.0F, 1.0F).lenientOptionalFieldOf("sparse", 0.4F).forGetter(Config::sparse)
        ).apply(instance, Config::new));
    }
}
