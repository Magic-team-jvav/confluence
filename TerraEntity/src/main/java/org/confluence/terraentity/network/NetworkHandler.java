package org.confluence.terraentity.network;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.confluence.terraentity.network.c2s.*;
import org.confluence.terraentity.network.s2c.*;

public final class NetworkHandler {

    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1.0.0");
        registrar.playToClient(SyncCameraShakePacket.TYPE, SyncCameraShakePacket.STREAM_CODEC, SyncCameraShakePacket::handle);
        registrar.playToClient(SyncSummonPacket.TYPE, SyncSummonPacket.STREAM_CODEC, SyncSummonPacket::handle);
        registrar.playToClient(SyncBossEventHealthPacket.TYPE, SyncBossEventHealthPacket.STREAM_CODEC, SyncBossEventHealthPacket::handle);
        registrar.playToClient(SyncNPCTradesPacketS2C.TYPE, SyncNPCTradesPacketS2C.STREAM_CODEC, SyncNPCTradesPacketS2C::handle);
        registrar.playToClient(SyncDataS2C.TYPE, SyncDataS2C.STREAM_CODEC, SyncDataS2C::handle);
        registrar.playToClient(UpdateNPCTradePacket.TYPE, UpdateNPCTradePacket.STREAM_CODEC, UpdateNPCTradePacket::handle);
        registrar.playToClient(ChesterAttachmentPacketS2C.TYPE, ChesterAttachmentPacketS2C.STREAM_CODEC, ChesterAttachmentPacketS2C::handle);
        registrar.playToClient(ClientBoundEventPacket.TYPE, ClientBoundEventPacket.STREAM_CODEC, ClientBoundEventPacket::handle);
        registrar.playToClient(SummonBossPacket.TYPE, SummonBossPacket.STREAM_CODEC, SummonBossPacket::handle);
        registrar.playToClient(SyncLevelNamePacketS2C.TYPE, SyncLevelNamePacketS2C.STREAM_CODEC, SyncLevelNamePacketS2C::handle);
        registrar.playToClient(SetAnglerDialogPacketS2C.TYPE, SetAnglerDialogPacketS2C.STREAM_CODEC, SetAnglerDialogPacketS2C::handle);
        registrar.playToClient(SyncWallOfFleshTargetPacket.TYPE, SyncWallOfFleshTargetPacket.STREAM_CODEC, SyncWallOfFleshTargetPacket::handle);
        registrar.playToClient(SyncWallOfFleshPositionsPacket.TYPE, SyncWallOfFleshPositionsPacket.STREAM_CODEC, SyncWallOfFleshPositionsPacket::handle);
        registrar.playToClient(UpdateBlackboardPacket.TYPE, UpdateBlackboardPacket.STREAM_CODEC, UpdateBlackboardPacket::handle);

        registrar.playToServer(ServerBoundVehicleExtensionPacket.TYPE, ServerBoundVehicleExtensionPacket.STREAM_CODEC, ServerBoundVehicleExtensionPacket::handle);
        registrar.playToServer(ServerBoundHousePacket.TYPE, ServerBoundHousePacket.STREAM_CODEC, ServerBoundHousePacket::handle);
        registrar.playToServer(NPCShopPacket.TYPE, NPCShopPacket.STREAM_CODEC, NPCShopPacket::handle);
        registrar.playToServer(ServerBoundEventPacket.TYPE, ServerBoundEventPacket.STREAM_CODEC, ServerBoundEventPacket::handle);
        registrar.playToServer(SetDebugModePacket.TYPE, SetDebugModePacket.STREAM_CODEC, SetDebugModePacket::handle);

    }
}
