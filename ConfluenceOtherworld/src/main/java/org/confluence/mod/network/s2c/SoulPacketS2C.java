package org.confluence.mod.network.s2c;

import io.netty.buffer.ByteBuf;
import org.mesdag.portlib.network.codec.PortByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.handler.ClientPacketHandler;
import org.mesdag.portlib.network.IPortPacket;
import org.mesdag.portlib.network.codec.PortStreamCodec;

public record SoulPacketS2C(int maxSoul, float currentSoul,
                            boolean fallenSoulCoreActive) implements IPortPacket.S2C {
    public static final ResourceLocation ID = Confluence.asResource("soul");
    public static final PortStreamCodec<ByteBuf, SoulPacketS2C> STREAM_CODEC = PortStreamCodec.composite(
            PortByteBufCodecs.VAR_INT, SoulPacketS2C::maxSoul,
            PortByteBufCodecs.FLOAT, SoulPacketS2C::currentSoul,
            PortByteBufCodecs.BOOL, SoulPacketS2C::fallenSoulCoreActive,
            SoulPacketS2C::new
    );

    @Override
    public ResourceLocation identifier() {
        return ID;
    }

    @Override
    public void work(Player player) {
        ClientPacketHandler.handleSoul(this, player);
    }
}
