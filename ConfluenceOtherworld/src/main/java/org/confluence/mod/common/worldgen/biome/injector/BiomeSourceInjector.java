package org.confluence.mod.common.worldgen.biome.injector;

import net.minecraft.world.level.biome.BiomeSource;
import org.confluence.mod.mixed.IInjectBiomeSource;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/// 处理器注册表。以 `BiomeSource` 实例为键（`BiomeSource` 没有覆写 equals/hashCode，
/// 所以是身份语义），而不是像 TerraBlender 那样把可变状态挂在共享的数据包注册表对象上。
///
/// 这样做的直接好处：
///
/// - 同一个 `NoiseGeneratorSettings` 被多个维度复用时不会互相污染；
/// - 换存档时 `BiomeSource` 实例是新的，不需要任何「只能追加一次」的闩锁，
///   在同一 JVM 里连续开多个世界也不会残留状态。
///
/// ## 为什么要回落到实例字段
///
/// 身份索引有一个致命盲区：**别人 clone 出来的副本**。TerraBlender 的
/// `MixinNoiseBasedChunkGenerator` 会把 `fillBiomesFromNoise` 的参数换成
/// `MultiNoiseBiomeSource#clone()`（`Object.clone()` 浅拷贝），这个副本在注册表里
/// 是查不到的；一旦回落成「没有处理器」，写进区块的群系就是纯原版，建面时
/// `SurfaceRules#isBiome` 读的正是这份区块数据，地表规则会整段失效。
///
/// 所以 `install()` 除了写注册表，还会把同一个处理器写到
/// {@link IInjectBiomeSource#confluence$setHandler} 的实例字段上 —— clone 会连带复制
/// 字段，于是副本也能命中。这里只是把那条路并进查找，调用方无需感知。
public final class BiomeSourceInjector {
    private static volatile Map<BiomeSource, BiomeSourceHandler> handlers = Map.of();

    private BiomeSourceInjector() {}

    @Nullable
    public static BiomeSourceHandler handlerOf(BiomeSource source) {
        BiomeSourceHandler handler = handlers.get(source);
        if (handler == null && source instanceof IInjectBiomeSource injectBiomeSource) {
            handler = injectBiomeSource.confluence$getHandler();
        }
        return handler;
    }

    public static boolean isInstalled() {
        return !handlers.isEmpty();
    }

    /// 在 `ServerAboutToStartEvent` 里调用。必须在原版第一次读取 `possibleBiomes()`
    /// 之前完成（`loadLevel()` 之前，见 `ServerLifecycleHooks.handleServerAboutToStart`）。
    static void setHandlers(Map<BiomeSource, BiomeSourceHandler> map) {
        handlers = Map.copyOf(map);
    }

    public static void uninstall() {
        handlers = Map.of();
    }
}
