package org.confluence.mod.network.s2c;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.handler.WeatherHandler;

public record WindSpeedPacketS2C(float x, float z) implements CustomPacketPayload {
    public static final Type<WindSpeedPacketS2C> TYPE = new Type<>(Confluence.asResource("wind_speed"));
    public static final StreamCodec<ByteBuf, WindSpeedPacketS2C> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, WindSpeedPacketS2C::x,
            ByteBufCodecs.FLOAT, WindSpeedPacketS2C::z,
            WindSpeedPacketS2C::new
    );

    @Override
    public Type<WindSpeedPacketS2C> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().isLocalPlayer()) {
                WeatherHandler.handleWindSpeed(this);
            }
        }).exceptionally(e -> {
            context.disconnect(Component.translatable("neoforge.network.invalid_flow", e.getMessage()));
            return null;
        });
    }

    public static void sendToAll(float x, float z) {
        if (ServerLifecycleHooks.getCurrentServer() != null) {
            PacketDistributor.sendToAllPlayers(new WindSpeedPacketS2C(x, z));
        }
    }

    public static void sendToClient(ServerPlayer serverPlayer, float windSpeedX, float windSpeedZ) {
        PacketDistributor.sendToPlayer(serverPlayer, new WindSpeedPacketS2C(windSpeedX, windSpeedZ));
    }
}
