package org.confluence.mod.mixin.world.level.biome;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import org.confluence.mod.common.worldgen.biome.injector.BiomeSourceHandler;
import org.confluence.mod.common.worldgen.biome.injector.BiomeSourceInjector;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.*;

/// 让 `possibleBiomes()` **在每次调用时**都是最新的，而不是被一次过早的记忆化冻住。
///
/// ## 为什么需要这一层
///
/// 本模组原先是在 `MultiNoiseBiomeSource#collectPossibleBiomes` 上追加自己区域里的群系。
/// 那条路有个致命时序问题：`BiomeSource#possibleBiomes` 是
/// `Suppliers.memoize(() -> collectPossibleBiomes()...)`，**只在第一次求值时算一次**。
/// 而那次求值早于 `ConfluenceBiomeInjector#install`（`ServerAboutToStartEvent`）——
/// 实测日志：`collectPossibleBiomes() on MultiNoiseBiomeSource: base=5` 紧接着
/// `...and no handler was installed at that moment`。原因在事件顺序：TerraBlender 的
/// `ServerAboutToStart` 监听器排在本模组前面，它的 `initializeBiomes` →
/// `appendDeferredBiomesList` 就会读一次那份集合，memoize 就地定稿；等本模组
/// `install()` 建好表时，`collectPossibleBiomes()` 已经不会再被调用了。
/// 所以写入端没有第二次机会，只能改到**读端**做幂等并集。
///
/// 后果（修复前）有两个，都是同一个变量造成的：
///
/// - `ChunkGenerator#applyBiomeDecoration` 的 `set.retainAll(biomeSource.possibleBiomes())`
///   （1.20.1 的 `ChunkGenerator.java:289`）把我们的群系整个剔掉，**地物一个都不生成**；
/// - `BiomeSource#findClosestBiome3d` 第一行就是 `possibleBiomes().stream().filter(...)`
///   （`BiomeSource.java:76`），集合里没有目标群系就直接返回 null，**`/locate biome` 报找不到**。
///
/// ## 为什么不改字段
///
/// TerraBlender 走的是「把 `BiomeSource.possibleBiomes` 这个字段整个替换掉」
/// （`MixinBiomeSource#appendDeferredBiomesList`）。本类不碰字段，只在**读取端**做并集：
/// `original.call()` 读到的仍是 TB 替换后的字段值，所以 TB 与其依赖模组的群系一个不丢；
/// 我们的区域群系则与求值时机彻底解耦 —— 谁先求值、谁后写字段都不影响结果。
///
/// ## 开销
///
/// `possibleBiomes()` 是**每个区块**都会被调用的（`ChunkGenerator#applyBiomeDecoration`），
/// 所以热路径必须做到「几次指针比较」：见 `confluence$withRegionBiomes` 里的命中判定，
/// 稳态下不分配、不遍历、不哈希。
@Mixin(value = BiomeSource.class, priority = 1100)
public abstract class BiomeSourceMixin {
    /// 按 `BiomeSource` 实例缓存并集。`WeakHashMap` 是为了换存档时不堆积 ——
    /// 旧 `BiomeSource` 被回收，条目随之消失。键是身份语义（`BiomeSource` 没有覆写 equals/hashCode）。
    @Unique
    private static final Map<BiomeSource, Entry> confluence$unions = Collections.synchronizedMap(new WeakHashMap<>());

    /// `handler` / `extras` 存的是**身份**，命中判定因此全是 `==`：
    ///
    /// - 换了处理器（`install()` 重建、换存档）→ 身份变 → 重建；
    /// - 换了 extras（末地 `TheEndBiomeHolder#open` 之前是空表、之后非空）→ 身份变 → 重建。
    ///
    /// **刻意不拿 `base` 当判据。** 曾经试过 `cached.base() == base`：它依赖
    /// 「`original.call()` 返回的是同一个 Set 实例」这个假设，而那是个实现细节
    /// （理论上 `Suppliers.memoize` 稳定，但只要有人把结果再包一层就会每次都是新实例），
    /// 一旦不成立，缓存就静默退化成每次重建 —— 而 `possibleBiomes()` 是每个区块都要走的。
    /// 现在判定只依赖「处理器身份 + extras 身份 + 缓存内容」这三件完全可控的事：
    /// 缓存里的并集是**已验证包含完整 extras** 的，所以 extras 身份不变就意味着它仍然正确。
    @Unique
    private record Entry(BiomeSourceHandler handler, List<Holder<Biome>> extras, Set<Holder<Biome>> union) {}

    @WrapMethod(method = "possibleBiomes")
    private Set<Holder<Biome>> confluence$withRegionBiomes(Operation<Set<Holder<Biome>>> original) {
        Set<Holder<Biome>> base = original.call();
        BiomeSource source = (BiomeSource) (Object) this;
        BiomeSourceHandler handler = BiomeSourceInjector.handlerOf(source);
        if (handler == null) {
            // 只可能发生在 install() 之前：这时补不了任何东西，原样返回，
            // 等 install() 之后再查就会命中下面的并集分支。
            return base;
        }

        List<Holder<Biome>> extras = handler.extraBiomes();
        if (extras.isEmpty()) {
            // 处理器还没初始化完（例如末地的 `TheEndBiomeHolder#open` 尚未跑）。
            // 返回 base 且**不写缓存** —— 否则会把残缺并集存成终态，
            // 那正是 collectPossibleBiomes 那一层刚踩过的坑。
            return base;
        }

        Entry entry = confluence$unions.get(source);
        // 热路径：两次指针比较，无分配、无遍历、无哈希。
        if (entry != null && entry.handler() == handler && entry.extras() == extras) {
            return entry.union();
        }

        // 冷路径（每个来源/每套 extras 只走一次）。base 本来就是 install() 之后求值的那一份时，
        // 里面已经有我们的群系，不必再包一层。
        Set<Holder<Biome>> union;
        if (base.containsAll(extras)) {
            union = base;
        } else {
            union = new ObjectArraySet<>(base.size() + extras.size());
            union.addAll(base);
            union.addAll(extras);
        }
        confluence$unions.put(source, new Entry(handler, extras, union));
        return union;
    }
}
