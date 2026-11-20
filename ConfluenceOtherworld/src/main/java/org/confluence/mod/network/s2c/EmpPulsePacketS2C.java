package org.confluence.mod.network.s2c;

import io.netty.buffer.ByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.effect.EmpPulseRenderer;
import org.mesdag.portlib.network.IPortPacket;
import org.mesdag.portlib.network.codec.PortByteBufCodecs;
import org.mesdag.portlib.network.codec.PortStreamCodec;

public record EmpPulsePacketS2C(double x, double y,
                                double z, float radius) implements IPortPacket.S2C {
    public static final ResourceLocation ID = Confluence.asResource("emp_pulse");
    public static final PortStreamCodec<ByteBuf, EmpPulsePacketS2C> STREAM_CODEC = PortStreamCodec.composite(
            PortByteBufCodecs.DOUBLE, EmpPulsePacketS2C::x,
            PortByteBufCodecs.DOUBLE, EmpPulsePacketS2C::y,
            PortByteBufCodecs.DOUBLE, EmpPulsePacketS2C::z,
            PortByteBufCodecs.FLOAT, EmpPulsePacketS2C::radius,
            EmpPulsePacketS2C::new
    );

    @Override
    public ResourceLocation identifier() {
        return ID;
    }

    @Override
    public void work(Player player) {
        EmpPulseRenderer.show(new Vec3(x, y, z), radius, player.level().getGameTime());
    }
}
