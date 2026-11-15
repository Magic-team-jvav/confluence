package org.confluence.mod.network.c2s;

import io.netty.buffer.ByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.item.mana.ThunderstrikeItem;
import org.confluence.lib.network.IPacketC2S;
import net.minecraft.network.codec.StreamCodec;

public record ThunderstrikeRangePacketC2S(double range) implements IPacketC2S {
    public static final Type<ThunderstrikeRangePacketC2S> TYPE = Confluence.createType("thunderstrike_range");
    public static final StreamCodec<ByteBuf, ThunderstrikeRangePacketC2S> STREAM_CODEC = new StreamCodec<>() {
        public ThunderstrikeRangePacketC2S decode(ByteBuf b) { return new ThunderstrikeRangePacketC2S(b.readDouble()); }
        public void encode(ByteBuf b, ThunderstrikeRangePacketC2S p) { b.writeDouble(p.range); }
    };
    @Override public Type<ThunderstrikeRangePacketC2S> type() { return TYPE; }
    @Override public void work(ServerPlayer player) {
        if (Double.isFinite(range) && player.isAlive() && ThunderstrikeItem.held(player)) {
            player.getPersistentData().putDouble(ThunderstrikeItem.RANGE_KEY,
                    Mth.clamp(range, 1, ThunderstrikeItem.maxRange(player)));
        }
    }
}
