package org.confluence.mod.network.s2c;

import io.netty.buffer.ByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.attachment.ManaStorage;
import org.mesdag.portlib.network.IPortPacket;
import org.mesdag.portlib.network.codec.PortByteBufCodecs;
import org.mesdag.portlib.network.codec.PortStreamCodec;

public record ManaPacketS2C(float currentMana) implements IPortPacket.S2C {
    public static final ResourceLocation ID = Confluence.asResource("mana");
    public static final PortStreamCodec<ByteBuf, ManaPacketS2C> STREAM_CODEC = PortByteBufCodecs.FLOAT.map(ManaPacketS2C::new, ManaPacketS2C::currentMana);

    @Override
    public ResourceLocation identifier() {
        return ID;
    }

    @Override
    public void work(Player player) {
        ManaStorage.of(player).setCurrentMana(currentMana);
    }
}
