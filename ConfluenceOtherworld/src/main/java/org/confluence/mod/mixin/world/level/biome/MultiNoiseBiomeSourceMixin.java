package org.confluence.mod.mixin.world.level.biome;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.confluence.mod.common.init.ModBiomes;
import org.confluence.mod.common.init.ModSecretSeeds;
import org.confluence.mod.common.worldgen.BannedBiomeMultiNoiseBiomeSource;
import org.confluence.mod.common.worldgen.biome.injector.BiomeSourceHandler;
import org.confluence.mod.common.worldgen.biome.injector.BiomeSourceInjector;
import org.confluence.mod.mixed.IMinecraftServer;
import org.confluence.mod.mixed.IMultiNoiseBiomeSource;
import org.confluence.mod.mixed.IWorldOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

/// 群系注入的切点，以及**为什么优先级必须是 1100**。
///
/// ## 与 TerraBlender 的共存靠优先级，不靠共享状态
///
/// TerraBlender（下称 TB）装与不装都要保证自定义群系能出。TB 的做法是在
/// `terrablender.mixin.MixinMultiNoiseBiomeSource` 里对同一个
/// `getNoiseBiome(IIILClimate$Sampler;)` 下了一个 `@Inject(cancellable = true)`，
/// 走它自己的 `Climate.ParameterList#findValuePositional`（分层噪声定区域 + RTree 最近邻）。
/// 本 mixin 是 `@WrapMethod`，优先级 1100 > TB 默认的 1000，于是：
///
/// - Mixin 按 `MixinInfo#compareTo` 升序应用（优先级大的后应用），TB 先、本模组后；
/// - `@Inject` 的回调块用 `insertBefore` 插在目标指令之前，**后插的在前**，
///   所以本模组的 `@WrapMethod` 在最终指令序列里位于 TB 的取消块之外；
/// - MixinExtras 的 `@WrapMethod` 是方法体级的重写（`WrapMethodApplicatorExtension`
///   把原方法体挪成 `WrapOperation` 目标，后应用者包在先前那层外面），
///   因此 TB 的 `if (ci.isCancelled()) return ci.getReturnValue();` 也在被包住的方法体里。
///
/// 三者合起来就是：**本模组先判自己的区域，接管了就直接返回，没接管才 `original.call(...)`
/// 交给 TB**。反过来（优先级低于 1000）TB 会先取消并把返回值定死，本模组再也改不动。
/// 这也是为什么这里必须用 `@WrapMethod` 而不是 `@Inject(cancellable = true)`：
/// 可取消注入之间是「先取消者胜」，拿不到确定性的先后。
///
/// 副作用说明：本模组接管某一列时不会去调用 TB 的查找（`resolve` 只在需要回落时才求值
/// `original`），所以两边各算各的，没有重复开销，也不互相污染 `Regions` / `uniqueness` 等全局表。
///
/// ## 不再在 `collectPossibleBiomes` 上追加群系
///
/// 追加点已经挪到 [BiomeSourceMixin#confluence$withRegionBiomes]
/// （环绕 `BiomeSource#possibleBiomes`）。原因是 `possibleBiomes` 是
/// `Suppliers.memoize(() -> collectPossibleBiomes()...)`，**只在第一次求值时算一次**，
/// 而那次求值早于 `ConfluenceBiomeInjector#install` 注册处理器，于是这里追加的群系会被永久丢弃
/// —— 症状是区块里出得来的群系却没有地物（`ChunkGenerator#applyBiomeDecoration` 的 `retainAll` 剔了它），
/// 且 `/locate biome` 找不到它（`BiomeSource#findClosestBiome3d` 先按这份集合筛）。
/// 改到读端做并集后，这个问题与求值时机彻底解耦。
@Mixin(value = MultiNoiseBiomeSource.class, priority = 1100)
public abstract class MultiNoiseBiomeSourceMixin implements IMultiNoiseBiomeSource {
    @Unique
    private Pair<Holder<Biome>, Holder<Biome>> confluence$biomePair;

    @WrapMethod(method = "getNoiseBiome(IIILnet/minecraft/world/level/biome/Climate$Sampler;)Lnet/minecraft/core/Holder;")
    private Holder<Biome> confluence$injectBiome(int x, int y, int z, Climate.Sampler sampler, Operation<Holder<Biome>> original) {
        BiomeSourceHandler handler = BiomeSourceInjector.handlerOf(confluence$self());
        if (handler == null) return original.call(x, y, z, sampler);
        return handler.resolve(x, y, z, sampler, () -> original.call(x, y, z, sampler));
    }

    @Override
    public Pair<Holder<Biome>, Holder<Biome>> confluence$getBiomePair() {
        if (confluence$biomePair == null) {
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            if (server == null) return null;
            WorldOptions worldOptions = server.getWorldData().worldGenOptions();
            long flag = IWorldOptions.of(worldOptions).confluence$getSecretFlag();
            ResourceKey<Biome> from;
            ResourceKey<Biome> to;
            if (confluence$self() instanceof BannedBiomeMultiNoiseBiomeSource) {
                return this.confluence$biomePair = new Pair<>(null, null);
            } else if (ModSecretSeeds.DRUNK_WORLD.match(flag)) {
                IMinecraftServer.of(server).confluence$updateSecretFlag(IWorldOptions.DOUBLE_EVIL);
                return this.confluence$biomePair = new Pair<>(null, null);
            } else if ((flag & IWorldOptions.DOUBLE_EVIL) == 0) {
                if (net.minecraft.util.RandomSource.create(worldOptions.seed()).nextBoolean()) {
                    from = ModBiomes.THE_CORRUPTION;
                    to = ModBiomes.THE_CRIMSON;
                    IMinecraftServer.of(server).confluence$updateSecretFlag(IWorldOptions.THE_CRIMSON);
                } else {
                    from = ModBiomes.THE_CRIMSON;
                    to = ModBiomes.THE_CORRUPTION;
                    IMinecraftServer.of(server).confluence$updateSecretFlag(IWorldOptions.THE_CORRUPTION);
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
