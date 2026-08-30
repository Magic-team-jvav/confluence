package org.confluence.mod.network.s2c;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.confluence.lib.network.IPacketS2C;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.data.saved.KillBoard;

public enum KillBoardSyncPacketS2C implements IPacketS2C {
    INSTANCE;
    public static final Type<KillBoardSyncPacketS2C> TYPE = Confluence.createType("kill_board_sync");
    public static final StreamCodec<RegistryFriendlyByteBuf, KillBoardSyncPacketS2C> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public KillBoardSyncPacketS2C decode(RegistryFriendlyByteBuf buffer) {
            KillBoard.INSTANCE.networkDecode(buffer);
            return INSTANCE;
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buffer, KillBoardSyncPacketS2C value) {
            KillBoard.INSTANCE.networkEncode(buffer);
        }
    };

    private KillBoardSyncPacketS2C() {}

    @Override
    public void work(Player player) {}

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
