package org.confluence.mod.common.worldgen.feature;

import PortLib.extensions.com.mojang.serialization.Codec.PortCodecExtension;
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
        float lava = config.lava;
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
                                setBlock(level, offset, meteorite, random, lava, fire);
                            }
                        } else {
                            setBlock(level, offset, v + sparse > point ? air : meteorite, random, lava, fire);
                        }
                    } else {
                        setBlock(level, offset, v + dense < point ? air : meteorite, random, lava, fire);
                    }
                }
            }
        }
        return true;
    }

    private static void setBlock(WorldGenLevel level, BlockPos pos, BlockState state, RandomSource random, float lava, float fire) {
        boolean b = state.is(OreBlocks.METEORITE_ORE.get());
        level.setBlock(pos, b && random.nextFloat() < lava ? Blocks.LAVA.defaultBlockState() : state, Block.UPDATE_ALL);
        if (b && level.getBlockState(pos.above()).canBeReplaced() && random.nextFloat() < fire) {
            level.setBlock(pos.above(), Blocks.FIRE.defaultBlockState(), Block.UPDATE_ALL);
        }
    }

    public record Config(
            int radius, float sparse, float lava,
            float fire
    ) implements FeatureConfiguration {
        public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                PortCodecExtension.lenientOptionalFieldOf(ExtraCodecs.intRange(1, 7), "radius", 7).forGetter(Config::radius),
                PortCodecExtension.lenientOptionalFieldOf(LibCodecUtils.FLOAT_0_1, "sparse", 0.4F).forGetter(Config::sparse),
                PortCodecExtension.lenientOptionalFieldOf(LibCodecUtils.FLOAT_0_1, "lava", 0.1F).forGetter(Config::lava),
                PortCodecExtension.lenientOptionalFieldOf(LibCodecUtils.FLOAT_0_1, "fire", 0.15F).forGetter(Config::fire)
        ).apply(instance, Config::new));
    }
}
