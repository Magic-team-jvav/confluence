package org.confluence.mod.mixin.world.level.biome;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import org.confluence.lib.mixed.SelfGetter;
import org.confluence.mod.common.worldgen.biome.injector.BiomeSourceHandler;
import org.confluence.mod.common.worldgen.biome.injector.BiomeSourceInjector;
import org.confluence.mod.mixed.IInjectBiomeSource;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = MultiNoiseBiomeSource.class, priority = 1100)
public abstract class MultiNoiseBiomeSourceMixin implements IInjectBiomeSource, SelfGetter<MultiNoiseBiomeSource> {
    @Unique
    private Pair<Holder<Biome>, Holder<Biome>> confluence$biomePair;

    /// 在 `install()` 里写入，之后只读。
    ///
    /// 存成字段而不是只放 `BiomeSourceInjector` 的注册表，是为了穿过 TerraBlender 的
    /// `MultiNoiseBiomeSource#clone()`：那是 `Object.clone()` 浅拷贝，会连同这个字段一起
    /// 复制到 clone 上；注册表按实例身份索引，clone 是查不到的。
    @Unique
    @Nullable
    private BiomeSourceHandler confluence$handler;

    @WrapMethod(method = "getNoiseBiome(IIILnet/minecraft/world/level/biome/Climate$Sampler;)Lnet/minecraft/core/Holder;")
    private Holder<Biome> confluence$injectBiome(int x, int y, int z, Climate.Sampler sampler, Operation<Holder<Biome>> original) {
        BiomeSourceHandler handler = this.confluence$handler;
        if (handler == null) handler = BiomeSourceInjector.handlerOf(confluence$self());
        if (handler == null) return original.call(x, y, z, sampler);
        return handler.resolve(x, y, z, sampler, () -> original.call(x, y, z, sampler));
    }

    @Override
    public void confluence$setHandler(@Nullable BiomeSourceHandler handler) {
        this.confluence$handler = handler;
    }

    @Override
    public @Nullable BiomeSourceHandler confluence$getHandler() {
        return this.confluence$handler;
    }

    @Override
    public Pair<Holder<Biome>, Holder<Biome>> confluence$getBiomePair() {
        if (confluence$biomePair == null) {
            this.confluence$biomePair = confluence$createBiomePair();
        }
        return confluence$biomePair;
    }
}
