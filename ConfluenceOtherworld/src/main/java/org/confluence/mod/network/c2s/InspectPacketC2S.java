package org.confluence.mod.network.c2s;

import io.netty.buffer.ByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.item.gun.BaseGun;
import org.mesdag.portlib.network.IPortPacket;
import org.mesdag.portlib.network.codec.PortStreamCodec;

public enum InspectPacketC2S implements IPortPacket.C2S {
    INSTANCE;

    public static final ResourceLocation ID = Confluence.asResource("inspect_gun");
    public static final PortStreamCodec<ByteBuf, InspectPacketC2S> STREAM_CODEC = PortStreamCodec.unit(INSTANCE);

    @Override
    public ResourceLocation identifier() {
        return ID;
    }

    @Override
    public void handle(IPortPacket.Context context) {
        if (context.player() instanceof ServerPlayer player) {
            context.enqueueWork(() -> work(player));
        }
    }

    @Override
    public void work(ServerPlayer player) {
        if (!player.isSpectator() && player.getMainHandItem().getItem() instanceof BaseGun gun) {
            gun.inspectAnimator(player.getMainHandItem(), player);
        }
    }

    public static void sendToServer() {
        Confluence.NETWORK_HANDLER.sendToServer(INSTANCE);
    }
}
