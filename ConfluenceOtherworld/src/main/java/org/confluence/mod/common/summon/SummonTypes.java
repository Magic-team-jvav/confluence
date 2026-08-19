package org.confluence.mod.common.summon;

import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.summon.dragon.StardustDragonSummon;
import org.confluence.mod.common.summon.flying.FinchSummon;
import org.confluence.mod.common.summon.flying.HornetSummon;
import org.confluence.mod.common.summon.flying.ImpSummon;
import org.confluence.mod.common.summon.flying.SculkWispSummon;
import org.confluence.mod.common.summon.ground.IronGolemSummon;
import org.confluence.mod.common.summon.ground.SnowFlinxSummon;
import org.confluence.mod.common.summon.slime.SlimeSummon;
import org.confluence.mod.common.summon.sword.SummonSword;
import org.confluence.mod.common.summon.terraprisma.TerraprismaSummon;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

/// 召唤物运行类型表，对应 Servantry 的 AttachmentEntityType 注册层。
public final class SummonTypes {
    private static final Map<ResourceLocation, SummonType> TYPES = new LinkedHashMap<>();

    public static final SummonType FINCH = register("finch_baby", FinchSummon::new);
    public static final SummonType IRON_GOLEM = register("i_32_iron_golem", IronGolemSummon::new);
    public static final SummonType SLIME = register("slime_baby", SlimeSummon::new);
    public static final SummonType HORNET = register("hornet_baby", HornetSummon::new);
    public static final SummonType SCULK_WISP = register("sculk_wisp", SculkWispSummon::new);
    public static final SummonType IMP = register("summon_imp", ImpSummon::new);
    public static final SummonType SNOW_FLINX = register("summon_snow_flinx", SnowFlinxSummon::new);
    public static final SummonType SUMMON_WOODEN_SWORD = registerSword(SummonSword.Kind.WOODEN);
    public static final SummonType SUMMON_STONE_SWORD = registerSword(SummonSword.Kind.STONE);
    public static final SummonType SUMMON_IRON_SWORD = registerSword(SummonSword.Kind.IRON);
    public static final SummonType SUMMON_GOLDEN_SWORD = registerSword(SummonSword.Kind.GOLDEN);
    public static final SummonType SUMMON_DIAMOND_SWORD = registerSword(SummonSword.Kind.DIAMOND);
    public static final SummonType SUMMON_NETHERITE_SWORD = registerSword(SummonSword.Kind.NETHERITE);
    public static final SummonType TERRAPRISMA = register("terraprisma", TerraprismaSummon::new);
    public static final SummonType STARDUST_DRAGON = register("stardust_dragon", StardustDragonSummon::new);

    private SummonTypes() {}

    public static @Nullable SummonType byId(ResourceLocation id) {
        return TYPES.get(id);
    }

    private static SummonType registerSword(SummonSword.Kind kind) {
        return register(kind.type(), (owner, slotCost, stats, pose) -> new SummonSword(owner, slotCost, stats, pose, kind));
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
