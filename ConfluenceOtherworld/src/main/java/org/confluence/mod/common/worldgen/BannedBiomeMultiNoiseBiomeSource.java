package org.confluence.mod.common.worldgen;

import PortLib.extensions.net.minecraft.core.HolderLookup.PortHolderLookupExtension;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraftforge.server.ServerLifecycleHooks;
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
            this.target = PortHolderLookupExtension.Provider.holderOrThrow(server.registryAccess(), targetBiome);
            this.protection = PortHolderLookupExtension.Provider.holderOrThrow(server.registryAccess(), Biomes.PLAINS);
        }
        if (biome.is(bannedBiome)) {
            ServerLevelData data = ServerLifecycleHooks.getCurrentServer().getWorldData().overworldData();
            BlockPos spawnPos = new BlockPos(data.getXSpawn(), data.getYSpawn(), data.getZSpawn());
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
