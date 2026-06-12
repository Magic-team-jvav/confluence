package org.confluence.mod.network.s2c;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.data.saved.KillBoard;
import org.mesdag.portlib.network.IPortPacket;
import org.mesdag.portlib.network.PortRegistryFriendlyByteBuf;
import org.mesdag.portlib.network.codec.PortStreamCodec;

public enum KillBoardSyncPacketS2C implements IPortPacket.S2C {
    INSTANCE;
    public static final ResourceLocation ID = Confluence.asResource("kill_board_sync");
    public static final PortStreamCodec<PortRegistryFriendlyByteBuf, KillBoardSyncPacketS2C> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public KillBoardSyncPacketS2C decode(PortRegistryFriendlyByteBuf buffer) {
            KillBoard.INSTANCE.networkDecode(buffer);
            return INSTANCE;
        }

        @Override
        public void encode(PortRegistryFriendlyByteBuf buffer, KillBoardSyncPacketS2C value) {
            KillBoard.INSTANCE.networkEncode(buffer);
        }
    };

    KillBoardSyncPacketS2C() {}

    @Override
    public void work(Player player) {}

    @Override
    public ResourceLocation identifier() {
        return ID;
    }

    public static void sendToAll() {
        if (net.minecraftforge.server.ServerLifecycleHooks.getCurrentServer() != null) {
            Confluence.NETWORK_HANDLER.sendToAllPlayers(INSTANCE);
        }
    }

    public static void sendToClient(ServerPlayer serverPlayer) {
        Confluence.NETWORK_HANDLER.sendToPlayer(INSTANCE);
    }
}
