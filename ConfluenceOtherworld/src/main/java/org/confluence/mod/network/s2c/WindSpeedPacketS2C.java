package org.confluence.mod.network.s2c;

import io.netty.buffer.ByteBuf;
import org.mesdag.portlib.network.codec.PortByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.handler.WeatherHandler;
import org.mesdag.portlib.network.IPortPacket;
import org.mesdag.portlib.network.codec.PortStreamCodec;

public record WindSpeedPacketS2C(float x, float z) implements IPortPacket.S2C {
    public static final ResourceLocation ID = Confluence.asResource("wind_speed");
    public static final PortStreamCodec<ByteBuf, WindSpeedPacketS2C> STREAM_CODEC = PortStreamCodec.composite(
            PortByteBufCodecs.FLOAT, WindSpeedPacketS2C::x,
            PortByteBufCodecs.FLOAT, WindSpeedPacketS2C::z,
            WindSpeedPacketS2C::new
    );

    @Override
    public ResourceLocation identifier() {
        return ID;
    }

    @Override
    public void work(Player player) {
        WeatherHandler.handleWindSpeed(this);
    }

    public static void sendToAll(float x, float z) {
        if (net.minecraftforge.server.ServerLifecycleHooks.getCurrentServer() != null) {
            Confluence.NETWORK_HANDLER.sendToAllPlayers(new WindSpeedPacketS2C(x, z));
        }
    }

    public static void sendToClient(ServerPlayer serverPlayer, float windSpeedX, float windSpeedZ) {
        Confluence.NETWORK_HANDLER.sendToPlayer(serverPlayer, new WindSpeedPacketS2C(windSpeedX, windSpeedZ));
    }
}
