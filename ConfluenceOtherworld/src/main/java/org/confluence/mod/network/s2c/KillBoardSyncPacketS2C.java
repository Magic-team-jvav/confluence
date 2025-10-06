package org.confluence.mod.network.s2c;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.confluence.mod.common.data.saved.KillBoard;
import org.confluence.mod.network.IPacket;

public final class KillBoardSyncPacketS2C implements IPacketS2C {
    public static final Type<KillBoardSyncPacketS2C> TYPE = IPacket.createType("kill_board_sync");
    public static final KillBoardSyncPacketS2C INSTANCE = new KillBoardSyncPacketS2C();
    public static final StreamCodec<ByteBuf, KillBoardSyncPacketS2C> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public KillBoardSyncPacketS2C decode(ByteBuf buffer) {
            KillBoard.INSTANCE.networkDecode(buffer);
            return INSTANCE;
        }

        @Override
        public void encode(ByteBuf buffer, KillBoardSyncPacketS2C value) {
            KillBoard.INSTANCE.networkEncode(buffer);
        }
    };

    private KillBoardSyncPacketS2C() {}

    @Override
    public Type<KillBoardSyncPacketS2C> type() {
        return TYPE;
    }

    public static void sendToAll() {
        if (ServerLifecycleHooks.getCurrentServer() != null) {
            PacketDistributor.sendToAllPlayers(INSTANCE);
        }
    }

    public static void sendToClient(ServerPlayer serverPlayer) {
        PacketDistributor.sendToPlayer(serverPlayer, INSTANCE);
    }
}
