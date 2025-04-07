package org.confluence.mod.integration.terra_entity;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import org.confluence.mod.Confluence;
import org.confluence.terraentity.api.event.WhipRegisterModifyEvent;

@EventBusSubscriber(modid = Confluence.MODID, bus = EventBusSubscriber.Bus.MOD)
public class TEEvents {

    @SubscribeEvent
    public static void onRegisterWhips(WhipRegisterModifyEvent event){
        // 子模块的鞭子数值比本体低
        event.setDamage(event.getDamage() / WhipRegisterModifyEvent.damageFactor);
    }

    public static void init(IEventBus eventBus){
        eventBus.addListener(TEEvents::onRegisterWhips);

    }
}
