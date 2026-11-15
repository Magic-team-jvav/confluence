package org.confluence.mod.network.s2c;

import io.netty.buffer.ByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.effect.ThunderstrikeVfx;
import org.joml.Vector3f;
import org.mesdag.portlib.network.IPortPacket;
import org.mesdag.portlib.network.codec.PortByteBufCodecs;
import org.mesdag.portlib.network.codec.PortStreamCodec;

/// Coordinates are only broadcast inside the originating dimension.
public record ThunderstrikePacketS2C(
        Vector3f start,
        Vector3f end,
        long seed,
        Vector3f spreadAxis
) implements IPortPacket.S2C {
    public static final ResourceLocation ID = Confluence.asResource("thunderstrike");
    public static final PortStreamCodec<ByteBuf, ThunderstrikePacketS2C> STREAM_CODEC = PortStreamCodec.composite(
            PortByteBufCodecs.VECTOR3F, ThunderstrikePacketS2C::start,
            PortByteBufCodecs.VECTOR3F, ThunderstrikePacketS2C::end,
            PortByteBufCodecs.VAR_LONG, ThunderstrikePacketS2C::seed,
            PortByteBufCodecs.VECTOR3F, ThunderstrikePacketS2C::spreadAxis,
            ThunderstrikePacketS2C::new
    );

    @Override
    public ResourceLocation identifier() {
        return ID;
    }

    @Override
    public void work(Player player) {
        ThunderstrikeVfx.receive(this);
    }
}
