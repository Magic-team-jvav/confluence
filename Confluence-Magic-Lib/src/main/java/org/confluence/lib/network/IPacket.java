package org.confluence.lib.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public interface IPacket extends CustomPacketPayload {
    default void handle(IPayloadContext context) {
        context.enqueueWork(() -> work(context)).exceptionally(e -> null);
    }

    default void work(IPayloadContext context) {
        Player player = context.player();
        if (player.isLocalPlayer()) {
            s2c(player);
        } else if (player instanceof ServerPlayer serverPlayer) {
            c2s(serverPlayer);
        }
    }

    default void c2s(ServerPlayer player) {}

    default void s2c(Player player) {}
}
