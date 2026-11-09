package org.confluence.mod.mixed;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.confluence.mod.common.init.ModBiomes;
import org.confluence.mod.common.init.ModSecretSeeds;
import org.confluence.mod.common.worldgen.BannedBiomeMultiNoiseBiomeSource;
import org.confluence.mod.util.OverworldUtils;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface IInjectBiomeSource {
    @Nullable Pair<Holder<Biome>, Holder<Biome>> confluence$getBiomePair();

    default @Nullable Pair<Holder<Biome>, Holder<Biome>> confluence$createBiomePair() {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) return null;
        WorldOptions worldOptions = server.getWorldData().worldGenOptions();
        long flag = IWorldOptions.of(worldOptions).confluence$getSecretFlag();
        ResourceKey<Biome> from;
        ResourceKey<Biome> to;
        if (confluence$getBannedBiomeSource() != null) {
            return new Pair<>(null, null);
        } else if (ModSecretSeeds.DRUNK_WORLD.match(flag)) {
            IMinecraftServer.of(server).confluence$updateSecretFlag(IWorldOptions.DOUBLE_EVIL);
            return new Pair<>(null, null);
        } else if ((flag & IWorldOptions.DOUBLE_EVIL) == 0) {
            if (net.minecraft.util.RandomSource.create(worldOptions.seed()).nextBoolean()) {
                from = ModBiomes.THE_CORRUPTION;
                to = ModBiomes.THE_CRIMSON;
                IMinecraftServer.of(server).confluence$updateSecretFlag(IWorldOptions.THE_CRIMSON);
            } else {
                from = ModBiomes.THE_CRIMSON;
                to = ModBiomes.THE_CORRUPTION;
                IMinecraftServer.of(server).confluence$updateSecretFlag(IWorldOptions.THE_CORRUPTION);
            }
        } else {
            if ((flag & IWorldOptions.THE_CORRUPTION) == 0) {
                from = ModBiomes.THE_CORRUPTION;
                to = ModBiomes.THE_CRIMSON;
            } else {
                from = ModBiomes.THE_CRIMSON;
                to = ModBiomes.THE_CORRUPTION;
            }
        }
        Registry<Biome> biomes = server.registryAccess().registryOrThrow(Registries.BIOME);
        return new Pair<>(biomes.getHolderOrThrow(from), biomes.getHolderOrThrow(to));
    }

    default List<Holder<Biome>> confluence$getJungleBiomes() {
        return ((BiomeSource) this).possibleBiomes().stream().filter(OverworldUtils::isJungle).toList();
    }

    default @Nullable BannedBiomeMultiNoiseBiomeSource confluence$getBannedBiomeSource() {
        return this instanceof BannedBiomeMultiNoiseBiomeSource source ? source : null;
    }
}
