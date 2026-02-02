package org.confluence.lib.network;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public interface IPacketC2S extends IPacket {
    @Override
    default void work(IPayloadContext context) {
        if (context.player() instanceof ServerPlayer player) {
            work(player);
        }
    }

    @Override
    default void c2s(ServerPlayer player) {
        work(player);
    }

    void work(ServerPlayer player);
}
