package org.confluence.mod.integration.heaven_destiny_moment;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import org.confluence.mod.Confluence;
import org.confluence.mod.integration.heaven_destiny_moment.init.ModMapCodecRegisters;
import org.confluence.mod.integration.terra_entity.AdditionalChesterTypes;
import org.confluence.mod.integration.terra_entity.init.ModTradeProviders;


public final class HDMEvents {
    public static void register(IEventBus eventBus) {
        ModMapCodecRegisters.CONDITION_CODEC.register(eventBus);
    }
}