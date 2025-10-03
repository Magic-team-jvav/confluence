package org.confluence.mod.integration.terra_entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import org.confluence.mod.Confluence;
import org.confluence.mod.integration.terra_entity.init.ModEffectStrategies;
import org.confluence.mod.integration.terra_entity.init.ModTradeProviders;
import org.confluence.terraentity.api.event.WhipRegisterModifyEvent;
import org.confluence.terraentity.init.entity.TEBossEntities;
import org.confluence.terraentity.init.entity.TEMonsterEntities;

@EventBusSubscriber(modid = Confluence.MODID, bus = EventBusSubscriber.Bus.MOD)
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

    public static void registerArmorPenetration(ArmorPenetrationRegistration registration) {
        registration.register(TEBossEntities.QUEEN_BEE.get(), 2);
        registration.register(TEBossEntities.SKELETRON.get(), 4);
        registration.register(TEBossEntities.SKELETRON_HAND.get(), 4);
        // 肉后
        registration.register(TEMonsterEntities.PIXIE.get(), 8);
        registration.register(TEMonsterEntities.WYVERN.get(), 8);
        registration.register(TEMonsterEntities.WRAITH.get(), 8);
        registration.register(TEMonsterEntities.POSSESS_ARMOR.get(), 8);
    }

    @FunctionalInterface
    public interface ArmorPenetrationRegistration {
        void register(EntityType<? extends LivingEntity> type, double value);
    }
}
