package org.confluence.mod.mixin.integration.terrablender;

import com.bawnorton.mixinsquared.TargetHandler;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.confluence.mod.common.init.ModBiomes;
import org.confluence.mod.common.worldgen.secret_seed.ModSecretSeeds;
import org.confluence.mod.common.worldgen.secret_seed.NotTheBees;
import org.confluence.mod.mixed.IWorldOptions;
import org.confluence.terra_curio.mixed.SelfGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mixin(value = MultiNoiseBiomeSource.class, priority = 1100)
public abstract class MixinMultiNoiseBiomeSourceSquared implements SelfGetter<MultiNoiseBiomeSource> {
    @Unique
    private long confluence$secretFlag = -1L;
    @Unique
    private List<Holder<Biome>> confluence$jungle;
    @Unique
    private Pair<Holder<Biome>, Holder<Biome>> confluence$biomePair;

    @TargetHandler(mixin = "terrablender.mixin.MixinMultiNoiseBiomeSource", name = "getNoiseBiome")
    @Inject(method = "@MixinSquared:Handler", at = @At("TAIL"))
    private void replaceBiome(int x, int y, int z, Climate.Sampler sampler, CallbackInfoReturnable<Holder<Biome>> cir, CallbackInfo ci) {
        if (cir.getReturnValue() != null) {
            if (ModSecretSeeds.NOT_THE_BEES.match(confluence$getSecretFlag())) {
                List<Holder<Biome>> jungle = confluence$getJungle();
                if (!jungle.isEmpty()) {
                    cir.setReturnValue(NotTheBees.replaceBiome(x, y, z, cir.getReturnValue(), jungle));
                }
            } else {
                Pair<Holder<Biome>, Holder<Biome>> pair = confluence$getBiomePair();
                if (pair != null && pair.getSecond() != null && cir.getReturnValue() == pair.getFirst()) {
                    cir.setReturnValue(pair.getSecond());
                }
            }
        }
    }

    @Unique
    private List<Holder<Biome>> confluence$getJungle() {
        if (confluence$jungle == null) {
            this.confluence$jungle = new ArrayList<>();
            Set<Holder<Biome>> set = new HashSet<>();
            for (Holder<Biome> holder : self().possibleBiomes()) {
                if (holder.is(Tags.Biomes.IS_JUNGLE)) {
                    set.add(holder);
                }
            }
            confluence$jungle.addAll(set);
        }
        return confluence$jungle;
    }

    @Unique
    private long confluence$getSecretFlag() {
        if (confluence$secretFlag == -1L) {
            if (ServerLifecycleHooks.getCurrentServer() == null) {
                return 0L;
            } else {
                this.confluence$secretFlag = ((IWorldOptions) ServerLifecycleHooks.getCurrentServer().getWorldData().worldGenOptions()).confluence$getSecretFlag();
            }
        }
        return confluence$secretFlag;
    }

    @Unique
    private Pair<Holder<Biome>, Holder<Biome>> confluence$getBiomePair() {
        if (confluence$biomePair == null) {
            if (ServerLifecycleHooks.getCurrentServer() != null) {
                WorldOptions worldOptions = ServerLifecycleHooks.getCurrentServer().getWorldData().worldGenOptions();
                LegacyRandomSource randomSource = new LegacyRandomSource(worldOptions.seed());
                Pair<ResourceKey<Biome>, ResourceKey<Biome>> pair;
                if (randomSource.nextBoolean()) {
                    pair = new Pair<>(ModBiomes.THE_CORRUPTION, ModBiomes.TR_CRIMSON);
                    ((IWorldOptions) worldOptions).confluence$withSecretFlag(IWorldOptions.TR_CRIMSON);
                } else {
                    pair = new Pair<>(ModBiomes.TR_CRIMSON, ModBiomes.THE_CORRUPTION);
                    ((IWorldOptions) worldOptions).confluence$withSecretFlag(IWorldOptions.THE_CORRUPTION);
                }
                Holder<Biome> source = null;
                Holder<Biome> target = null;
                for (Holder<Biome> biome : self().possibleBiomes()) {
                    if (biome.is(pair.getFirst())) {
                        source = biome;
                    } else if (biome.is(pair.getSecond())) {
                        target = biome;
                    }
                }
                this.confluence$biomePair = new Pair<>(source, target);
            }
        }
        return confluence$biomePair;
    }
}
