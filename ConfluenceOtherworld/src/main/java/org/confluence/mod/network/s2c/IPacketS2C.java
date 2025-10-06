package org.confluence.mod.network.s2c;

import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.confluence.mod.network.IPacket;

public interface IPacketS2C extends IPacket {
    @Override
    default void work(IPayloadContext context) {
        if (context.player().isLocalPlayer()) {
            work(context.player());
        }
    }

    default void work(Player player) {}
}
