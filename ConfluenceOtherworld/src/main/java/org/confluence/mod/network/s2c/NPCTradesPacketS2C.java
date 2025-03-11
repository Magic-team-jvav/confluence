package org.confluence.mod.network.s2c;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.npc.NPCTrades;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public record NPCTradesPacketS2C(Map<ResourceLocation, NPCTrades> tradesMap) implements CustomPacketPayload {
    public static final Type<NPCTradesPacketS2C> TYPE = new Type<>(Confluence.asResource("npc_trades_packet_s2c"));
    public static final StreamCodec<ByteBuf, NPCTradesPacketS2C> STREAM_CODEC = NPCTrades.MAP_STREAM_CODEC.map(NPCTradesPacketS2C::new, NPCTradesPacketS2C::tradesMap);

    @Override
    public @NotNull Type<NPCTradesPacketS2C> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            NPCTrades.reset(tradesMap);
        }).exceptionally(e -> null);
    }

    public static void sync(ServerPlayer player){
        PacketDistributor.sendToPlayer(player, new NPCTradesPacketS2C(NPCTrades.getTradeMap()));
    }
}
