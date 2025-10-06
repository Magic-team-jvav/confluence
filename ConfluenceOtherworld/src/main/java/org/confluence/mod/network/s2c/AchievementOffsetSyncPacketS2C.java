package org.confluence.mod.network.s2c;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
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
import java.util.function.IntFunction;

public record AchievementOffsetSyncPacketS2C(Map<ResourceLocation, AchievementOffset> value) implements CustomPacketPayload {
    public static final Type<AchievementOffsetSyncPacketS2C> TYPE = new Type<>(Confluence.asResource("achievement_offset_sync"));
    public static final StreamCodec<ByteBuf, AchievementOffsetSyncPacketS2C> STREAM_CODEC = ByteBufCodecs
            .map((IntFunction<Map<ResourceLocation, AchievementOffset>>) HashMap::new, ResourceLocation.STREAM_CODEC, AchievementOffset.STREAM_CODEC)
            .map(AchievementOffsetSyncPacketS2C::new, AchievementOffsetSyncPacketS2C::value);

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
