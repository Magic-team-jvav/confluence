package org.confluence.mod.common.summoner.register;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.item.whip.FirecrackerItem;
import org.confluence.mod.common.summoner.summonMark.SummonMarkType;
import org.mesdag.portlib.registries.PortRegisterHandler;
import org.mesdag.portlib.registries.PortRegistration;
import org.mesdag.portlib.registries.PortRegistryEntry;

public final class SummonerSummonMarks {

    public static final PortRegistration<SummonMarkType> TYPES = PortRegisterHandler.create(Confluence.MODID, SummonerRegistries.SUMMON_MARK_TYPE_KEY);

    public static final PortRegistryEntry<SummonMarkType, SummonMarkType> LEATHER_WHIP = TYPES.register("leather_whip", location -> new SummonMarkType(location, 1.0F, 0, 0, null, null, null, null));
    public static final PortRegistryEntry<SummonMarkType, SummonMarkType> SLUB_WHIP = TYPES.register("slub_whip", location -> new SummonMarkType(location, 1.0F, 0, 0, null, null, null, null));
    public static final PortRegistryEntry<SummonMarkType, SummonMarkType> RUBY_WHIP = TYPES.register("ruby_whip", location -> new SummonMarkType(location, 1.0F, 0, 0, null, null, null, null));
    public static final PortRegistryEntry<SummonMarkType, SummonMarkType> AMBER_WHIP = TYPES.register("amber_whip", location -> new SummonMarkType(location, 1.0F, 0, 0, null, null, null, null));
    public static final PortRegistryEntry<SummonMarkType, SummonMarkType> TOPAZ_WHIP = TYPES.register("topaz_whip", location -> new SummonMarkType(location, 1.0F, 0, 0, null, null, null, null));
    public static final PortRegistryEntry<SummonMarkType, SummonMarkType> JADE_WHIP = TYPES.register("jade_whip", location -> new SummonMarkType(location, 1.0F, 0, 0, null, null, null, null));
    public static final PortRegistryEntry<SummonMarkType, SummonMarkType> DIAMOND_WHIP = TYPES.register("diamond_whip", location -> new SummonMarkType(location, 1.0F, 0, 0, null, null, null, null));
    public static final PortRegistryEntry<SummonMarkType, SummonMarkType> SAPPHIRE_WHIP = TYPES.register("sapphire_whip", location -> new SummonMarkType(location, 1.0F, 0, 0, null, null, null, null));
    public static final PortRegistryEntry<SummonMarkType, SummonMarkType> AMETHYST_WHIP = TYPES.register("amethyst_whip", location -> new SummonMarkType(location, 1.0F, 0, 0, null, null, null, null));
    public static final PortRegistryEntry<SummonMarkType, SummonMarkType> SWAMP_WHIP = TYPES.register("swamp_whip", location -> new SummonMarkType(location, 2.0F, 0, 0, null, null, null, null));
    public static final PortRegistryEntry<SummonMarkType, SummonMarkType> SNAPTHORN = TYPES.register("snapthorn", location -> new SummonMarkType(location, 3.0F, 0, 0, null, null, null, null));
    public static final PortRegistryEntry<SummonMarkType, SummonMarkType> SPINAL_TAP = TYPES.register("spinal_tap", location -> new SummonMarkType(location, 4.0F, 0, 0, null, null, null, null));
    public static final PortRegistryEntry<SummonMarkType, SummonMarkType> FIRECRACKER = TYPES.register("firecracker", location -> new SummonMarkType(location,
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

    public static void init() {
    }
}
