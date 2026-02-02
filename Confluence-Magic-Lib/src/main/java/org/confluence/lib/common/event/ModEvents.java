package org.confluence.lib.common.event;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.data.IdFixer;
import org.confluence.lib.common.fluid.FluidBuilder;
import org.confluence.lib.network.AttackDamagePacketS2C;
import org.confluence.lib.network.SetEntityDataPacketS2C;

@EventBusSubscriber(modid = ConfluenceMagicLib.LIB_ID)
public final class ModEvents {
    @SubscribeEvent
    public static void register(RegisterEvent event) {
        FluidBuilder.register(event);
    }

    @SubscribeEvent
    public static void registerPayloadHandlers(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToClient(SetEntityDataPacketS2C.TYPE, SetEntityDataPacketS2C.STREAM_CODEC, SetEntityDataPacketS2C::handle);
        registrar.playToClient(AttackDamagePacketS2C.TYPE, AttackDamagePacketS2C.STREAM_CODEC, AttackDamagePacketS2C::handle);
    }

    @SubscribeEvent
    public static void fmlLoadComplete(FMLLoadCompleteEvent event) {
        event.enqueueWork(IdFixer::postRegisterEvents);
    }
}
