package org.confluence.mod.integration.terra_entity;

import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.eventbus.api.IEventBus;
import org.confluence.lib.common.LibAttributes;
import org.confluence.mod.integration.terra_entity.init.AdditionalChesterTypes;
import org.confluence.mod.integration.terra_entity.init.ModEffectStrategies;
import org.confluence.mod.integration.terra_entity.init.ModTradeProviders;
import org.confluence.terraentity.api.event.WhipRegisterModifyEvent;
import org.confluence.terraentity.init.entity.TEBossEntities;
import org.confluence.terraentity.init.entity.TEMonsterEntities;

public final class TEEvents {
    public static void onRegisterWhips(WhipRegisterModifyEvent event) {
        // 子模块的鞭子数值比本体低
        event.setDamage(event.getDamage() / WhipRegisterModifyEvent.damageFactor);
    }

    public static void register(IEventBus eventBus) {
        ModTradeProviders.TYPES.register(eventBus);
        ModEffectStrategies.EFFECT_STRATEGY.register(eventBus);
        AdditionalChesterTypes.register(eventBus);
    }

    public static void modifyAttributes(EntityAttributeModificationEvent event) {
        new AttributeRegistration(event)
                .set(LibAttributes.getArmorPenetration())
                .register(TEBossEntities.QUEEN_BEE.get(), 2)
                .register(TEBossEntities.SKELETRON.get(), 4)
                .register(TEBossEntities.SKELETRON_HAND.get(), 4)
                .register(TEBossEntities.HILL_OF_FLESH.get(), 4)
                .register(TEBossEntities.WALL_OF_FLESH.get(), 6)
                // 肉后
                .register(TEMonsterEntities.PIXIE.get(), 8)
                .register(TEMonsterEntities.WYVERN.get(), 8)
                .register(TEMonsterEntities.WRAITH.get(), 8)
                .register(TEMonsterEntities.POSSESS_ARMOR.get(), 8)
                .register(TEMonsterEntities.CORRUPT_SLIME.get(), 8)
                .register(TEMonsterEntities.LUMINOUS_SLIME.get(), 8)
                .register(TEMonsterEntities.CRIMSLIME.get(), 8)
                .register(TEMonsterEntities.WOODEN_MIMIC.get(), 8)
                .register(TEMonsterEntities.GOLDEN_MIMIC.get(), 8)
                .register(TEMonsterEntities.SHADOW_MIMIC.get(), 8)
                .register(TEMonsterEntities.ICE_MIMIC.get(), 8)
                .register(TEMonsterEntities.CRIMSON_MIMIC.get(), 8)
                .register(TEMonsterEntities.CORRUPT_MIMIC.get(), 8)
                .register(TEMonsterEntities.HALLOWED_MIMIC.get(), 8)
                .register(TEMonsterEntities.JUNGLE_MIMIC.get(), 8)

                .register(TEMonsterEntities.MUMMY.get(), 8)
                .register(TEMonsterEntities.DARK_MUMMY.get(), 8)
                .register(TEMonsterEntities.BLOOD_MUMMY.get(), 8)
                .register(TEMonsterEntities.LIGHT_MUMMY.get(), 8)
                .register(TEMonsterEntities.DARK_LAMIA.get(), 8)
                .register(TEMonsterEntities.LIGHT_LAMIA.get(), 8)
                .register(TEMonsterEntities.DERPLING.get(), 8)
                .register(TEMonsterEntities.HERPLING.get(), 8)
                .register(TEMonsterEntities.GHOUL.get(), 8)
                .register(TEMonsterEntities.VILE_GHOUL.get(), 8)
                .register(TEMonsterEntities.TAINTED_GHOUL.get(), 8)
                .register(TEMonsterEntities.DREAMER_GHOUL.get(), 8)
                .register(TEMonsterEntities.SAND_POACHER.get(), 8)

                .register(TEBossEntities.RETINAZER.get(), 8)
                .register(TEBossEntities.SPAZMATISM.get(), 8)
                .register(TEBossEntities.PLANTERA.get(), 8)
                .register(TEBossEntities.PLANTERA_TENTACLE.get(), 8)
                .register(TEBossEntities.PLANTERA_HOOK.get(), 8)


                .set(Attributes.ARMOR_TOUGHNESS)
                .register(TEMonsterEntities.PIXIE.get(), 2)
                .register(TEMonsterEntities.WYVERN.get(), 2)
                .register(TEMonsterEntities.CORRUPT_SLIME.get(), 2)
                .register(TEMonsterEntities.LUMINOUS_SLIME.get(), 2)
                .register(TEMonsterEntities.CRIMSLIME.get(), 2)
                .register(TEMonsterEntities.WOODEN_MIMIC.get(), 2)
                .register(TEMonsterEntities.GOLDEN_MIMIC.get(), 2)
                .register(TEMonsterEntities.SHADOW_MIMIC.get(), 2)
                .register(TEMonsterEntities.ICE_MIMIC.get(), 2)
                .register(TEMonsterEntities.CRIMSON_MIMIC.get(), 2)
                .register(TEMonsterEntities.CORRUPT_MIMIC.get(), 2)
                .register(TEMonsterEntities.HALLOWED_MIMIC.get(), 2)
                .register(TEMonsterEntities.JUNGLE_MIMIC.get(), 2)

                .register(TEMonsterEntities.MUMMY.get(), 2)
                .register(TEMonsterEntities.DARK_MUMMY.get(), 2)
                .register(TEMonsterEntities.BLOOD_MUMMY.get(), 2)
                .register(TEMonsterEntities.LIGHT_MUMMY.get(), 2)
                .register(TEMonsterEntities.DARK_LAMIA.get(), 2)
                .register(TEMonsterEntities.LIGHT_LAMIA.get(), 2)
                .register(TEMonsterEntities.DERPLING.get(), 2)
                .register(TEMonsterEntities.HERPLING.get(), 2)
                .register(TEMonsterEntities.GHOUL.get(), 2)
                .register(TEMonsterEntities.VILE_GHOUL.get(), 2)
                .register(TEMonsterEntities.TAINTED_GHOUL.get(), 2)
                .register(TEMonsterEntities.DREAMER_GHOUL.get(), 2)
                .register(TEMonsterEntities.SAND_POACHER.get(), 2)

                .register(TEBossEntities.RETINAZER.get(), 2)
                .register(TEBossEntities.SPAZMATISM.get(), 2)
                .register(TEBossEntities.PLANTERA.get(), 2)
                .register(TEBossEntities.PLANTERA_TENTACLE.get(), 2)
                .register(TEBossEntities.PLANTERA_HOOK.get(), 2)
        ;
    }
}
