package org.confluence.mod.network.c2s;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.confluence.mod.network.IPacket;

public interface IPacketC2S extends IPacket {
    @Override
    default void work(IPayloadContext context) {
        if (context.player() instanceof ServerPlayer player) {
            work(player);
        }
    }

    default void work(ServerPlayer player) {}
}
