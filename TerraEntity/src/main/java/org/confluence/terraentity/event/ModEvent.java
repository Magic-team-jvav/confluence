package org.confluence.terraentity.event;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.confluence.terraentity.Config;
import org.confluence.terraentity.TerraEntity;

@EventBusSubscriber(modid = TerraEntity.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ModEvent {

    @SubscribeEvent
    public static void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            Config.init();

        });
    }
}
