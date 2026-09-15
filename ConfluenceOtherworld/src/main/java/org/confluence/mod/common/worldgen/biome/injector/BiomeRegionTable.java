package org.confluence.mod.common.worldgen.biome.injector;

import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import org.confluence.mod.Confluence;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

/// 某个维度类别下、某个 `BiomeSource` 实例专属的区域表。
///
/// 表一旦建好就是不可变的，可以安全地发布给世界生成的工作线程。
///
/// ## 查询流程
///
/// 1. {@link BiomeRegionAllocator#index} 决定这一列归哪个区域（0 = 原版）；
/// 2. 归区域时，按**声明顺序**在区域自己的参数盒子里找第一个包含当前气候单元的；
/// 3. 一个都不包含就返回 `null`，调用方回落原版 —— 这就是取代 TerraBlender
///    `terrablender:deferred_placeholder` 占位群系的机制。
///
/// 因为用的是包含判定而不是 `Climate.RTree` 的最近邻，这里不需要访问包私有的
/// `Climate$RTree`，也就不需要任何 access widener；只需要公开的
/// `Climate.Parameter#distance(long)`。
public final class BiomeRegionTable {
    public record Entry(Climate.ParameterPoint parameters, Holder<Biome> biome) {}

    private final BiomeRegionType type;
    private final BiomeRegionAllocator allocator;
    private final List<BiomeRegion> regions;
    /// 下标 0 恒为空表（原版区域），下标 i 对应 {@code regions.get(i - 1)}。
    private final List<List<Entry>> entries;
    private final List<Holder<Biome>> biomes;
    /// 诊断用位图，每个区域占两位（命中 / 未命中）。
    private final AtomicInteger diagnostics = new AtomicInteger();

    private BiomeRegionTable(BiomeRegionType type, BiomeRegionAllocator allocator,
                             List<BiomeRegion> regions, List<List<Entry>> entries, List<Holder<Biome>> biomes) {
        this.type = type;
        this.allocator = allocator;
        this.regions = regions;
        this.entries = entries;
        this.biomes = biomes;
    }

    /// @param remap 群系键重映射，用于「同一套区域布局，按世界旗标把某个邪恶群系换成另一个」，
    ///              这样腐化世界与猩红世界的地形骨架完全一致。
    public static BiomeRegionTable build(BiomeRegionType type, long seed, Registry<Biome> biomeRegistry,
                                        List<BiomeRegion> regions, UnaryOperator<ResourceKey<Biome>> remap) {
        int[] weights = new int[regions.size()];
        for (int i = 0; i < weights.length; i++) {
            weights[i] = Math.max(1, regions.get(i).weight());
        }
        BiomeRegionAllocator allocator = new BiomeRegionAllocator(seed, Math.max(1, type.vanillaWeight()), weights, type.regionSizeBlocks());

        List<List<Entry>> entries = new ArrayList<>(regions.size() + 1);
        entries.add(List.of());
        Set<Holder<Biome>> biomes = new LinkedHashSet<>();
        for (BiomeRegion region : regions) {
            List<Entry> collected = new ArrayList<>();
            region.addBiomes(pair -> {
                ResourceKey<Biome> key = remap.apply(pair.getSecond());
                biomeRegistry.getHolder(key).ifPresent(holder -> collected.add(new Entry(pair.getFirst(), holder)));
            });
            List<Entry> immutable = List.copyOf(collected);
            entries.add(immutable);
            for (Entry entry : immutable) {
                biomes.add(entry.biome());
            }
            if (immutable.isEmpty()) {
                // 区域一个参数点都没落地，说明群系键没注册（打错名）或声明的区间永远不满足。
                // 这种情况下该区域仍然占着一份权重带宽，表现为「本来该出区域的地方全是原版」。
                Confluence.LOGGER.warn("Biome region {} produced no parameter points; its weight band will fall back to vanilla", region.id());
            }
        }
        return new BiomeRegionTable(type, allocator, List.copyOf(regions), List.copyOf(entries), List.copyOf(biomes));
    }

    public BiomeRegionType type() {
        return type;
    }

    public List<BiomeRegion> regions() {
        return regions;
    }

    public List<Holder<Biome>> biomes() {
        return biomes;
    }

