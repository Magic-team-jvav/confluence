package org.confluence.mod.network.s2c;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.data.AchievementOffset;
import org.confluence.mod.common.data.AchievementOffsetLoader;

import java.util.HashMap;
import java.util.Map;

public record AchievementOffsetSyncPacketS2C(Map<ResourceLocation, AchievementOffset> value) implements CustomPacketPayload {
    public static final Type<AchievementOffsetSyncPacketS2C> TYPE = new Type<>(Confluence.asResource("achievement_offset_sync"));
    public static final StreamCodec<RegistryFriendlyByteBuf, AchievementOffsetSyncPacketS2C> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public AchievementOffsetSyncPacketS2C decode(RegistryFriendlyByteBuf buffer) {
            Map<ResourceLocation, AchievementOffset> map = new HashMap<>();
            int size = buffer.readVarInt();
            for (int i = 0; i < size; i++) {
                ResourceLocation key = ResourceLocation.STREAM_CODEC.decode(buffer);
                AchievementOffset value = AchievementOffset.STREAM_CODEC.decode(buffer);
                map.put(key, value);
            }
            return new AchievementOffsetSyncPacketS2C(map);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buffer, AchievementOffsetSyncPacketS2C value) {
            buffer.writeVarInt(value.value.size());
            for (Map.Entry<ResourceLocation, AchievementOffset> entry : value.value.entrySet()) {
                ResourceLocation.STREAM_CODEC.encode(buffer, entry.getKey());
                AchievementOffset.STREAM_CODEC.encode(buffer, entry.getValue());
            }
        }
    };

    @Override
    public Type<AchievementOffsetSyncPacketS2C> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().isLocalPlayer()) {
                AchievementOffsetLoader.handle(value);
            }
        }).exceptionally(e -> {
            context.disconnect(Component.translatable("neoforge.network.invalid_flow", e.getMessage()));
            return null;
        });
    }

    public static void sendToClient(ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, new AchievementOffsetSyncPacketS2C(AchievementOffsetLoader.getInstance().getRegisteredAchievements()));
    }
}
