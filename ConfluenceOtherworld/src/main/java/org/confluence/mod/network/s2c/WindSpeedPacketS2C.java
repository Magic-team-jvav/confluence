package org.confluence.mod.network.s2c;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.confluence.mod.client.handler.WeatherHandler;
import org.confluence.mod.network.IPacket;

public record WindSpeedPacketS2C(float x, float z) implements IPacketS2C {
    public static final Type<WindSpeedPacketS2C> TYPE = IPacket.createType("wind_speed");
    public static final StreamCodec<ByteBuf, WindSpeedPacketS2C> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, WindSpeedPacketS2C::x,
            ByteBufCodecs.FLOAT, WindSpeedPacketS2C::z,
            WindSpeedPacketS2C::new
    );

    @Override
    public Type<WindSpeedPacketS2C> type() {
        return TYPE;
    }

    @Override
    public void work(Player player) {
        WeatherHandler.handleWindSpeed(this);
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
