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
import org.confluence.mod.client.handler.ClientPacketHandler;
import org.confluence.mod.common.data.saved.GamePhase;
import org.confluence.mod.common.data.saved.KillBoard;

public record KillBoardSyncPacketS2C(GamePhase gamePhase, int defeatedBoss) implements CustomPacketPayload {
    public static final Type<KillBoardSyncPacketS2C> TYPE = new Type<>(Confluence.asResource("kill_board_sync"));
    public static final StreamCodec<ByteBuf, KillBoardSyncPacketS2C> STREAM_CODEC = StreamCodec.composite(
            GamePhase.STREAM_CODEC, KillBoardSyncPacketS2C::gamePhase,
            ByteBufCodecs.VAR_INT, KillBoardSyncPacketS2C::defeatedBoss,
            KillBoardSyncPacketS2C::new
    );

    @Override
    public Type<KillBoardSyncPacketS2C> type() {
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

    public static void sendToAll() {
        if (ServerLifecycleHooks.getCurrentServer() != null) {
            PacketDistributor.sendToAllPlayers(new KillBoardSyncPacketS2C(
                    KillBoard.INSTANCE.getGamePhase(),
                    KillBoard.INSTANCE.getDefeatedBoss()
            ));
        }
    }

    public static void sendToClient(ServerPlayer serverPlayer) {
        PacketDistributor.sendToPlayer(serverPlayer, new KillBoardSyncPacketS2C(
                KillBoard.INSTANCE.getGamePhase(),
                KillBoard.INSTANCE.getDefeatedBoss()
        ));
    }
}
