package org.confluence.mod.network.s2c;

import io.netty.buffer.ByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;
import org.confluence.mod.api.event.ThunderstrikeVisualEvent;
import org.mesdag.portlib.event.PortEventHandler;
import org.mesdag.portlib.network.IPortPacket;
import org.mesdag.portlib.network.codec.PortStreamCodec;

/** Coordinates are only broadcast inside the originating dimension. */
public record ThunderstrikePacketS2C(Vec3 start, Vec3 end, long seed, Vec3 spreadAxis) implements IPortPacket.S2C {
    public static final ResourceLocation ID = Confluence.asResource("thunderstrike");
    public static final PortStreamCodec<ByteBuf, ThunderstrikePacketS2C> STREAM_CODEC = new PortStreamCodec<>() {
        public ThunderstrikePacketS2C decode(ByteBuf b) {
            return new ThunderstrikePacketS2C(new Vec3(b.readDouble(), b.readDouble(), b.readDouble()),
                    new Vec3(b.readDouble(), b.readDouble(), b.readDouble()), b.readLong(),
                    new Vec3(b.readDouble(), b.readDouble(), b.readDouble()));
        }
        public void encode(ByteBuf b, ThunderstrikePacketS2C p) {
            b.writeDouble(p.start.x); b.writeDouble(p.start.y); b.writeDouble(p.start.z);
            b.writeDouble(p.end.x); b.writeDouble(p.end.y); b.writeDouble(p.end.z); b.writeLong(p.seed);
            b.writeDouble(p.spreadAxis.x); b.writeDouble(p.spreadAxis.y); b.writeDouble(p.spreadAxis.z);
        }
    };
    @Override public ResourceLocation identifier() { return ID; }
    @Override public void work(Player player) { PortEventHandler.postEvent(new ThunderstrikeVisualEvent(this)); }
}
