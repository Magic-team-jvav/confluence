package org.confluence.mod.network.c2s;

import io.netty.buffer.ByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.handler.WormholeHandler;
import org.mesdag.portlib.network.IPortPacket;
import org.mesdag.portlib.network.PortPacketDistributor;
import org.mesdag.portlib.network.codec.PortStreamCodec;

/// 请求可通过虫洞药水抵达的在线队友列表。
public record WormholeRequestPlayerDataPacketC2S() implements IPortPacket.C2S {
    public static final ResourceLocation ID = Confluence.asResource("wormhole_request_player_data");
    public static final PortStreamCodec<ByteBuf, WormholeRequestPlayerDataPacketC2S> STREAM_CODEC =
            PortStreamCodec.unit(new WormholeRequestPlayerDataPacketC2S());

    @Override
    public ResourceLocation identifier() {
        return ID;
    }

    @Override
    public void work(ServerPlayer player) {
        WormholeHandler.work(this, player);
    }

    public static void sendToServer() {
        PortPacketDistributor.sendToServer(new WormholeRequestPlayerDataPacketC2S());
    }
}
