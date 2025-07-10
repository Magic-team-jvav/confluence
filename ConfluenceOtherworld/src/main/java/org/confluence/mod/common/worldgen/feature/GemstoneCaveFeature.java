package org.confluence.mod.common.worldgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.OreFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import org.confluence.lib.util.FeatureUtils;
import org.confluence.lib.util.StructureUtils;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.common.init.block.OreBlocks;

import java.util.function.Consumer;

public class GemstoneCaveFeature extends Feature<GemstoneCaveFeature.Config> {
    public GemstoneCaveFeature(Codec<Config> pCodec) {
        super(pCodec);
    }

    private static final BlockState[] GEMSTONES = new BlockState[]{
            OreBlocks.RUBY_ORE.get().defaultBlockState(),
            OreBlocks.TOPAZ_ORE.get().defaultBlockState(),
            OreBlocks.SAPPHIRE_ORE.get().defaultBlockState(),
            OreBlocks.JADE_ORE.get().defaultBlockState(),
            OreBlocks.AMETHYST_ORE.get().defaultBlockState(),
            Blocks.DIAMOND_ORE.defaultBlockState()
    };

    @Override
    public boolean place(FeaturePlaceContext<Config> pContext) {
        Config config = pContext.config();
        RandomSource random = pContext.random();
        WorldGenLevel level = pContext.level();
        BlockPos blockPos = pContext.origin();
        float radius = config.radius + 0.5F;
        BlockState stone = Blocks.STONE.defaultBlockState();
        BlockState air = Blocks.AIR.defaultBlockState();
        int index0 = random.nextInt(6);
        int index1 = index0;
        while (index1 == index0) {
            index1 = random.nextInt(6);
        }
        float rotation = (2 * Mth.PI) * random.nextFloat();
        int x = Float.floatToIntBits(Mth.sin(rotation) * radius);
        int z = Float.floatToIntBits(Mth.cos(rotation) * radius);

        FeatureUtils.ball(radius + 2, blockPos, GEMSTONES[index0], true, level);
        FeatureUtils.ball(radius + 2, blockPos, GEMSTONES[index1], true, level, 0.5F, random);
        FeatureUtils.ball(radius + 2, blockPos, stone, true, level, 0.7F, random);
        FeatureUtils.ball(radius, blockPos, air, true, level);
        FeatureUtils.ellipsoid(radius - 1, radius + 1, radius - 1, blockPos.offset(x, 0, z), air, true, level);

        return true;
    }

    public record Config(int radius) implements FeatureConfiguration {
        public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.fieldOf("radius").forGetter(GemstoneCaveFeature.Config::radius)
        ).apply(instance, Config::new));
    }
}
