package org.confluence.lib.network;

import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public interface IPacketS2C extends IPacket {
    @Override
    default void work(IPayloadContext context) {
        if (context.player().isLocalPlayer()) {
            work(context.player());
        }
    }

    @Override
    default void s2c(Player player) {
        work(player);
    }

    void work(Player player);
}