    public Stream<Holder<Biome>> biomeStream() {
        return biomes.stream();
    }

    public int index(int quartX, int quartZ) {
        return allocator.index(quartX, quartZ);
    }

    public BiomeRegionAllocator allocator() {
        return allocator;
    }

    /// 查表：区域 `index` 是否接管当前气候单元；不接管返回 `null`。
    @Nullable
    public Holder<Biome> find(int index, Climate.TargetPoint target) {
        if (index <= 0 || index >= entries.size()) return null;
        List<Entry> list = entries.get(index);
        for (int i = 0; i < list.size(); i++) {
            Entry entry = list.get(i);
            if (covers(entry.parameters(), target)) return entry.biome();
        }
        return null;
    }

    /// 完整查询。`null` 表示应当回落原版。
    ///
    /// 注意：只有确实归某个区域时才会去采样气候，归原版时不会产生额外开销。
    @Nullable
    public Holder<Biome> resolve(int quartX, int quartY, int quartZ, Climate.Sampler sampler) {
        int index = allocator.index(quartX, quartZ);
        if (index == 0) return null;
        Climate.TargetPoint target = sampler.sample(quartX, quartY, quartZ);
        Holder<Biome> biome = find(index, target);
        diagnose(index, biome != null, quartX, quartY, quartZ, target);
        return biome;
    }

    /// 每个区域只记录一次「命中」和一次「接管了这一列但没有任何参数盒包含当前气候」。
    ///
    /// 这两条日志是判断区域是否真的在生效的最直接依据：
    ///
    /// - 一条都没有 → 区域分配器从没把任何一列判给该区域（噪声场或权重有问题）；
    /// - 只有 miss → 区域接管了，但声明的气候区间永远命中不了（区间写错，或用了零宽度区间）。
    private void diagnose(int index, boolean hit, int quartX, int quartY, int quartZ, Climate.TargetPoint target) {
        int bit = 1 << (index * 2 + (hit ? 0 : 1));
        if ((this.diagnostics.get() & bit) != 0) return;
        if ((this.diagnostics.getAndAccumulate(bit, (current, update) -> current | update) & bit) != 0) return;

        BiomeRegion region = this.regions.get(index - 1);
        if (hit) {
            // 用方块坐标，方便直接拿去 /tp 验证。
            Confluence.LOGGER.info("Biome region {} took over at block ({}, {}, {}); climate T={} H={} C={} E={} D={} W={}",
                    region.id(), QuartPos.toBlock(quartX), QuartPos.toBlock(quartY), QuartPos.toBlock(quartZ),
                    target.temperature(), target.humidity(), target.continentalness(),
                    target.erosion(), target.depth(), target.weirdness());
        } else {
            Confluence.LOGGER.info("Biome region {} owns column ({}, {}) but no parameter box covers T={} H={} C={} E={} D={} W={}; falling back to vanilla",
                    region.id(), QuartPos.toBlock(quartX), QuartPos.toBlock(quartZ),
                    target.temperature(), target.humidity(), target.continentalness(),
                    target.erosion(), target.depth(), target.weirdness());
        }
    }

    /// 供启动日志使用的一句话描述。
    public String describe() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < this.regions.size(); i++) {
            if (i > 0) builder.append(", ");
            BiomeRegion region = this.regions.get(i);
            builder.append(region.id().getPath())
                    .append("(w=").append(region.weight())
                    .append(",pts=").append(this.entries.get(i + 1).size())
                    .append(')');
        }
        return builder.toString();
    }

    /// 参数盒是否包含目标气候单元。
    ///
    /// 判定 6 个气候轴（temperature / humidity / continentalness / erosion / depth / weirdness）；
    /// `offset` 不参与划分，区域声明一律用 `offset = 0`。
    public static boolean covers(Climate.ParameterPoint parameters, Climate.TargetPoint target) {
        return parameters.temperature().distance(target.temperature()) == 0L
                && parameters.humidity().distance(target.humidity()) == 0L
                && parameters.continentalness().distance(target.continentalness()) == 0L
                && parameters.erosion().distance(target.erosion()) == 0L
                && parameters.depth().distance(target.depth()) == 0L
                && parameters.weirdness().distance(target.weirdness()) == 0L;
    }
}
