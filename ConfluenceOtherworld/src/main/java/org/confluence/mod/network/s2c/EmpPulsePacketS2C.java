package org.confluence.mod.network.s2c;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.network.IPacketS2C;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.effect.EmpPulseRenderer;

public record EmpPulsePacketS2C(double x, double y, double z, float radius) implements IPacketS2C {
    public static final Type<EmpPulsePacketS2C> TYPE = Confluence.createType("emp_pulse");
    public static final StreamCodec<ByteBuf, EmpPulsePacketS2C> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.DOUBLE, EmpPulsePacketS2C::x,
            ByteBufCodecs.DOUBLE, EmpPulsePacketS2C::y,
            ByteBufCodecs.DOUBLE, EmpPulsePacketS2C::z,
            ByteBufCodecs.FLOAT, EmpPulsePacketS2C::radius,
            EmpPulsePacketS2C::new
    );

    @Override
    public Type<EmpPulsePacketS2C> type() {
        return TYPE;
    }

    @Override
    public void work(Player player) {
        EmpPulseRenderer.show(new Vec3(x, y, z), radius, player.level().getGameTime());
    }
}
