package org.confluence.mod.common.worldgen;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.confluence.mod.mixin.accessor.MultiNoiseBiomeSourceAccessor;

public class BannedBiomeMultiNoiseBiomeSource extends MultiNoiseBiomeSource {
    private final ResourceKey<Biome> bannedBiome;
    private final ResourceKey<Biome> targetBiome;
    private Holder<Biome> target;

    public BannedBiomeMultiNoiseBiomeSource(MultiNoiseBiomeSource biomeSource, ResourceKey<Biome> bannedBiome, ResourceKey<Biome> targetBiome) {
        super(((MultiNoiseBiomeSourceAccessor) biomeSource).getParameters());
        this.bannedBiome = bannedBiome;
        this.targetBiome = targetBiome;
    }

    @Override
    public Holder<Biome> getNoiseBiome(int pX, int pY, int pZ, Climate.Sampler pSampler) {
        Holder<Biome> biome = super.getNoiseBiome(pX, pY, pZ, pSampler);
        if (biome.is(bannedBiome)) {
            if (target == null) {
                MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
                if (server == null) return biome;
                this.target = server.registryAccess().registryOrThrow(Registries.BIOME).getHolderOrThrow(targetBiome);
            }
            return target;
        }
        return biome;
    }
}
