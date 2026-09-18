package org.confluence.mod.network.c2s;

import io.netty.buffer.ByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.item.whip.WhipSession;
import org.mesdag.portlib.network.IPortPacket;
import org.mesdag.portlib.network.PortPacketDistributor;
import org.mesdag.portlib.network.codec.PortByteBufCodecs;
import org.mesdag.portlib.network.codec.PortStreamCodec;

public record WhipControlPacketC2S(boolean pressed) implements IPortPacket.C2S {
    public static final ResourceLocation ID = Confluence.asResource("whip_control");
    public static final PortStreamCodec<ByteBuf, WhipControlPacketC2S> STREAM_CODEC = PortByteBufCodecs.BOOL.map(WhipControlPacketC2S::new, WhipControlPacketC2S::pressed);

    @Override
    public ResourceLocation identifier() {return ID;}

    @Override
    public void work(ServerPlayer player) {
        WhipSession.setHeld(player, pressed);
    }

    public static void send(boolean pressed) {
        PortPacketDistributor.sendToServer(new WhipControlPacketC2S(pressed));
    }
}
