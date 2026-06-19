package org.confluence.mod.network.c2s;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.lib.network.IPacketC2S;
import org.confluence.lib.network.IPacketS2C;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.handler.WormholeHandler;

public record WormholeRequestPlayerDataPacketC2S() implements IPacketC2S {
    public static final Type<WormholeRequestPlayerDataPacketC2S> TYPE = Confluence.createType("wormhole_request_player_data");
    public static final StreamCodec<ByteBuf, WormholeRequestPlayerDataPacketC2S> STREAM_CODEC = StreamCodec.unit(new WormholeRequestPlayerDataPacketC2S());

    @Override
    public Type<WormholeRequestPlayerDataPacketC2S> type() {
        return TYPE;
    }

    public static void sendToServer() {
        PacketDistributor.sendToServer(new WormholeRequestPlayerDataPacketC2S());
    }

    @Override
    public void work(ServerPlayer player) {
        WormholeHandler.work(this, player);
    }
}
