package org.confluence.mod.common.init;

import org.confluence.lib.common.worldgen.biome.MiniBiome;
import org.confluence.lib.common.worldgen.biome.MiniBiomeType;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.data.spawner.NPCSpawner;
import org.confluence.mod.common.worldgen.feature.MeteoriteFeature;

/// Confluence 注册的迷你生物群系（泰拉瑞亚 `SceneMetrics` 那套：窗口内方块计数 + 阈值）。
///
/// 这些是**标记**，不影响原版群系，也不参与 `level.getBiome()`：
/// 需要它们的玩法（刷怪、音乐、天气、效果……）得自己查 {@link MiniBiome}。
///
/// 腐化 / 猩红 / 神圣 / 发光蘑菇没有列在这里 —— 它们已经是真正的动态群系
/// （见 {@link ModDynamicBiomes}），重复注册成标记只会产生两套真相。
///
/// 所有阈值都是**校准旋钮**：窗口是半径 {@link MiniBiome#WINDOW_RADIUS} 的立方体，
/// 比泰拉的 2D 窗口大得多，所以数值不能直接照搬，按实际生成规模调。
public final class ModMiniBiomes {
    private ModMiniBiomes() {}

    /// 强制触发本类的静态初始化，完成迷你群系注册。
    /// 建议与 {@link ModDynamicBiomes#init()} 一起在模组初始化阶段调用。
    public static void init() {}

    /// 花岗岩洞。
    ///
    /// 计数源是"签名方块" `NatureBlocks.GRANITE`（只有这个结构会放），所以没有噪声要压，
    /// 阈值的含义变成「窗口里要看得到洞的多大体量」——按实测标定：
    ///
    /// - 洞里实测 `count = 126355`（`influence` 需要 max 一起抬，否则恒为 1.00）；
    /// - 洞水平半径约 40、窗口 ±64，所以从中心到距中心 24 格窗口都罩得住整个洞（恒定 126k），
    ///   到洞缘约剩 80%，洞外 24 格约剩 50%，洞外 45 格以上基本为 0；
    /// - 取 **60000**（约半个洞）→ 标记范围比洞略大一圈（外扩约 25 格），
    ///   而玩家自己砌的花岗岩墙（几千格）和普通地下（0）都不会触发。
    ///
    /// 想让标记更贴着洞就把阈值往 100000 抬，想更外扩就往 30000 降。
    public static final MiniBiomeType GRANITE_CAVE = MiniBiomeType
            .builder(Confluence.asResource("granite_cave"))
            .priority(500)
            .threshold(60000, 126000)
            .count(ModBlockCounters.GRANITE, 1)
            .build();

    /// 大理石洞。
    ///
    /// 天然大理石洞是**方解石**造的，而原版紫水晶洞也有方解石外壳（大洞的外壳约一两千格，
    /// 窗口里同时装下两三个就可能凑到 5000 上下），所以阈值要有余量，
    /// 否则站在紫水晶洞旁边也会被判成大理石洞。大理石洞本体是万级体量，6000 够分。
    public static final MiniBiomeType MARBLE_CAVE = MiniBiomeType
            .builder(Confluence.asResource("marble_cave"))
            .priority(600)
            .threshold(6000, 20000)
            .count(ModBlockCounters.MARBLE, 1)
            .build();

    /// 蜘蛛窝：蛛网密集处
    public static final MiniBiomeType SPIDER_NEST = MiniBiomeType
            .builder(Confluence.asResource("spider_nest"))
            .priority(400)
            .threshold(150, 600)
            .count(ModBlockCounters.COBWEB, 1)
            .build();

    /// 蜂巢：蜂巢块 + 蜂蜜
    public static final MiniBiomeType BEE_HIVE = MiniBiomeType
            .builder(Confluence.asResource("bee_hive"))
            .priority(300)
            .threshold(400, 2000)
            .count(ModBlockCounters.HIVE, 1)
            .count(ModBlockCounters.HONEY, 1)
            .build();

    /// 微光：**渐变**而不是布尔。阈值与上限用 [MiniBiome] 里的常量，方便单独调。
    public static final MiniBiomeType SHIMMER = MiniBiomeType
            .builder(Confluence.asResource("shimmer"))
            .priority(100)
            .threshold(MiniBiome.SHIMMER_COUNT_MIN, MiniBiome.SHIMMER_COUNT_MAX)
            .count(ModBlockCounters.SHIMMER, 1)
            .build();

    /// 陨石。
    ///
    /// 阈值按坑的体积推算：[MeteoriteFeature.Config#radius()] 是 23（跨 3×3 区块），
    /// 球体体积 `4/3·π·23³ ≈ 5.1 万`，其中坑心以下的部分会被挖成陨石矿，量级在万级。
    /// 所以阈值取 3000（约等于新鲜陨石坑体量的三成：站在坑内/坑边必中，
    /// 而玩家自己堆的几十上百块陨石矿不会触发），max 取 12000（坑心满强度，往外递减）。
    /// 半径从 7 改到 23 时体积涨了约 35 倍，这里的数值就是按这个比例重新标的。
    ///
    /// **优先级必须高于花岗岩洞/大理石洞**：判定窗口是 ±64 的立方体，站在陨石坑里时
    /// 邻洞（甚至紫水晶洞）的方解石也会被算进来，两边同时命中时如果洞穴的序号更小，
    /// [MiniBiome#primaryAt] 就会报成洞穴 —— 陨石坑是小而稀有的「等来的事件」，该它赢。
    public static final MiniBiomeType METEORITE = MiniBiomeType
            .builder(Confluence.asResource("meteorite"))
            .priority(250)
            .threshold(3000, 12000)
            .count(ModBlockCounters.METEORITE, 1)
            .build();

    /// 墓地：墓碑数减去向日葵数（与 {@link ModBlockCounters#isGraveyard} 同一套语义，只是改成窗口求和）
    public static final MiniBiomeType GRAVEYARD = MiniBiomeType
            .builder(Confluence.asResource("graveyard"))
            .priority(800)
            .threshold(ModBlockCounters.GRAVEYARD_THRESHOLD, 32)
            .count(ModBlockCounters.TOMB, 1)
            .offset(ModBlockCounters.SUNFLOWER, 1)
            .build();

    /// 小镇：不是方块计数，而是**查询点所在 region 内已入住的 NPC 数量**。
    ///
    /// 注意这是服务端数据（{@link NPCSpawner} 是 `IGlobalData`），所以客户端查不到 ——
    /// 客户端要表现小镇效果的话，得从服务端同步，或者让客户端自己按已存在的 NPC 实体数算。
    public static final MiniBiomeType TOWN = MiniBiomeType
            .builder(Confluence.asResource("town"))
            .priority(200)
            .threshold(NPCSpawner.TOWN_NPC_THRESHOLD, 8)
            .provider((level, pos) -> NPCSpawner.INSTANCE.getAliveNpcCount(new NPCSpawner.Region(pos), type -> true))
            .build();

    static {
        MiniBiome.register(SHIMMER);
        MiniBiome.register(TOWN);
        MiniBiome.register(BEE_HIVE);
        MiniBiome.register(SPIDER_NEST);
        MiniBiome.register(GRANITE_CAVE);
        MiniBiome.register(MARBLE_CAVE);
        MiniBiome.register(METEORITE);
        MiniBiome.register(GRAVEYARD);
    }
}
