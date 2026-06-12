package org.confluence.mod.network.s2c;

import io.netty.buffer.ByteBuf;
import org.mesdag.portlib.network.codec.PortByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.handler.ClientPacketHandler;
import org.mesdag.portlib.network.IPortPacket;
import org.mesdag.portlib.network.codec.PortStreamCodec;

public record ManaPacketS2C(int maxMana, float currentMana) implements IPortPacket.S2C {
    public static final ResourceLocation ID = Confluence.asResource("mana");
    public static final PortStreamCodec<ByteBuf, ManaPacketS2C> STREAM_CODEC = PortStreamCodec.composite(
            PortByteBufCodecs.VAR_INT, ManaPacketS2C::maxMana,
            PortByteBufCodecs.FLOAT, ManaPacketS2C::currentMana,
            ManaPacketS2C::new
    );

    @Override
    public ResourceLocation identifier() {
        return ID;
    }

    @Override
    public void work(Player player) {
        ClientPacketHandler.handleMana(this, player);
    }
}
