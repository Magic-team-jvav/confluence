package com.xiaohunao.mine_team.common.init;

import com.xiaohunao.mine_team.MineTeam;
import com.xiaohunao.mine_team.common.network.TeamAttachmentSyncPayload;
import com.xiaohunao.mine_team.common.network.TeamManagerSyncPayload;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = MineTeam.MODID)
public class MTNetworkRegister {
    public static final String VERSION = "0.0.1";

    @SubscribeEvent
    public static void registerPayload(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(VERSION);
        registrar.playBidirectional(TeamAttachmentSyncPayload.TYPE, TeamAttachmentSyncPayload.STREAM_CODEC, new DirectionalPayloadHandler<>(
                TeamAttachmentSyncPayload::clientHandle,
                TeamAttachmentSyncPayload::serverHandle
        ));

        registrar.playToClient(TeamManagerSyncPayload.TYPE, TeamManagerSyncPayload.STREAM_CODEC, TeamManagerSyncPayload::clientHandle);
    }
}