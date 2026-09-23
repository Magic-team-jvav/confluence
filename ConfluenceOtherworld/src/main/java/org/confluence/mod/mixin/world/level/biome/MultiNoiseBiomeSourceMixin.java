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
import net.minecraftforge.server.ServerLifecycleHooks;
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

    /// **不再在 `collectPossibleBiomes` 上追加本模组的群系。**
    ///
    /// 追加点已经挪到 [BiomeSourceMixin#confluence$withRegionBiomes]
    /// （环绕 `BiomeSource#possibleBiomes`）。原因是 `possibleBiomes` 是
    /// `Suppliers.memoize(() -> collectPossibleBiomes()...)`，**只在第一次求值时算一次**，
    /// 而那次求值实测早于 `ConfluenceBiomeInjector#install` 注册处理器，
    /// 于是这里追加的群系会被永久丢弃 —— 症状是区块里出得来的群系却没有地物
    /// （`ChunkGenerator#applyBiomeDecoration` 的 `retainAll` 把它剔了），
    /// 且 `/locate biome` 找不到它（`BiomeSource#findClosestBiome3d` 先按这份集合筛）。
    /// 改到读端做并集后，这个问题与求值时机彻底解耦。

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
