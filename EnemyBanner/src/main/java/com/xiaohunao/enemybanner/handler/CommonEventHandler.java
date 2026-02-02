package com.xiaohunao.enemybanner.handler;

import com.xiaohunao.enemybanner.EnemyBanner;
import com.xiaohunao.enemybanner.gui.BannerBoxScreen;
import com.xiaohunao.enemybanner.gui.Menus;
import com.xiaohunao.enemybanner.payloads.ClientPayloadHandler;
import com.xiaohunao.enemybanner.payloads.PlayerBannerCountPayload;
import com.xiaohunao.enemybanner.payloads.ServerPayloadHandler;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = EnemyBanner.MODID)
public class CommonEventHandler {

    @SubscribeEvent
    public static void  registerScreens(RegisterMenuScreensEvent event){
        event.register(Menus.BANNER_BOX_MENU.get(), BannerBoxScreen::new);
    }

    @SubscribeEvent
    public static void registerPayloadHandler(RegisterPayloadHandlersEvent event){
        final PayloadRegistrar registrar = event.registrar("1");
        registrar.playBidirectional(
                PlayerBannerCountPayload.TYPE,
                PlayerBannerCountPayload.STREAM_CODEC,
                new DirectionalPayloadHandler<>(ClientPayloadHandler::handle, ServerPayloadHandler::handle)
        );
    }
}
