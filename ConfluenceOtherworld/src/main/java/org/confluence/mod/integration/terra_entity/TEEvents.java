package org.confluence.mod.integration.terra_entity;

import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import org.confluence.mod.Confluence;
import org.confluence.mod.integration.terra_entity.init.ModEffectStrategies;
import org.confluence.mod.integration.terra_entity.init.ModTradeProviders;
import org.confluence.terra_curio.common.init.TCAttributes;
import org.confluence.terraentity.api.event.WhipRegisterModifyEvent;
import org.confluence.terraentity.init.entity.TEBossEntities;
import org.confluence.terraentity.init.entity.TEMonsterEntities;

@EventBusSubscriber(modid = Confluence.MODID)
public final class TEEvents {
    @SubscribeEvent
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
                .set(TCAttributes.ARMOR_PENETRATION)
                .register(TEBossEntities.QUEEN_BEE.get(), 2)
                .register(TEBossEntities.SKELETRON.get(), 4)
                .register(TEBossEntities.SKELETRON_HAND.get(), 4)
                // 肉后
                .register(TEMonsterEntities.PIXIE.get(), 8)
                .register(TEMonsterEntities.WYVERN.get(), 8)
                .register(TEMonsterEntities.WRAITH.get(), 8)
                .register(TEMonsterEntities.POSSESS_ARMOR.get(), 8)
                .register(TEMonsterEntities.CORRUPT_SLIME.get(), 8)
                .register(TEMonsterEntities.LUMINOUS_SLIME.get(), 8)
                .register(TEMonsterEntities.CRIMSLIME.get(), 8)


                .set(Attributes.ARMOR_TOUGHNESS)
                .register(TEMonsterEntities.PIXIE.get(), 2)
                .register(TEMonsterEntities.WYVERN.get(), 2)
                .register(TEMonsterEntities.WRAITH.get(), 2)
                .register(TEMonsterEntities.POSSESS_ARMOR.get(), 2)
                .register(TEMonsterEntities.CORRUPT_SLIME.get(), 2)
                .register(TEMonsterEntities.LUMINOUS_SLIME.get(), 2)
                .register(TEMonsterEntities.CRIMSLIME.get(), 2)
        ;
    }
}
