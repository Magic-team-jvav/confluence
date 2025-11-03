package org.confluence.mod.network.s2c;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.confluence.mod.client.handler.ClientPacketHandler;
import org.confluence.mod.common.data.saved.GlobalCloakData;
import org.confluence.mod.network.IPacket;

public final class GlobalCloakSyncPacketS2C implements IPacketS2C {
    public static final Type<GlobalCloakSyncPacketS2C> TYPE = IPacket.createType("global_cloak_sync");
    private static final GlobalCloakSyncPacketS2C INSTANCE = new GlobalCloakSyncPacketS2C();
    public static final StreamCodec<RegistryFriendlyByteBuf, GlobalCloakSyncPacketS2C> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public GlobalCloakSyncPacketS2C decode(RegistryFriendlyByteBuf buffer) {
            GlobalCloakData.INSTANCE.networkDecode(buffer);
            return INSTANCE;
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buffer, GlobalCloakSyncPacketS2C value) {
            GlobalCloakData.INSTANCE.networkEncode(buffer);
        }
    };

    @Override
    public Type<GlobalCloakSyncPacketS2C> type() {
        return TYPE;
    }

    @Override
    public void work(Player player) {
        ClientPacketHandler.handleCloak();
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
