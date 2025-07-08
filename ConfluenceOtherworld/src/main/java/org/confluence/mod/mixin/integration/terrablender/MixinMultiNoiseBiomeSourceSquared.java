package org.confluence.mod.mixin.integration.terrablender;

import com.bawnorton.mixinsquared.TargetHandler;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.confluence.lib.mixed.SelfGetter;
import org.confluence.mod.common.init.ModBiomes;
import org.confluence.mod.common.init.ModSecretSeeds;
import org.confluence.mod.common.worldgen.BannedBiomeMultiNoiseBiomeSource;
import org.confluence.mod.mixed.IMinecraftServer;
import org.confluence.mod.mixed.IMultiNoiseBiomeSource;
import org.confluence.mod.mixed.IWorldOptions;
import org.confluence.mod.util.OverworldUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mixin(value = MultiNoiseBiomeSource.class, priority = 1100)
public abstract class MixinMultiNoiseBiomeSourceSquared implements SelfGetter<MultiNoiseBiomeSource>, IMultiNoiseBiomeSource {
    @Unique
    private List<Holder<Biome>> confluence$jungle;
    @Unique
    private Pair<Holder<Biome>, Holder<Biome>> confluence$biomePair;
    @Unique
    private Holder<Biome> confluence$protection;

    @TargetHandler(mixin = "terrablender.mixin.MixinMultiNoiseBiomeSource", name = "getNoiseBiome")
    @Inject(method = "@MixinSquared:Handler", at = @At("TAIL"))
    private void replaceBiome(int x, int y, int z, Climate.Sampler sampler, CallbackInfoReturnable<Holder<Biome>> cir, CallbackInfo ci) {
        OverworldUtils.replaceBiome(confluence$self(), x, y, z, cir, () -> {
            if (confluence$jungle == null) {
                this.confluence$jungle = new ArrayList<>();
                Set<Holder<Biome>> set = confluence$self().possibleBiomes().stream().filter(holder -> holder.is(Tags.Biomes.IS_JUNGLE)).collect(Collectors.toSet());
                confluence$jungle.addAll(set);
            }
            return confluence$jungle;
        }, this::confluence$getBiomePair, registryAccess -> {
            if (confluence$protection == null) {
                this.confluence$protection = registryAccess.holderOrThrow(Biomes.PLAINS);
            }
            return confluence$protection;
        });
    }

    @Override
    public Pair<Holder<Biome>, Holder<Biome>> confluence$getBiomePair() {
        if (confluence$biomePair == null) {
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            if (server == null) return null;
            WorldOptions worldOptions = server.getWorldData().worldGenOptions();
            long flag = ((IWorldOptions) worldOptions).confluence$getSecretFlag();
            ResourceKey<Biome> from;
            ResourceKey<Biome> to;
            if (confluence$self() instanceof BannedBiomeMultiNoiseBiomeSource) {
                return this.confluence$biomePair = new Pair<>(null, null);
            } else if (ModSecretSeeds.DRUNK_WORLD.match(flag)) {
                ((IMinecraftServer) server).confluence$updateSecretFlag(IWorldOptions.DOUBLE_EVIL);
                return this.confluence$biomePair = new Pair<>(null, null);
            } else if ((flag & IWorldOptions.DOUBLE_EVIL) == 0) {
                if (new LegacyRandomSource(worldOptions.seed()).nextBoolean()) {
                    from = ModBiomes.THE_CORRUPTION;
                    to = ModBiomes.THE_CRIMSON;
                    ((IMinecraftServer) server).confluence$updateSecretFlag(IWorldOptions.THE_CRIMSON);
                } else {
                    from = ModBiomes.THE_CRIMSON;
                    to = ModBiomes.THE_CORRUPTION;
                    ((IMinecraftServer) server).confluence$updateSecretFlag(IWorldOptions.THE_CORRUPTION);
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
            this.confluence$biomePair = new Pair<>(biomes.getHolderOrThrow(from), biomes.getHolderOrThrow(to));
        }
        return confluence$biomePair;
    }
}
