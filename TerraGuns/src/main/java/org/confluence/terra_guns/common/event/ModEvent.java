package org.confluence.terra_guns.common.event;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.confluence.lib.event.NameFixRegisterEvent;
import org.confluence.terra_guns.TerraGuns;
import org.confluence.terra_guns.common.init.TGGunSounds;
import org.confluence.terra_guns.common.init.TGTrailColors;
import org.confluence.terra_guns.network.c2s.ShootPacketC2S;

@EventBusSubscriber(modid = TerraGuns.MODID)
public class ModEvent {
    @SubscribeEvent
    public static void registerNetWork(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(ShootPacketC2S.TYPE, ShootPacketC2S.STREAM_CODEC, ShootPacketC2S::handle);
    }

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            TGGunSounds.init();
            TGTrailColors.init();
        });
    }

    @SubscribeEvent
    public static void itemNameFixRegister(NameFixRegisterEvent.Item event) {
        // 1.1.4 -> 1.1.5
        event.register("terra_guns:blowpipe", "terra_guns:blowgun");
    }
}
