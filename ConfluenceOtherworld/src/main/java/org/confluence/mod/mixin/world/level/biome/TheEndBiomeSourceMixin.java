package org.confluence.mod.mixin.world.level.biome;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.TheEndBiomeSource;
import org.confluence.lib.mixed.SelfGetter;
import org.confluence.mod.common.worldgen.biome.injector.BiomeSourceHandler;
import org.confluence.mod.common.worldgen.biome.injector.BiomeSourceInjector;
import org.spongepowered.asm.mixin.Mixin;

/// 末地群系注入。
///
/// 这里只需要 `getNoiseBiome` 这一个切点：`possibleBiomes()` 的追加已经统一由
/// {@link BiomeSourceMixin} 在 `BiomeSource` 层面完成（本 mixin 的处理器也是
/// `BiomeSourceInjector` 里的一份，所以自动被覆盖到）。
@Mixin(TheEndBiomeSource.class)
public abstract class TheEndBiomeSourceMixin implements SelfGetter<TheEndBiomeSource> {
    @WrapMethod(method = "getNoiseBiome(IIILnet/minecraft/world/level/biome/Climate$Sampler;)Lnet/minecraft/core/Holder;")
    private Holder<Biome> confluence$injectBiome(int x, int y, int z, Climate.Sampler sampler, Operation<Holder<Biome>> original) {
        BiomeSourceHandler handler = BiomeSourceInjector.handlerOf(confluence$self());
        if (handler == null) return original.call(x, y, z, sampler);
        return handler.resolve(x, y, z, sampler, () -> original.call(x, y, z, sampler));
    }
}
