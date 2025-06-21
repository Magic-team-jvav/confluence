package org.confluence.mod.network.s2c;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.handler.ClientPacketHandler;
import org.confluence.mod.common.data.saved.GamePhase;

public record GamePhasePacketS2C(GamePhase gamePhase) implements CustomPacketPayload {
    public static final Type<GamePhasePacketS2C> TYPE = new Type<>(Confluence.asResource("game_phase"));
    public static final StreamCodec<ByteBuf, GamePhasePacketS2C> STREAM_CODEC = GamePhase.STREAM_CODEC.map(GamePhasePacketS2C::new, GamePhasePacketS2C::gamePhase);

    @Override
    public Type<GamePhasePacketS2C> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().isLocalPlayer()) {
                ClientPacketHandler.handleGamePhase(this);
            }
        }).exceptionally(e -> {
            context.disconnect(Component.translatable("neoforge.network.invalid_flow", e.getMessage()));
            return null;
        });
    }

    public static void sendToAll(GamePhase gamePhase) {
        if (ServerLifecycleHooks.getCurrentServer() != null) {
            PacketDistributor.sendToAllPlayers(new GamePhasePacketS2C(gamePhase));
        }
    }

    public static void sendToClient(ServerPlayer serverPlayer, GamePhase gamePhase) {
        PacketDistributor.sendToPlayer(serverPlayer, new GamePhasePacketS2C(gamePhase));
    }
}
