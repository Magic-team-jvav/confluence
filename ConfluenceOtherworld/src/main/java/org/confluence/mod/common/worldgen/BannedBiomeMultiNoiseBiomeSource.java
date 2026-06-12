package org.confluence.mod.common.worldgen;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import org.confluence.mod.common.init.ModBiomes;
import org.confluence.mod.mixed.IMinecraftServer;
import org.confluence.mod.mixed.IWorldOptions;

public class BannedBiomeMultiNoiseBiomeSource extends MultiNoiseBiomeSource {
    private final ResourceKey<Biome> bannedBiome;
    private final ResourceKey<Biome> targetBiome;
    private Holder<Biome> target;
    private Holder<Biome> protection;

    public BannedBiomeMultiNoiseBiomeSource(MultiNoiseBiomeSource biomeSource, ResourceKey<Biome> bannedBiome, ResourceKey<Biome> targetBiome) {
        super(biomeSource.parameters);
        this.bannedBiome = bannedBiome;
        this.targetBiome = targetBiome;
    }

    @Override
    public Holder<Biome> getNoiseBiome(int x, int y, int z, Climate.Sampler sampler) {
        Holder<Biome> biome = super.getNoiseBiome(x, y, z, sampler);
        if (target == null || protection == null) {
            MinecraftServer server = net.minecraftforge.server.ServerLifecycleHooks.getCurrentServer();
            if (server == null) return biome;
            if (targetBiome.equals(ModBiomes.THE_CORRUPTION)) {
                IMinecraftServer.of(server).confluence$updateSecretFlag(IWorldOptions.THE_CORRUPTION);
            } else if (targetBiome.equals(ModBiomes.THE_CRIMSON)) {
                IMinecraftServer.of(server).confluence$updateSecretFlag(IWorldOptions.THE_CRIMSON);
            }
            this.target = server.registryAccess().holderOrThrow(targetBiome);
            this.protection = server.registryAccess().holderOrThrow(Biomes.PLAINS);
        }
        if (biome.is(bannedBiome)) {
            BlockPos spawnPos = net.minecraftforge.server.ServerLifecycleHooks.getCurrentServer().getWorldData().overworldData().getSpawnPos();
            if (Math.abs((spawnPos.getX() >> 2) - x) <= 50 || Math.abs((spawnPos.getZ() >> 2) - z) <= 50) {
                return protection;
            }
            return target;
        }
        return biome;
    }

    public ResourceKey<Biome> getBannedBiome() {
        return bannedBiome;
    }
}
