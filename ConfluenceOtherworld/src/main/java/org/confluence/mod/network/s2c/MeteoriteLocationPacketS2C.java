package org.confluence.mod.network.s2c;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.handler.MeteorLandingHandler;

public record MeteoriteLocationPacketS2C(BlockPos location, int tickUntilLanding) implements CustomPacketPayload {
    public static final Type<MeteoriteLocationPacketS2C> TYPE = new Type<>(Confluence.asResource("meteorite_location"));
    public static final StreamCodec<ByteBuf, MeteoriteLocationPacketS2C> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, p -> p.location,
            ByteBufCodecs.INT, p -> p.tickUntilLanding,
            MeteoriteLocationPacketS2C::new
    );

    @Override
    public Type<MeteoriteLocationPacketS2C> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (player.isLocalPlayer()) {
                MeteorLandingHandler.handlePacket(this, player);
            }
        }).exceptionally(e -> {
            context.disconnect(Component.translatable("neoforge.network.invalid_flow", e.getMessage()));
            return null;
        });
    }

    /**
     * @param location         陨石坐标，等于[0, 0, 0]是无效的
     * @param tickUntilLanding 落地时间，小于等于0将只刷新指南针
     */
    public static void sendToAll(BlockPos location, int tickUntilLanding) {
        if (ServerLifecycleHooks.getCurrentServer() != null && !BlockPos.ZERO.equals(location)) {
            PacketDistributor.sendToAllPlayers(new MeteoriteLocationPacketS2C(location, tickUntilLanding));
        }
    }
}
