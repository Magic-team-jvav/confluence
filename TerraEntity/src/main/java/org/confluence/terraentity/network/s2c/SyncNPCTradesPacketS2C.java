package org.confluence.terraentity.network.s2c;

import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.entity.npc.trade.NPCTradeManager;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public record SyncNPCTradesPacketS2C(Map<ResourceLocation, Tag> tradesMap) implements CustomPacketPayload {
    public static final Type<SyncNPCTradesPacketS2C> TYPE = new Type<>(TerraEntity.space("npc_trades_packet_s2c"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncNPCTradesPacketS2C> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public SyncNPCTradesPacketS2C decode(RegistryFriendlyByteBuf buffer) {
            Map<ResourceLocation, Tag> map = new HashMap<>();
            int size = buffer.readVarInt();
            for (int i = 0; i < size; i++) {
                map.put(buffer.readResourceLocation(), buffer.readNbt(NbtAccounter.unlimitedHeap()));
            }
            return new SyncNPCTradesPacketS2C(map);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buffer, SyncNPCTradesPacketS2C value) {
            buffer.writeVarInt(value.tradesMap.size());
            for (Map.Entry<ResourceLocation, Tag> entry : value.tradesMap.entrySet()) {
                buffer.writeResourceLocation(entry.getKey());
                buffer.writeNbt(entry.getValue());
            }
        }
    };

    @Override
    public @NotNull Type<SyncNPCTradesPacketS2C> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            NPCTradeManager.Loader.getInstance().syncFromServer(context.player().registryAccess(), tradesMap);
        }).exceptionally(e -> null);
    }

    public static void sync(ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, new SyncNPCTradesPacketS2C(NPCTradeManager.Loader.getInstance().getTagMap()));
    }
}
