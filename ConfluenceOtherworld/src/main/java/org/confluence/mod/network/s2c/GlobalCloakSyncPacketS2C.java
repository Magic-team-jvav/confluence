package org.confluence.mod.network.s2c;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.handler.ClientPacketHandler;
import org.confluence.mod.common.data.saved.GlobalCloakData;
import org.mesdag.portlib.network.IPortPacket;
import org.mesdag.portlib.network.PortRegistryFriendlyByteBuf;
import org.mesdag.portlib.network.codec.PortStreamCodec;

public enum GlobalCloakSyncPacketS2C implements IPortPacket.S2C {
    INSTANCE;

    public static final ResourceLocation ID = Confluence.asResource("global_cloak_sync");
    public static final PortStreamCodec<PortRegistryFriendlyByteBuf, GlobalCloakSyncPacketS2C> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public GlobalCloakSyncPacketS2C decode(PortRegistryFriendlyByteBuf buffer) {
            GlobalCloakData.INSTANCE.networkDecode(buffer);
            return INSTANCE;
        }

        @Override
        public void encode(PortRegistryFriendlyByteBuf buffer, GlobalCloakSyncPacketS2C value) {
            GlobalCloakData.INSTANCE.networkEncode(buffer);
        }
    };

    @Override
    public ResourceLocation identifier() {
        return ID;
    }

    @Override
    public void work(Player player) {
        ClientPacketHandler.handleCloak();
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
