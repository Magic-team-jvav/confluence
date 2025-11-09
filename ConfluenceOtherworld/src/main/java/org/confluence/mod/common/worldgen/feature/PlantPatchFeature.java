package org.confluence.mod.common.worldgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

import java.util.*;

public class PlantPatchFeature extends Feature<PlantPatchFeature.Config> {

    public PlantPatchFeature(Codec<Config> pCodec) {
        super(pCodec);
    }

    @Override
    public boolean place(FeaturePlaceContext<Config> context) {
        Config config = context.config();
        RandomSource random = context.random();
        BlockPos origin = context.origin();
        WorldGenLevel level = context.level();

        int successCount = 0;
        int radius = config.radius();
        int tries = config.tries();
        BlockStateProvider plantProvider = config.plant();
        int protectRadius = config.protectRadius();
        Block protectBlock = config.protectBlock();

        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        Set<BlockPos> protectCenters = new HashSet<>();

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                for (int y = -radius; y <= radius; y++) {
                    mutablePos.setWithOffset(origin, x, y, z);
                    if (level.getBlockState(mutablePos).is(protectBlock)) {
                        protectCenters.add(mutablePos.immutable());
                    }
                }
            }
        }

        Set<ResourceKey<Biome>> allowedBiomeKeys = null;
        if (!config.biomes().isEmpty()) {
            allowedBiomeKeys = new HashSet<>();
            for (Holder<Biome> biomeHolder : config.biomes()) {
                biomeHolder.unwrapKey().ifPresent(allowedBiomeKeys::add);
            }
        }

        for (int attempt = 0; attempt < tries; attempt++) {
            mutablePos.setWithOffset(origin,
                random.nextInt(radius * 2 + 1) - radius,
                random.nextInt(radius * 2 + 1) - radius,
                random.nextInt(radius * 2 + 1) - radius
            );
            if (allowedBiomeKeys != null) {
                Holder<Biome> currentBiome = level.getBiome(mutablePos);
                Optional<ResourceKey<Biome>> currentKey = currentBiome.unwrapKey();
                if (currentKey.isEmpty() || !allowedBiomeKeys.contains(currentKey.get())) {
                    continue;
                }
            }
            if (!level.isEmptyBlock(mutablePos) || !level.getBlockState(mutablePos.below()).isSolid()) continue;
            boolean inProtectedArea = false;
            Iterator<BlockPos> iterator = protectCenters.iterator();
            while (iterator.hasNext()) {
                BlockPos protectPos = iterator.next();
                if (!level.getBlockState(protectPos).is(protectBlock)) {
                    iterator.remove();
                    continue;
                }
                int dx = Math.abs(mutablePos.getX() - protectPos.getX());
                int dz = Math.abs(mutablePos.getZ() - protectPos.getZ());
                int dy = Math.abs(mutablePos.getY() - protectPos.getY());
                if (dx <= protectRadius && dz <= protectRadius && dy <= protectRadius) {
                    inProtectedArea = true;
                    break;
                }
            }
            if (inProtectedArea) continue;
            BlockState plantToPlace = plantProvider.getState(random, mutablePos);
            if (!plantToPlace.canSurvive(level, mutablePos)) continue;
            if (plantToPlace.is(protectBlock)) {
                boolean canPlace = true;
                for (int x = -protectRadius; x <= protectRadius && canPlace; x++) {
                    for (int z = -protectRadius; z <= protectRadius && canPlace; z++) {
                        if (x == 0 && z == 0) continue;
                        BlockPos nearbyPos = mutablePos.offset(x, 0, z);
                        if (!level.isEmptyBlock(nearbyPos)) {
                            canPlace = false;
                        }
                    }
                }
                if (!canPlace) continue;
                protectCenters.add(mutablePos.immutable());
            }

            level.setBlock(mutablePos, plantToPlace, 2);
            successCount++;
        }
        return successCount > 0;
    }

    public record Config(
        BlockStateProvider plant,           // 要生成的植物
        Block protectBlock,                 // 需要保护区域的植物方块
        int protectRadius,                  // 保护半径
        int radius,                         // 生成半径
        int tries,                          // 尝试次数
        List<Holder<Biome>> biomes          // 可生成的生物群系（空列表表示所有）
    ) implements FeatureConfiguration {
        public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BlockStateProvider.CODEC.fieldOf("plant").forGetter(Config::plant),
            BuiltInRegistries.BLOCK.byNameCodec().fieldOf("protect_block").forGetter(Config::protectBlock),
            Codec.INT.fieldOf("protect_radius").orElse(1).forGetter(Config::protectRadius),
            Codec.INT.fieldOf("radius").orElse(7).forGetter(Config::radius),
            Codec.INT.fieldOf("tries").orElse(64).forGetter(Config::tries),
            Biome.CODEC.listOf().fieldOf("biomes").orElse(List.of()).forGetter(Config::biomes)
        ).apply(instance, Config::new));
    }
}