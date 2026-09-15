package org.confluence.mod.common.worldgen.biome.injector;

import net.minecraft.world.level.biome.BiomeSource;
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
public final class BiomeSourceInjector {
    private static volatile Map<BiomeSource, BiomeSourceHandler> handlers = Map.of();

    private BiomeSourceInjector() {}

    @Nullable
    public static BiomeSourceHandler handlerOf(BiomeSource source) {
        return handlers.get(source);
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
