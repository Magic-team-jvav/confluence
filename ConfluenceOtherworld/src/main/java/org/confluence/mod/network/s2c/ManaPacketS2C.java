package org.confluence.mod.network.s2c;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import org.confluence.mod.client.handler.ClientPacketHandler;
import org.confluence.mod.network.IPacket;

public record ManaPacketS2C(int maxMana, float currentMana) implements IPacketS2C {
    public static final Type<ManaPacketS2C> TYPE = IPacket.createType("mana");
    public static final StreamCodec<ByteBuf, ManaPacketS2C> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ManaPacketS2C::maxMana,
            ByteBufCodecs.FLOAT, ManaPacketS2C::currentMana,
            ManaPacketS2C::new
    );

    @Override
    public Type<ManaPacketS2C> type() {
        return TYPE;
    }

    @Override
    public void work(Player player) {
        ClientPacketHandler.handleMana(this, player);
    }
}
