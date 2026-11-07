package org.confluence.mod.network.c2s;

import io.netty.buffer.ByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.item.mana.ThunderstrikeItem;
import org.mesdag.portlib.network.IPortPacket;
import org.mesdag.portlib.network.codec.PortStreamCodec;

public record ThunderstrikeRangePacketC2S(double range) implements IPortPacket.C2S {
    public static final ResourceLocation ID = Confluence.asResource("thunderstrike_range");
    public static final PortStreamCodec<ByteBuf, ThunderstrikeRangePacketC2S> STREAM_CODEC = new PortStreamCodec<>() {
        public ThunderstrikeRangePacketC2S decode(ByteBuf b) { return new ThunderstrikeRangePacketC2S(b.readDouble()); }
        public void encode(ByteBuf b, ThunderstrikeRangePacketC2S p) { b.writeDouble(p.range); }
    };
    @Override public ResourceLocation identifier() { return ID; }
    @Override public void work(ServerPlayer player) {
        if (Double.isFinite(range) && player.isAlive() && ThunderstrikeItem.held(player)) {
            player.getPersistentData().putDouble(ThunderstrikeItem.RANGE_KEY,
                    Mth.clamp(range, 1, ThunderstrikeItem.maxRange(player)));
        }
    }
}
