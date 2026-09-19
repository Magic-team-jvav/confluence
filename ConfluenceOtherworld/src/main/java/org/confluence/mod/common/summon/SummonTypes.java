package org.confluence.mod.common.summon;

import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.summon.dragon.StardustDragonSummon;
import org.confluence.mod.common.summon.ground.*;
import org.confluence.mod.common.summon.slime.SlimeSummon;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

/// 召唤物运行类型表，对应 Servantry 的 AttachmentEntityType 注册层。
public final class SummonTypes {
    private static final Map<ResourceLocation, SummonType> TYPES = new LinkedHashMap<>();

    public static final SummonType SLIME = register("slime_baby", SlimeSummon::new);
    public static final SummonType SNOW_FLINX = register("summon_snow_flinx", SnowFlinxSummon::new);
    public static final SummonType VAMPIRE_FROG = register("vampire_frog", VampireFrogSummon::new);
    public static final SummonType SPIDER = register("spider", SpiderSummon::new);
    public static final SummonType DESERT_TIGER = register("desert_tiger", DesertTigerSummon::new);
    public static final SummonType STARDUST_DRAGON = register("stardust_dragon", StardustDragonSummon::new);

    private SummonTypes() {}

    public static @Nullable SummonType byId(ResourceLocation id) {
        return TYPES.get(id);
    }


    private static SummonType register(String path, SummonFactory factory) {
        return register(Confluence.asResource(path), factory);
    }

    private static SummonType register(ResourceLocation id, SummonFactory factory) {
        SummonType type = new SummonType(id, factory);
        if (TYPES.putIfAbsent(id, type) != null) {
            throw new IllegalStateException("Duplicate summon type: " + id);
        }
        return type;
    }
}
