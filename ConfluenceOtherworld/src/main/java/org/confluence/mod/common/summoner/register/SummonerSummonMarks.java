package org.confluence.mod.common.summoner.register;

import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.common.item.whip.FirecrackerItem;
import org.confluence.mod.common.summoner.summonMark.SummonMarkType;

public final class SummonerSummonMarks {

    public static final DeferredRegister<SummonMarkType> TYPES = SummonerRegistries.SUMMON_MARK_TYPES;

    public static final DeferredHolder<SummonMarkType, SummonMarkType> LEATHER_WHIP = TYPES.register("leather_whip", location -> new SummonMarkType(location, 1.0F, 0, 0, null, null, null, null));
    public static final DeferredHolder<SummonMarkType, SummonMarkType> SLUB_WHIP = TYPES.register("slub_whip", location -> new SummonMarkType(location, 1.0F, 0, 0, null, null, null, null));
    public static final DeferredHolder<SummonMarkType, SummonMarkType> RUBY_WHIP = TYPES.register("ruby_whip", location -> new SummonMarkType(location, 1.0F, 0, 0, null, null, null, null));
    public static final DeferredHolder<SummonMarkType, SummonMarkType> AMBER_WHIP = TYPES.register("amber_whip", location -> new SummonMarkType(location, 1.0F, 0, 0, null, null, null, null));
    public static final DeferredHolder<SummonMarkType, SummonMarkType> TOPAZ_WHIP = TYPES.register("topaz_whip", location -> new SummonMarkType(location, 1.0F, 0, 0, null, null, null, null));
    public static final DeferredHolder<SummonMarkType, SummonMarkType> JADE_WHIP = TYPES.register("jade_whip", location -> new SummonMarkType(location, 1.0F, 0, 0, null, null, null, null));
    public static final DeferredHolder<SummonMarkType, SummonMarkType> DIAMOND_WHIP = TYPES.register("diamond_whip", location -> new SummonMarkType(location, 1.0F, 0, 0, null, null, null, null));
    public static final DeferredHolder<SummonMarkType, SummonMarkType> SAPPHIRE_WHIP = TYPES.register("sapphire_whip", location -> new SummonMarkType(location, 1.0F, 0, 0, null, null, null, null));
    public static final DeferredHolder<SummonMarkType, SummonMarkType> AMETHYST_WHIP = TYPES.register("amethyst_whip", location -> new SummonMarkType(location, 1.0F, 0, 0, null, null, null, null));
    public static final DeferredHolder<SummonMarkType, SummonMarkType> SWAMP_WHIP = TYPES.register("swamp_whip", location -> new SummonMarkType(location, 2.0F, 0, 0, null, null, null, null));
    public static final DeferredHolder<SummonMarkType, SummonMarkType> SNAPTHORN = TYPES.register("snapthorn", location -> new SummonMarkType(location, 3.0F, 0, 0, null, null, null, null));
    public static final DeferredHolder<SummonMarkType, SummonMarkType> SPINAL_TAP = TYPES.register("spinal_tap", location -> new SummonMarkType(location, 4.0F, 0, 0, null, null, null, null));
    public static final DeferredHolder<SummonMarkType, SummonMarkType> FIRECRACKER = TYPES.register("firecracker", location -> new SummonMarkType(location,
            0,
            0,
            0,
            null,
            (tracker, markInstance, target, source, damage) -> {
                if (!markInstance.isUsed() && damage > 0.0F) {
                    markInstance.setUsed(true);
                    FirecrackerItem.explode(source.getAttachmentEntity().getOwner(), target, target, damage, source.getArmorPenetration());
                    return damage * 2.75F;
                }
                return damage;
            },
            null,
            null));

    /// 通过 `SummonerRegistries.register(eventBus)` 挂上模组事件总线，所以这里**不能**再 `register(eventBus)`
    public static void init() {
    }
}
