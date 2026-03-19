package org.confluence.mod.mixin.integration.terrablender;

import com.bawnorton.mixinsquared.TargetHandler;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.TheEndBiomeSource;
import org.confluence.mod.common.worldgen.TheEndBiomeHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.stream.Stream;

@Mixin(value = TheEndBiomeSource.class, priority = 1100)
public abstract class MixinTheEndBiomeSourceSquared {
    @TargetHandler(mixin = "terrablender.mixin.MixinTheEndBiomeSource", name = "onGetNoiseBiome")
    @Inject(method = "@MixinSquared:Handler", at = @At("TAIL"))
    private void replaceBiome(int x, int y, int z, Climate.Sampler sampler, CallbackInfoReturnable<Holder<Biome>> cir, CallbackInfo ci) {
        TheEndBiomeHolder.replaceBiome(x, y, z, sampler, cir);
    }

    /// 不要标记为cancellable
    @TargetHandler(mixin = "terrablender.mixin.MixinTheEndBiomeSource", name = "onCollectPossibleBiomes")
    @Inject(method = "@MixinSquared:Handler", at = @At("TAIL"))
    private void addConfluence(CallbackInfoReturnable<Stream<Holder<Biome>>> cir, CallbackInfo ci) {
        Stream<Holder<Biome>> stream = cir.getReturnValue();
        if (stream != null) {
            cir.setReturnValue(TheEndBiomeHolder.addConfluenceBiomes(stream));
        }
    }
}
