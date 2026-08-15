package org.confluence.mod.network.s2c;

import it.unimi.dsi.fastutil.booleans.BooleanObjectPair;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.handler.ClientPacketHandler;
import org.confluence.mod.common.data.saved.GlobalCloakData;
import org.mesdag.portlib.network.IPortPacket;
import org.mesdag.portlib.network.PortRegistryFriendlyByteBuf;
import org.mesdag.portlib.network.codec.PortStreamCodec;

import java.util.Map;

public record GlobalCloakSyncPacketS2C(
        Map<BlockState, BooleanObjectPair<BlockState>> blocks,
        Map<Item, BooleanObjectPair<Item>> items) implements IPortPacket.S2C {

    public static final ResourceLocation ID = Confluence.asResource("global_cloak_sync");
    public static final PortStreamCodec<PortRegistryFriendlyByteBuf, GlobalCloakSyncPacketS2C> STREAM_CODEC = new PortStreamCodec<>() {
        @Override
        public GlobalCloakSyncPacketS2C decode(PortRegistryFriendlyByteBuf buffer) {
            return new GlobalCloakSyncPacketS2C(
                    GlobalCloakData.BLOCK_MAP_STREAM_CODEC.decode(buffer),
                    GlobalCloakData.ITEM_MAP_STREAM_CODEC.decode(buffer));
        }

        @Override
        public void encode(PortRegistryFriendlyByteBuf buffer, GlobalCloakSyncPacketS2C value) {
            GlobalCloakData.BLOCK_MAP_STREAM_CODEC.encode(buffer, value.blocks);
            GlobalCloakData.ITEM_MAP_STREAM_CODEC.encode(buffer, value.items);
        }
    };

    @Override
    public ResourceLocation identifier() {
        return ID;
    }

    @Override
    public void handle(IPortPacket.Context context) {
        Player player = context.player();
        if (player != null) context.enqueueWork(() -> work(player));
    }

    @Override
    public void work(Player player) {
        GlobalCloakData.INSTANCE.applyNetworkState(blocks, items);
        ClientPacketHandler.handleCloak();
    }

    public static void sendToAll() {
        if (ServerLifecycleHooks.getCurrentServer() != null) {
            Confluence.NETWORK_HANDLER.sendToAllPlayers(current());
        }
    }

    public static void sendToClient(ServerPlayer player) {
        Confluence.NETWORK_HANDLER.sendToPlayer(player, current());
    }

    private static GlobalCloakSyncPacketS2C current() {
        return new GlobalCloakSyncPacketS2C(
                GlobalCloakData.INSTANCE.blockMapSnapshot(),
                GlobalCloakData.INSTANCE.itemMapSnapshot());
    }
}
