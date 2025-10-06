package org.confluence.mod.common.worldgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import org.confluence.lib.util.FeatureUtils;
import org.confluence.mod.common.block.functional.network.INetworkEntity;
import org.confluence.mod.common.init.ModFeatures;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.jetbrains.annotations.NotNull;

/**
 * 带有引爆器的地物
 */
public class WithDetonatorFeature extends Feature<WithDetonatorFeature.Config> {
    public WithDetonatorFeature(Codec<Config> codec) {
        super(codec);
    }

    @Override
    public boolean place(@NotNull FeaturePlaceContext<Config> context) {
        Config config = context.config();
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        BlockPos origin = context.origin();
        boolean placedSubFeature = placeSubFeature(context, level, config, random, origin);

        int distance = config.distanceFromOrigin;
        int i = (random.nextInt(distance) + 1) * (random.nextBoolean() ? -1 : 1);
        int j = random.nextInt(distance) + 1;
        int k = (random.nextInt(distance) + 1) * (random.nextBoolean() ? -1 : 1);

        int offsetY = j > 0 ? 2 : -2;
        BlockPos center = new BlockPos(origin.getX() + i / 2, origin.getY() + j / 2 + offsetY, origin.getZ() + k / 2);
        int bound = distance / 2;
        carveRoom(bound, level, center);

        BlockPos detonatorPos = placeDetonator(random, bound, i, k, center, level);
        boolean b = level.setBlock(detonatorPos, FunctionalBlocks.DETONATOR.get().defaultBlockState(), 3);
        boolean b1 = level.setBlock(origin, FunctionalBlocks.INSTANTANEOUS_EXPLOSION_TNT.get().defaultBlockState(), 3);

        if (b && b1) {
            INetworkEntity tnt = ModFeatures.getNetworkEntity(level, origin);
            INetworkEntity detonator = ModFeatures.getNetworkEntity(level, detonatorPos);
            if (tnt != null && detonator != null) {
                tnt.connectTo(0x0000FF, detonatorPos, detonator);
            }
        }
        return placedSubFeature;
    }

    private static BlockPos.@NotNull MutableBlockPos placeDetonator(RandomSource random, int bound, int i, int k, BlockPos center, WorldGenLevel level) {
        int dx = (random.nextInt(bound) + bound - 2) * Mth.sign(i);
        int dz = (random.nextInt(bound) + bound - 2) * Mth.sign(k);
        BlockPos.MutableBlockPos mutable = center.mutable().move(dx, 0, dz);
        for (int dy = 0; dy < bound && FeatureUtils.isPosAir(level, mutable); dy++) {
            mutable.move(0, -1, 0);
        }
        return mutable;
    }

    private static boolean placeSubFeature(@NotNull FeaturePlaceContext<Config> context, WorldGenLevel level, Config config, RandomSource random, BlockPos origin) {
        return level.registryAccess().registryOrThrow(Registries.CONFIGURED_FEATURE)
                .getHolder(config.subFeature).orElseThrow().value()
                .place(level, context.chunkGenerator(), random, origin);
    }

    private static void carveRoom(int bound, WorldGenLevel level, BlockPos center) {
        BlockState air = Blocks.AIR.defaultBlockState();
        int halfDist = bound + 2;
        int halfDistSqr = halfDist * halfDist;
        for (int x = -halfDist; x < halfDist; x++) {
            for (int y = -halfDist + 2; y < halfDist - 1; y++) {
                for (int z = -halfDist; z < halfDist; z++) {
                    if (x * x + y * y + z * z <= halfDistSqr) {
                        FeatureUtils.safeSetBlock(level, center.offset(x, y, z), air, ModFeatures.IS_BASE_STONE);
                    }
                }
            }
        }
    }

    public record Config(ResourceLocation subFeature, int distanceFromOrigin) implements FeatureConfiguration {
        public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("sub_feature").forGetter(Config::subFeature),
                ExtraCodecs.intRange(1, 15).lenientOptionalFieldOf("distance_from_origin", 7).forGetter(Config::distanceFromOrigin)
        ).apply(instance, Config::new));
    }
}
