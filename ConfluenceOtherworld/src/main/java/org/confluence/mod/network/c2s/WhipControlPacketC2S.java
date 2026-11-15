package org.confluence.mod.network.c2s;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.lib.network.IPacketC2S;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.item.whip.WhipSession;

public record WhipControlPacketC2S(boolean pressed) implements IPacketC2S {
    public static final Type<WhipControlPacketC2S> TYPE = Confluence.createType("whip_control");
    public static final StreamCodec<ByteBuf, WhipControlPacketC2S> STREAM_CODEC = ByteBufCodecs.BOOL.map(WhipControlPacketC2S::new, WhipControlPacketC2S::pressed);

    @Override
    public Type<WhipControlPacketC2S> type() {return TYPE;}

    @Override
    public void work(ServerPlayer player) {
        WhipSession.setHeld(player, pressed);
    }

    public static void send(boolean pressed) {
        PacketDistributor.sendToServer(new WhipControlPacketC2S(pressed));
    }
}
