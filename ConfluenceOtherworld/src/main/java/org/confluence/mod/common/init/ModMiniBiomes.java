package org.confluence.mod.common.init;

import net.minecraft.server.level.ServerLevel;
import org.confluence.lib.common.worldgen.biome.MiniBiome;
import org.confluence.lib.common.worldgen.biome.MiniBiomeType;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.data.saved.HouseHandler;
import org.confluence.mod.common.data.spawner.NPCSpawner;
import org.confluence.mod.util.OverworldUtils;

/// 多个环境标记可以同时成立；优先级只服务于需要唯一表现的消费方，不用于抹掉刷怪条件。
/// 范围单位为方块，计数以 4 格单元裁切；以下范围是初始参数，需按实际洞穴和人工场景标定。
public final class ModMiniBiomes {
    private ModMiniBiomes() {}

    public static void init() {}

    /// 花岗岩洞按专属材料计数，保留人工构建的可能。
    public static final MiniBiomeType GRANITE_CAVE = MiniBiomeType
            .builder(Confluence.asResource("granite_cave"))
            .priority(500)
            .window(48, 32)
            .condition((level, pos) -> pos.getY() < OverworldUtils.getSurfaceY())
            .threshold(60000, 126000)
            .count(ModBlockCounters.GRANITE, 1)
            .build();

    /// 方解石也存在于紫水晶洞，阈值仍需区分零散外壳与大理石区域。
    public static final MiniBiomeType MARBLE_CAVE = MiniBiomeType
            .builder(Confluence.asResource("marble_cave"))
            .priority(600)
            .window(32, 24)
            .condition((level, pos) -> pos.getY() < OverworldUtils.getSurfaceY())
            .threshold(6000, 20000)
            .count(ModBlockCounters.MARBLE, 1)
            .build();

    public static final MiniBiomeType SPIDER_NEST = MiniBiomeType
            .builder(Confluence.asResource("spider_nest"))
            .priority(400)
            .window(16, 12)
            .condition((level, pos) -> pos.getY() < OverworldUtils.getSurfaceY())
            .threshold(150, 600)
            .count(ModBlockCounters.COBWEB, 1)
            .build();

    public static final MiniBiomeType BEE_HIVE = MiniBiomeType
            .builder(Confluence.asResource("bee_hive"))
            .priority(300)
            .window(24, 16)
            .threshold(400, 2000)
            .count(ModBlockCounters.HIVE, 1)
            .count(ModBlockCounters.HONEY, 1)
            .build();

    public static final MiniBiomeType SHIMMER = MiniBiomeType
            .builder(Confluence.asResource("shimmer"))
            .priority(100)
            .window(32, 16)
            .threshold(MiniBiome.SHIMMER_COUNT_MIN, MiniBiome.SHIMMER_COUNT_MAX)
            .count(ModBlockCounters.SHIMMER, 1)
            .build();

    /// 陨石存在与音乐优先级分开；挖除矿物后不再满足计数即退出。
    public static final MiniBiomeType METEORITE = MiniBiomeType
            .builder(Confluence.asResource("meteorite"))
            .priority(250)
            .window(32, 24)
            .threshold(3000, 12000)
            .count(ModBlockCounters.METEORITE, 1)
            .build();

    public static final MiniBiomeType GRAVEYARD = MiniBiomeType
            .builder(Confluence.asResource("graveyard"))
            .priority(800)
            .window(16, 12)
            .threshold(ModBlockCounters.GRAVEYARD_THRESHOLD, 32)
            .count(ModBlockCounters.TOMB, 1)
            .offset(ModBlockCounters.SUNFLOWER, 1)
            .build();

    /// 入住数据由服务端提供，不从客户端读取集成服务器的静态存档对象。
    public static final MiniBiomeType TOWN = MiniBiomeType
            .builder(Confluence.asResource("town"))
            .priority(200)
            .window(64, 32)
            .condition((level, pos) -> level instanceof ServerLevel)
            .threshold(NPCSpawner.TOWN_NPC_THRESHOLD, 8)
            .provider((level, pos) -> level instanceof ServerLevel serverLevel
                    ? HouseHandler.INSTANCE.countNearbyResidents(serverLevel, pos, 64, 32) : 0)
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
