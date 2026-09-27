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
/// ## 优先级为什么必须写 1100
///
/// 1.21.1 的 TerraBlender（4.x）**新增了** `terrablender.mixin.MixinTheEndBiomeSource`，
/// 它同样注入 `TheEndBiomeSource#getNoiseBiome`（可取消，会 `setReturnValue`）——
/// 也就是和本 mixin 落在同一个注入点上，而 TB 没写 `priority`，用默认 1000。
/// 本 mixin 若也用默认值，两者同优先级时应用顺序由 Mixin 内部 order 决定，不可控；
/// 一旦 TB 先应用，它的取消块就包在外面，本模组的末地群系再也出不来。
/// 写 1100 保证本 mixin 后应用、包在 TB 的取消块外面，语义与主世界那个（同样是 1100）一致。
///
/// 判定顺序因此和主世界一样：**先判本模组的区域，接管了直接返回，没接管才 `original.call(...)`
/// 交给 TB**。TB 自己那套末地高地/中地/边缘/岛屿噪声生成、以及它给别家模组加的末地群系，
/// 都在 `original` 里，一个不丢。
///
/// ## 只需要 `getNoiseBiome` 一个切点
///
/// `possibleBiomes()` 的追加已经统一由 {@link BiomeSourceMixin} 在 `BiomeSource` 层面完成
/// （末地处理器本来就是 `BiomeSourceInjector` 里的一份，自动被覆盖到）。
/// 这不影响 TB：它的 `onCollectPossibleBiomes` 加在 `collectPossibleBiomes` 上，
/// 而本模组包的是上一层的 `possibleBiomes()`，TB 的产出会进到 `original.call()` 的结果里。
@Mixin(value = TheEndBiomeSource.class, priority = 1100)
public abstract class TheEndBiomeSourceMixin implements SelfGetter<TheEndBiomeSource> {
    @WrapMethod(method = "getNoiseBiome(IIILnet/minecraft/world/level/biome/Climate$Sampler;)Lnet/minecraft/core/Holder;")
    private Holder<Biome> confluence$injectBiome(int x, int y, int z, Climate.Sampler sampler, Operation<Holder<Biome>> original) {
        BiomeSourceHandler handler = BiomeSourceInjector.handlerOf(confluence$self());
        if (handler == null) return original.call(x, y, z, sampler);
        return handler.resolve(x, y, z, sampler, () -> original.call(x, y, z, sampler));
    }
}
