package org.confluence.mod.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.confluence.mod.Confluence;

public interface IPacket extends CustomPacketPayload {
    static <P extends IPacket> Type<P> createType(String id) {
        return new Type<>(Confluence.asResource(id));
    }

    default void handle(IPayloadContext context) {
        context.enqueueWork(() -> work(context)).exceptionally(e -> null);
    }

    void work(IPayloadContext context);
}
