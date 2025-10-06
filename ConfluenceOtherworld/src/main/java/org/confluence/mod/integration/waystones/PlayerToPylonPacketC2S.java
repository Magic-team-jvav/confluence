package org.confluence.mod.integration.waystones;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.confluence.lib.util.LibStreamCodecUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.CommonConfigs;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record PlayerToPylonPacketC2S(UUID uuid) implements CustomPacketPayload {
    public static final Type<PlayerToPylonPacketC2S> TYPE = new Type<>(Confluence.asResource("player_to_pylon"));
    public static final StreamCodec<? super FriendlyByteBuf, PlayerToPylonPacketC2S> STREAM_CODEC = LibStreamCodecUtils.UUID.map(PlayerToPylonPacketC2S::new, PlayerToPylonPacketC2S::uuid);

    @Override
    public @NotNull Type<PlayerToPylonPacketC2S> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer serverPlayer && CommonConfigs.XAEROS_MAP_PYLON_WAYPOINT.get()) {
                WaystonesHelper.handle(serverPlayer, uuid);
            }
        }).exceptionally(e -> {
            context.disconnect(Component.translatable("neoforge.network.invalid_flow", e.getMessage()));
            return null;
        });
    }
}
