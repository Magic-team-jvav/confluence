package org.confluence.mod.mixin.level;

import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.confluence.mod.common.worldgen.secret_seed.ModSecretSeeds;
import org.confluence.mod.common.worldgen.secret_seed.NotTheBees;
import org.confluence.mod.mixed.IWorldOptions;
import org.confluence.terra_curio.mixed.SelfGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mixin(value = MultiNoiseBiomeSource.class, priority = 900)
public abstract class MultiNoiseBiomeSourceMixin implements SelfGetter<MultiNoiseBiomeSource> {
    @Shadow
    public abstract Holder<Biome> getNoiseBiome(Climate.TargetPoint targetPoint);

    @Unique
    private long confluence$secretFlag = -1L;
    @Unique
    private List<Holder<Biome>> confluence$jungle;

    @Inject(method = "getNoiseBiome(IIILnet/minecraft/world/level/biome/Climate$Sampler;)Lnet/minecraft/core/Holder;", at = @At("HEAD"), cancellable = true)
    private void replaceBiome(int x, int y, int z, Climate.Sampler sampler, CallbackInfoReturnable<Holder<Biome>> cir) {
        if (ModSecretSeeds.NOT_THE_BEES.match(confluence$getSecretFlag())) {
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
            cir.setReturnValue(NotTheBees.replaceBiome(x, y, z, getNoiseBiome(sampler.sample(x, y, z)), confluence$jungle));
        }
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
}
