package org.confluence.mod.network.s2c;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import org.confluence.lib.network.IPacketS2C;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.effect.ThunderstrikeVfx;
import org.joml.Vector3f;

/// Coordinates are only broadcast inside the originating dimension.
public record ThunderstrikePacketS2C(
        Vector3f start,
        Vector3f end,
        long seed,
        Vector3f spreadAxis
) implements IPacketS2C {
    public static final Type<ThunderstrikePacketS2C> TYPE = Confluence.createType("thunderstrike");
    public static final StreamCodec<ByteBuf, ThunderstrikePacketS2C> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VECTOR3F, ThunderstrikePacketS2C::start,
            ByteBufCodecs.VECTOR3F, ThunderstrikePacketS2C::end,
            ByteBufCodecs.VAR_LONG, ThunderstrikePacketS2C::seed,
            ByteBufCodecs.VECTOR3F, ThunderstrikePacketS2C::spreadAxis,
            ThunderstrikePacketS2C::new
    );

    @Override
    public Type<ThunderstrikePacketS2C> type() {
        return TYPE;
    }

    @Override
    public void work(Player player) {
        ThunderstrikeVfx.receive(this);
    }
}
