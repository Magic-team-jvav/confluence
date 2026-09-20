package org.confluence.mod.common.summoner.register;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.minecraft.world.entity.player.Player;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.item.whip.FirecrackerItem;
import org.confluence.mod.common.summoner.summonMark.SummonMarkType;
import org.confluence.mod.common.summoner.summonMark.SummonMarkTypeBuild;

public final class SummonerSummonMarks {

    public static final DeferredRegister<SummonMarkType> TYPES = DeferredRegister.create(SummonerRegistries.SUMMON_MARK_TYPE_KEY, Confluence.MODID);

    public static final RegistryObject<SummonMarkType> BASE = TYPES.register("base", () -> new SummonMarkTypeBuild(Confluence.asResource("base")).damage(1.0F).criticalRate(0.0F).armorPierce(0.0F).build());

    public static final RegistryObject<SummonMarkType> LEATHER_WHIP = register("leather_whip", 1.0F);
    public static final RegistryObject<SummonMarkType> SLUB_WHIP = register("slub_whip", 1.0F);
    public static final RegistryObject<SummonMarkType> RUBY_WHIP = register("ruby_whip", 1.0F);
    public static final RegistryObject<SummonMarkType> AMBER_WHIP = register("amber_whip", 1.0F);
    public static final RegistryObject<SummonMarkType> TOPAZ_WHIP = register("topaz_whip", 1.0F);
    public static final RegistryObject<SummonMarkType> JADE_WHIP = register("jade_whip", 1.0F);
    public static final RegistryObject<SummonMarkType> DIAMOND_WHIP = register("diamond_whip", 1.0F);
    public static final RegistryObject<SummonMarkType> SAPPHIRE_WHIP = register("sapphire_whip", 1.0F);
    public static final RegistryObject<SummonMarkType> AMETHYST_WHIP = register("amethyst_whip", 1.0F);
    public static final RegistryObject<SummonMarkType> SWAMP_WHIP = register("swamp_whip", 2.0F);
    public static final RegistryObject<SummonMarkType> SNAPTHORN = register("snapthorn", 3.0F);
    public static final RegistryObject<SummonMarkType> SPINAL_TAP = register("spinal_tap", 4.0F);
    public static final RegistryObject<SummonMarkType> FIRECRACKER = TYPES.register("firecracker", () -> new SummonMarkTypeBuild(Confluence.asResource("firecracker"))
            .damage(0.0F)
            .armorPierce(0.0F)
            .criticalRate(0.0F)
            .onDamagePre((tracker, target, source, damage) -> {
                if (!tracker.isUsed() && damage > 0.0F) {
                    tracker.setUsed(true);
                    FirecrackerItem.explode(source.getAttachmentEntity().getOwner(), target, target, damage, source.getArmorPenetration());
                    return damage * 2.75F;
                }
                return damage;
            })
            .build());

    private static RegistryObject<SummonMarkType> register(String name, float damage) {
        return TYPES.register(name, () -> new SummonMarkTypeBuild(Confluence.asResource(name)).damage(damage).armorPierce(0.0F).criticalRate(0.0F).build());
    }

    public static void register(IEventBus eventBus) {
        TYPES.register(eventBus);
    }
}
